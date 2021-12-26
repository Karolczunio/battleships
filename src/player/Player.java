package player;

import boards.Board;
import navigation.BoundVector;
import navigation.Position;

public abstract class Player {
    private final String name;
    protected Board placementsBoard;
    protected Board shotsBoard;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract BoundVector supplyShipPlacement(int size, Board target);

    public abstract Position supplyShotPosition();

    public abstract void confirmShot(ShotResult result, Position positionOfShot);

    public abstract void confirmDamage(ShotResult result, Position positionOfShot);
}
