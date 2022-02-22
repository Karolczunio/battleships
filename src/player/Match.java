package player;

import boards.Board;
import boards.BoardElement;
import boards.Part;
import navigation.BoundVector;
import navigation.Position;

public class Match {
    private Player currentPlayer;
    private Player anotherPlayer;
    //every number in ships array is a size of a ship that should be placed
    private final static int[] numberOfTiles = {5, 1, 1, 1, 3, 3, 4};
    private final static int[] shipIds = {1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6};

    private Match(Player player1, Player player2) {
        this.currentPlayer = player1;
        this.anotherPlayer = player2;
    }

    private static Board getBoardWithShipsPlaced(Player player) {
        Board board = Board.getEmptyBoard();
        BoundVector placement;
        for (int i = 0; i < shipIds.length; i++) {
            placement = player.supplyShipPlacement(numberOfTiles[i], board);
            if (!Helper.isValidShipPlacement(numberOfTiles[i], placement, board)) {
                throw new IllegalStateException("Player provided incorrect implementation of supplyShipPlacement method");
            }
            board = getBoardWithShipPlaced(shipIds[i], placement, board);
        }
        return board;
    }

    private static Board getBoardWithShipPlaced(int id, BoundVector placement, Board board) {
        return board
                .getModifiedBoard(element -> element.getWithChangedPart(Part.SHIP).getWithChangedId(id),
                        placement.getStandardPath())
                .getModifiedBoard(element -> element
                                .getWithChangedId(element.id() | id),
                        placement.getNeighbourhoodOfStandardPath());
    }

    private static TheStateOfTheGame getStatus(Player player) {
        for (int id : shipIds) {
            if (!isShipSunk(id, player.placementsBoard)) {
                return TheStateOfTheGame.PLAYING;
            }
        }
        return TheStateOfTheGame.VICTORY;
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
        markPartOfTheShipAsShot(position, currentPlayer);
        destroyPartOfTheShip(position, anotherPlayer);
        int id = boardElement.id();
        if (!isShipSunk(id, anotherPlayer.placementsBoard)) {
            return ShotResult.HIT;
        }
        uncoverSurroundingWaters(id, currentPlayer);
        return ShotResult.SUNK;
    }

    private static boolean isShipSunk(int id, Board board) {
        Position[] shipPositions = board
                .getPositionsOfAllElementsSatisfying(element -> element.id() == id);

        BoardElement currentElement;
        for (Position shipPosition : shipPositions) {

            currentElement = board
                    .getElementByPosition(shipPosition);

            if (currentElement.part() == Part.SHIP) {
                return false;
            }
        }
        return true;
    }

    private void swapPlayers() {
        Player temp = currentPlayer;
        currentPlayer = anotherPlayer;
        anotherPlayer = temp;
    }

    private static void uncoverSurroundingWaters(int id, Player player) {
        Position[] positionsAroundTheShip = player
                .shotsBoard
                .getPositionsOfAllElementsSatisfying(element -> (element.id() & id) != 0);
        player.shotsBoard = player
                .shotsBoard
                .getModifiedBoard(element -> element.getWithChangedVisibility(true), positionsAroundTheShip);
    }

    private static void markPartOfTheShipAsShot(Position position, Player player) {
        player.shotsBoard = player
                .shotsBoard
                .getModifiedBoard(element -> element
                        .getWithChangedPart(Part.WRECK)
                        .getWithChangedVisibility(true), position);
    }

    private static void destroyPartOfTheShip(Position position, Player player) {
        player.placementsBoard = player
                .placementsBoard
                .getModifiedBoard(element -> element.getWithChangedPart(Part.WRECK), position);
    }

    public static Match create(Player player1, Player player2) {
        player1.placementsBoard = getBoardWithShipsPlaced(player1);
        player1.confirmPlacingAllShips();
        player2.placementsBoard = getBoardWithShipsPlaced(player2);
        player2.confirmPlacingAllShips();

        player1.shotsBoard = player2
                .placementsBoard
                .getModifiedBoard(element -> element.getWithChangedVisibility(false));

        player2.shotsBoard = player1
                .placementsBoard
                .getModifiedBoard(element -> element.getWithChangedVisibility(false));

        return new Match(player1, player2);
    }

    public void play() {
        do {
            Position shotPosition = currentPlayer.supplyShotPosition();
            ShotResult result = shoot(shotPosition);
            currentPlayer.confirmShot(result, shotPosition);
            anotherPlayer.confirmDamage(result, shotPosition);
            swapPlayers();
        } while (getStatus(currentPlayer) == TheStateOfTheGame.PLAYING);
        System.out.println("Player " + anotherPlayer.getName() + " won!");
    }
}
