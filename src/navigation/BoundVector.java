package navigation;

import java.util.Arrays;

public record BoundVector(Position origin, Position destination) {

    /**
     * The method returns FreeVector representing relative displacement of this BoundVector according to X and Y axis
     *
     * @return object of class FreeVector based on this BoundVector
     */
    public FreeVector getFreeVector() {
        return new FreeVector(destination.x() - origin.x(), destination.y() - origin.y());
    }

    /**
     * The method returns all positions between origin and destination if the free vector
     * of this bound vector is orthogonal, diagonal or none, else it throws IllegalStateException
     *
     * @return the array of positions between origin and destination
     */
    public Position[] getStandardPath() {
        FreeVector freeVector = getFreeVector();
        if (freeVector.distance() != 0 && !freeVector.isOrthogonal() && !freeVector.isDiagonal()) {
            throw new IllegalStateException("Free vector of this bound vector is not orthogonal, diagonal or none");
        }
        int length = freeVector.distance() + 1;
        Position[] path = new Position[length];
        Position current = this.origin;
        FreeVector iteration = freeVector.iteration();
        for (int i = 0; i < path.length; i++) {
            path[i] = current;
            current = current.getPositionMovedBy(iteration);
        }
        return path;
    }

    private Position[] removeDuplicates(Position[] array) {
        Position[] result = new Position[array.length];
        int index = 0;
        boolean isDuplicate;
        for (Position position : array) {
            isDuplicate = false;
            for (int j = 0; j < index; j++) {
                if (position.equals(result[j])) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                result[index] = position;
                index++;
            }
        }
        return Arrays.copyOf(result, index);
    }

    private Position[] mergeArrays(Position[] a, Position[] b) {
        Position[] result = new Position[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private Position[] subtractArrays(Position[] arrayToSubtractFrom, Position[] arrayBeingSubtracted) {
        Position[] result = new Position[arrayToSubtractFrom.length];
        int index = 0;
        boolean saveValue;
        for (Position tested : arrayToSubtractFrom) {
            saveValue = true;
            for (Position value : arrayBeingSubtracted) {
                if (tested.equals(value)) {
                    saveValue = false;
                    break;
                }
            }
            if (saveValue){
                result[index] = tested;
                index++;
            }
        }
        return Arrays.copyOf(result, index);
    }

    public Position[] getNeighbourhoodOfStandardPath() {
        Position[] path = getStandardPath();
        Position[] result = new Position[]{};
        for (Position position : path) {
            result = mergeArrays(result, position.getNeighbourhood());
        }
        return subtractArrays(removeDuplicates(result), path);
    }

    public static BoundVector getRandomOrthogonalBoundVector(int lowerXBound, int upperXBound, int lowerYBound, int upperYBound, int size) {
        Position origin = Position.getRandomPosition(lowerXBound, upperXBound, lowerYBound, upperYBound);
        Position destination = Position.getRandomPosition(origin, origin.x() - size, origin.x() + size, origin.y() - size, origin.y() + size);
        return new BoundVector(origin, destination);
    }

    /**
     * The method returns String representation of this bound vector
     *
     * @return String representation of this bound vector
     */
    @Override
    public String toString() {
        return "{" + origin.toString() + "," + destination.toString() + "}";
    }

}
