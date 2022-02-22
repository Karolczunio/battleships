package player;

import boards.Board;
import boards.Part;
import navigation.BoundVector;
import navigation.Position;

public class Helper {
    public static Position getPosition(String positionString) {
        if (positionString == null) {
            throw new IllegalArgumentException("Position string cannot be null");
        }
        if (!positionString.matches("[a-j]([1-9]|10)")) {
            throw new IllegalArgumentException("Position string is incorrect");
        }
        int x = positionString.charAt(0) - 'a';
        int y = Integer.parseInt(positionString.substring(1)) - 1;
        return new Position(x, y);
    }

    public static BoundVector getBoundVector(String boundVectorString) {
        if (boundVectorString == null) {
            throw new IllegalArgumentException("Bound vector string cannot be null");
        }
        if (!boundVectorString.matches("([a-j]([1-9]|10)){2}")) {
            throw new IllegalArgumentException("Bound vector string is incorrect");
        }
        String[] tokens = boundVectorString.split("(?=[a-j])");
        return new BoundVector(getPosition(tokens[0]), getPosition(tokens[1]));
    }

    public static boolean isValidShipPlacement(int shipSize, BoundVector placement, Board placementBoard) {
        if (placement.getFreeVector().distance() != 0 && !placement.getFreeVector().isOrthogonal()) {
            return false;
        }
        if (placement.getFreeVector().distance() + 1 != shipSize) {
            return false;
        }
        Position[] path = placement.getStandardPath();
        Position[] neighbourhood = placement.getNeighbourhoodOfStandardPath();
        for (Position position : path) {
            if (!placementBoard.isValidPosition(position) || placementBoard.getElementByPosition(position).part() != Part.EMPTY) {
                return false;
            }
        }
        for (Position position : neighbourhood) {
            if (placementBoard.isValidPosition(position) && placementBoard.getElementByPosition(position).part() != Part.EMPTY) {
                return false;
            }
        }
        return true;
    }
}
