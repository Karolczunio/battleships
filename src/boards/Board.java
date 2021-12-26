package boards;

import navigation.Position;

import java.util.Arrays;

public class Board {
    private final static int x = 10;
    private final static int y = 10;
    private final BoardElement[][] contents;

    public Board(BoardElement[][] contents) {
        if (contents == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (contents.length != y) {
            throw new IllegalArgumentException("Array should be of height " + y);
        }
        for (BoardElement[] content : contents) {
            if (content.length != x) {
                throw new IllegalArgumentException("Array should be of width " + x);
            }
            for (BoardElement boardElement : content) {
                if (boardElement == null) {
                    throw new IllegalArgumentException("Array element cannot be null");
                }
            }
        }
        this.contents = contents;
    }

    public static Board getEmptyBoard() {
        BoardElement[][] boardElements = new BoardElement[y][x];
        for (BoardElement[] boardElement : boardElements) {
            Arrays.fill(boardElement, new BoardElement(Part.EMPTY, 0, true));
        }
        return new Board(boardElements);
    }

    public boolean isValidPosition(Position where) {
        return where.x() >= 0 && where.x() < x && where.y() >= 0 && where.y() < y;
    }

    private BoardElement[][] getContents() {
        BoardElement[][] copy = new BoardElement[contents.length][];
        for (int i = 0; i < contents.length; i++) {
            copy[i] = Arrays.copyOf(contents[i], contents[i].length);
        }
        return copy;
    }

    public Position[] getAllValidPositions() {
        Position[] positions = new Position[x * y];
        int index = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                positions[index] = new Position(i, j);
                index++;
            }
        }
        return positions;
    }

    public BoardElement getElementByPosition(Position where) {
        if (!isValidPosition(where)){
            throw new IllegalArgumentException("Trying to access illegal position: "+where);
        }
        return contents[where.y()][where.x()];
    }

    public Position[] getPositionsOfAllElementsSatisfying(BoardElementTester tester) {
        Position[] positions = getAllValidPositions();
        Position[] area = new Position[positions.length];
        int index = 0;
        for (Position position : positions) {
            if (tester.test(getElementByPosition(position))) {
                area[index] = position;
                index++;
            }
        }
        return Arrays.copyOf(area, index);
    }

    public Board getModifiedBoard(BoardElementTransformer transformer, Position... places) {
        BoardElement[][] copy = getContents();
        for (Position place : places) {
            if (isValidPosition(place)) {
                copy[place.y()][place.x()] = transformer.change(copy[place.y()][place.x()]);
            }
        }
        return new Board(copy);
    }

    public Board getModifiedBoard(BoardElementTransformer transformer) {
        Position[] places = getAllValidPositions();
        return getModifiedBoard(transformer, places);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("**abcdefghij**\n");
        for (int i = 0; i < contents.length; i++) {
            int nr = i + 1;
            String extraSpace = (nr < 10) ? " " : "";
            builder.append(extraSpace).append(nr);
            for (int j = 0; j < contents[i].length; j++) {
                builder.append(contents[i][j].character());
            }
            builder.append(nr).append("\n");
        }
        builder.append("**abcdefghij**\n");
        return builder.toString();
    }
}
