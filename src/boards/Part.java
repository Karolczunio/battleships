package boards;

public enum Part {
    SHIP, WRECK, EMPTY;
    char character(){
        return switch (this){
            case SHIP -> 'S';
            case WRECK -> 'W';
            case EMPTY -> ' ';
        };
    }
}
