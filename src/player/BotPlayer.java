package player;

import boards.Board;
import boards.BoardElement;
import boards.Part;
import navigation.BoundVector;
import navigation.Position;

import java.util.Random;
import java.util.Scanner;

public class BotPlayer extends Player {

    public BotPlayer(String name) {
        super(name);
    }

    @Override
    public BoundVector supplyShipPlacement(int size, Board target) {
        BoundVector boundVector;
        do {
            boundVector = BoundVector.getRandomOrthogonalBoundVector(0, 9, 0, 9, size);
        } while (!Helper.isValidShipPlacement(size, boundVector, target));
        return boundVector;
    }

    @Override
    public Position supplyShotPosition() {
        Random random = new Random();
        Position[] hidden = shotsBoard.getPositionsOfAllElementsSatisfying(element -> !element.isVisible());
        if (hidden.length == 0) {
            return shotsBoard.getAllValidPositions()[0];
        }
        return hidden[random.nextInt(hidden.length)];
    }

    @Override
    public void confirmShot(ShotResult result, Position positionOfShot) {

    }

    @Override
    public void confirmDamage(ShotResult result, Position positionOfShot) {

    }


}
