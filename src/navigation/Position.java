package navigation;

import java.util.Random;

public record Position(int x, int y) {

    /**
     * The method returns Position created by moving from this position by the vector given as an argument.
     *
     * @param vector FreeVector representing displacement on the board from this position
     * @return the position on the board displaced by vector
     */
    public Position getPositionMovedBy(FreeVector vector) {
        return new Position(x + vector.x(), y + vector.y());
    }

    public Position[] getNeighbourhood(){
        Position[] result = new Position[8];
        result[0] = this.getPositionMovedBy(new FreeVector(1, 0));
        result[1] = this.getPositionMovedBy(new FreeVector(1, 1));
        result[2] = this.getPositionMovedBy(new FreeVector(0, 1));
        result[3] = this.getPositionMovedBy(new FreeVector(-1, 1));
        result[4] = this.getPositionMovedBy(new FreeVector(-1, 0));
        result[5] = this.getPositionMovedBy(new FreeVector(-1, -1));
        result[6] = this.getPositionMovedBy(new FreeVector(0, -1));
        result[7] = this.getPositionMovedBy(new FreeVector(1, -1));
        return result;
    }
    
    public static Position getRandomPosition(int lowerXBound, int upperXBound, int lowerYBound, int upperYBound){
        if (lowerXBound > upperXBound || lowerYBound > upperYBound){
            throw new IllegalArgumentException("Invalid bounds");
        }
        Random random = new Random();
        int randomX = lowerXBound+random.nextInt(upperXBound - lowerXBound + 1);
        int randomY = lowerYBound+random.nextInt(upperYBound - lowerYBound + 1);
        return new Position(randomX, randomY);
    }
    
    static Position getRandomPosition(Position positionOfReference, int lowerXBound, int upperXBound, int lowerYBound, int upperYBound){
        Position position = getRandomPosition(lowerXBound, upperXBound, lowerYBound, upperYBound);
        Random random = new Random();
        if (random.nextInt(2) == 0){
            return new Position(positionOfReference.x(), position.y());
        }
        else {
            return new Position(position.x(), positionOfReference.y()) ;
        }
    }
    
    /**
     * The method returns String representation of this position.
     *
     * @return For valid positions in format LD L - letter, D - digit,
     * for invalid positions String "out"
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
