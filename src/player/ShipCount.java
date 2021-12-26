package player;

public record ShipCount(int numberOfTiles, int numberOfShips) {
    public ShipCount{
        if (numberOfTiles <= 0){
            throw new IllegalArgumentException("numberOfTiles has to be a positive integer");
        }
        if (numberOfShips <= 0){
            throw new IllegalArgumentException("numberOfShips has to be a positive integer");
        }
    }
}
