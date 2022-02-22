package navigation;

public record FreeVector(int x, int y) {

    /**
     * The method checks if this FreeVector represents orthogonal displacement
     *
     * @return true if displacement is orthogonal, otherwise false
     */
    public boolean isOrthogonal() {
        return distance() != 0 && (x == 0 || y == 0);
    }

    /**
     * The method checks if FreeVector represents diagonal displacement
     *
     * @return true if displacement is diagonal, otherwise false
     */
    public boolean isDiagonal() {
        return distance() != 0 && Math.abs(x) == Math.abs(y);
    }

    /**
     * The method checks if FreeVector represents displacement that is 2 tiles in one direction
     * and 1 tile in another direction
     *
     * @return true if displacement is 1 tile in one direction and 2 tiles in another direction, otherwise false
     */
    public boolean isLShaped() {
        return (Math.abs(x) == 2 && Math.abs(y) == 1)
                || (Math.abs(x) == 1 && Math.abs(y) == 2);
    }

    /**
     * The method returns Chebyshev distance of this FreeVector
     *
     * @return distance of this FreeVector
     */
    public int distance() {
        return Math.max(Math.abs(x), Math.abs(y));
    }

    /**
     * The method adds argument FreeVector to this FreeVector and returns their sum
     *
     * @param vector FreeVector to be added to this FreeVector
     * @return sum of this FreeVector and argument FreeVector
     */
    public FreeVector add(FreeVector vector) {
        return new FreeVector(this.x() + vector.x(), this.y() + vector.y());
    }

    /**
     * The method subtracts argument FreeVector from this FreeVector and returns their difference
     *
     * @param vector FreeVector to be subtracted from this FreeVector
     * @return difference of this FreeVector and argument FreeVector
     */
    public FreeVector subtract(FreeVector vector) {
        return new FreeVector(this.x() - vector.x(), this.y() - vector.y());
    }

    /**
     * The method returns FreeVector that represents general direction of its displacement but without magnitude
     *
     * @return general direction of displacement as FreeVector
     */
    public FreeVector iteration() {
        return new FreeVector(x != 0 ? x / Math.abs(x) : 0, y != 0 ? y / Math.abs(y) : 0);
    }

    /**
     * The method returns String representation of this FreeVector
     *
     * @return String representation of this FreeVector.
     */
    @Override
    public String toString() {
        return "{" + x + "," + y + "}";
    }
}
