package player;

import boards.Board;
import boards.BoardElement;
import boards.Part;
import navigation.BoundVector;
import navigation.Position;

public class Match {
    private Player currentPlayer;
    private Player anotherPlayer;
    private final static ShipCount[] shipCounts = {
            new ShipCount(5, 1),
            new ShipCount(1, 3),
            new ShipCount(3, 2),
            new ShipCount(4, 1)
    };

    private Match(Player player1, Player player2) {
        this.currentPlayer = player1;
        this.anotherPlayer = player2;
    }

    private static Board getBoardWithShipsPlaced(Player player) {
        Board board = Board.getEmptyBoard();
        BoundVector placement;
        int nr = 0;
        for (ShipCount shipCount : shipCounts) {
            for (int j = 0; j < shipCount.numberOfShips(); j++) {
                placement = player.supplyShipPlacement(shipCount.numberOfTiles(), board);
                //if (!Helper.isValidShipPlacement(shipCount.numberOfTiles(), placement, board)) {
                    //throw new IllegalStateException("Player provided incorrect implementation of supplyShipPlacement method");
                //}
                board = getBoardWithShipPlaced(nr++, placement, board);
                //when using input from the user only the last iteration throws an exception
                //usually there is a different exception every time I run the program
                //sometimes there is no exception
                //when using the same each time automatically I still get different exception
                //everytime I run the program
            }
        }
        return board;
    }

    private static Board getBoardWithShipPlaced(int nr, BoundVector placement, Board board) {
        if (nr < 0) {
            throw new IllegalArgumentException("Ship number needs to be an unsigned integer");
        }
        return board
                .getModifiedBoard(element -> element.getWithChangedPart(Part.SHIP).getWithChangedId(1 << nr),
                        placement.getStandardPath())
                .getModifiedBoard(element -> element
                                .getWithChangedId(element.id() | (1 << nr)),
                        placement.getNeighbourhoodOfStandardPath());
    }

    public static Match create(Player player1, Player player2) {
        player1.placementsBoard = getBoardWithShipsPlaced(player1);
        player2.placementsBoard = getBoardWithShipsPlaced(player2);

        player1.shotsBoard = player2
                .placementsBoard
                .getModifiedBoard(element -> element.getWithChangedVisibility(false));

        player2.shotsBoard = player1
                .placementsBoard
                .getModifiedBoard(element -> element.getWithChangedVisibility(false));

        return new Match(player1, player2);
    }

    private ShotResult shoot(Position position) {
        BoardElement boardElement = currentPlayer
                .shotsBoard
                .getElementByPosition(position);
        if (boardElement.isVisible()) {
            return ShotResult.WASTED;
        }
        if (boardElement.part() == Part.EMPTY) {
            currentPlayer.shotsBoard = currentPlayer
                    .shotsBoard
                    .getModifiedBoard(element -> element
                            .getWithChangedVisibility(true), position);
            return ShotResult.MISS;
        }

        int id = boardElement.id();
        Position[] shipPositions = currentPlayer
                .shotsBoard
                .getPositionsOfAllElementsSatisfying(element -> element.id() == id);

        BoardElement currentElement;
        for (Position shipPosition : shipPositions) {

            currentElement = currentPlayer
                    .shotsBoard
                    .getElementByPosition(shipPosition);

            if (shipPosition != position && currentElement.part() == Part.SHIP) {
                destroyPartOfTheShip(position);
                return ShotResult.HIT;
            }
        }

        destroyPartOfTheShip(position);
        Position[] positionsAroundTheShip = currentPlayer
                .shotsBoard
                .getPositionsOfAllElementsSatisfying(element -> (element.id() & id) != 0);
        uncoverSurroundingWaters(positionsAroundTheShip);
        return ShotResult.SUNK;
    }

    private void swapPlayers() {
        Player temp = currentPlayer;
        currentPlayer = anotherPlayer;
        anotherPlayer = temp;
    }

    private void uncoverSurroundingWaters(Position[] positions) {
        currentPlayer.shotsBoard = currentPlayer
                .shotsBoard
                .getModifiedBoard(element -> element.getWithChangedVisibility(true), positions);
    }

    private void destroyPartOfTheShip(Position position) {
        currentPlayer.shotsBoard = currentPlayer
                .shotsBoard
                .getModifiedBoard(element -> element
                        .getWithChangedPart(Part.WRECK)
                        .getWithChangedVisibility(true), position);
        anotherPlayer.placementsBoard = anotherPlayer
                .placementsBoard
                .getModifiedBoard(element -> element.getWithChangedPart(Part.WRECK));
    }


    private TheStateOfTheGame getStatus() {
        for (int nr = 0; nr < shipCounts.length; nr++) {
            if (!isShipSunk(nr)) {
                return TheStateOfTheGame.PLAYING;
            }
        }
        return TheStateOfTheGame.VICTORY;
    }

    private boolean isShipSunk(int nr) {
        if (nr < 0) {
            throw new IllegalArgumentException("Ship number needs to be an unsigned integer");
        }
        int id = 1 << nr;
        Position[] shipPositions = currentPlayer
                .placementsBoard
                .getPositionsOfAllElementsSatisfying(element -> element.id() == id);
        BoardElement currentElement;
        for (Position shipPosition : shipPositions) {
            currentElement = currentPlayer.placementsBoard.getElementByPosition(shipPosition);
            if (currentElement.part() != Part.WRECK) {
                return false;
            }
        }
        return true;
    }

    public void play() {
        do {
            Position shotPosition = currentPlayer.supplyShotPosition();
            ShotResult result = shoot(shotPosition);
            currentPlayer.confirmShot(result, shotPosition);
            anotherPlayer.confirmDamage(result, shotPosition);
            swapPlayers();
        } while (getStatus() == TheStateOfTheGame.PLAYING);
        System.out.println("Player " + anotherPlayer.getName() + " won!");
    }
}
