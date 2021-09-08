import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

    //number of ships for each player
    final static int shipCount = 5;
    //character representing empty field
    final static char emptyField = ' ';
    //character representing hidden field
    final static char hiddenField = '.';
    //character representing undamaged part of a ship
    final static char shipField = 'S';
    //character representing damaged part of a ship
    final static char wreckField = 'W';

    //board representing positions of player ship placements
    static char[][] playerPlacementsBoard = new char[10][10];
    //board representing state of knowledge about the enemy board(computer placements board)
    static char[][] playerShotsBoard = new char[10][10];
    //array in which each row represents a ship,
    //where each element of such a row is a position of a part of the ship
    //on the player placements board
    static int[][][] playerShips = new int[shipCount][][];

    //board representing positions of computer ship placements
    static char[][] computerPlacementsBoard = new char[10][10];
    //board representing state of knowledge about the enemy board(player placements board)
    static char[][] computerShotsBoard = new char[10][10];
    //array in which each row represents a ship,
    //where each element of such a row is a position of a part of the ship
    //on the computer placements board
    static int[][][] computerShips = new int[shipCount][][];

    //initialization of the static elements of the program
    static {
        for (int i = 0; i < 10; i++) {

            Arrays.fill(playerPlacementsBoard[i], emptyField);
            Arrays.fill(playerShotsBoard[i], hiddenField);

            Arrays.fill(computerPlacementsBoard[i], emptyField);
            Arrays.fill(computerShotsBoard[i], hiddenField);
        }
    }

    /**
     * The method checks whether given board is valid
     * which means it's regular 10 by 10 char array
     *
     * @param board char array representing board
     * @return true when valid, otherwise false
     */
    static boolean isValidBoard(char[][] board) {
        if (board == null || board.length != 10)
            return false;
        for (char[] array : board) {
            if (array.length != 10)
                return false;
        }
        return true;
    }

    /**
     * This method takes a board represented as an array
     * and transforms it into its String representation
     *
     * @param board array represented as 2d char array
     * @return board represented as a String
     */
    private static String getBoardAsString(char[][] board) {
        StringBuilder result = new StringBuilder(131);
        result.append("*abcdefghij*\n");
        for (int i = 0; i < board.length; i++) {
            result.append(i);
            for (int j = 0; j < board[i].length; j++) {
                result.append(board[i][j]);
            }
            result.append(i);
            result.append('\n');
        }
        result.append("*abcdefghij*\n");
        return result.toString();
    }

    /**
     * The method transforms String representation of the position
     * into position represented as an int array
     *
     * @param position represented as a String
     * @return position represented as int array
     */
    static int[] getPositionVector(String position) {
        return new int[]{position.charAt(0) - 'a', position.charAt(1) - '0'};
    }

    /**
     * The method transforms position represented as an int array
     * into position represented as a String
     *
     * @param position represented as int array
     * @return position represented as String
     */
    static String getPositionString(int[] position) {
        return String.valueOf((char) ('a' + position[0])) + String.valueOf((char) ('0' + position[1]));
    }

    /**
     * The method extracts character from array board
     * at a position given by int array position
     *
     * @param board    board to extract character from
     * @param position position at which character will be extracted from a board
     * @return the character extracted from a board
     */
    private static char getBoardElement(char[][] board, int[] position) {
        if (!isValidBoard(board))
            throw new IllegalArgumentException("Incorrect Board");
        if (!isValidPosition(position))
            throw new IllegalArgumentException("Incorrect Position");
        return board[position[1]][position[0]];
    }

    /**
     * The method sets value into position at the board
     *
     * @param board    board to insert character into
     * @param position position at which the value should be set
     * @param value    character to set the board at a given position
     */
    private static void setBoardElement(char[][] board, int[] position, char value) {
        if (!isValidBoard(board))
            throw new IllegalArgumentException("Incorrect Board");
        if (!isValidPosition(position))
            throw new IllegalArgumentException("Incorrect Position");
        board[position[1]][position[0]] = value;
    }

    /**
     * The method checks if position is contained within 10 by 10 board
     *
     * @param position that is to be checked
     * @return true if it's valid, otherwise false
     */
    static boolean isValidPosition(int[] position) {
        return position != null && position.length == 2
                && position[0] >= 0 && position[0] <= 9
                && position[1] >= 0 && position[1] <= 9;
    }

    /**
     * The method checks whether both position is within a given board
     * and the position at the given board contains a character that represents an empty field
     *
     * @param board    board inside which to check for a character
     * @param position position to check the character at
     * @return true if conditions are met, otherwise false
     */
    static boolean isValidInterior(char[][] board, int[] position) {
        return isValidPosition(position) && getBoardElement(board, position) == emptyField;
    }

    /**
     * The method checks whether either position is outside the given board
     * or is within it but contains a character that represents an empty field
     *
     * @param board    board inside which to check for a character
     * @param position position to check the character at
     * @return true if conditions are met, otherwise false
     */
    static boolean isValidExterior(char[][] board, int[] position) {
        return !isValidPosition(position) || getBoardElement(board, position) == emptyField;
    }

    /**
     * The method checks whether array of 3 characters representing 3 positions
     * either before a sequence of tiles on a board representing a potential ship placement
     * or after it is composed of positions that represent the exterior which means
     * they are either outside the board or contain an element that represents an empty field
     *
     * @param board   board inside which to check for characters
     * @param section an array of positions before or behind potential ship placement
     * @return true if conditions are met, otherwise false
     */
    static boolean isValidEdgeSection(char[][] board, int[][] section) {
        return isValidExterior(board, section[0])
                && isValidExterior(board, section[1])
                && isValidExterior(board, section[2]);
    }

    /**
     * The method checks whether array of 3 characters representing 3 positions
     * within potential ship placement is composed of positions that represent
     * at the edges of the section, the exterior, which means they are either outside the board
     * or contain an element that represents an empty field, in the center of the section interior
     * which means both a valid position within a board and that this position contains
     * a character that represents an empty field
     *
     * @param board   board inside which to check for characters
     * @param section an array of positions within potential ship placement
     * @return true if conditions are met, otherwise false
     */
    static boolean isValidMiddleSection(char[][] board, int[][] section) {
        return isValidExterior(board, section[0])
                && isValidInterior(board, section[1])
                && isValidExterior(board, section[2]);
    }

    /**
     * The method takes String representing 2 positions starting position
     * and a final position of a ship structure
     *
     * @param positions String representing 2 positions
     * @return array of 2 positions also called bound displacement vector
     */
    static int[][] getBoundDisplacementVector(String positions) {
        if (!positions.matches("([a-j][0-9]){2}"))
            throw new IllegalArgumentException("Incorrect Bound Displacement Vector");
        return new int[][]{getPositionVector(positions.substring(0, 2)), getPositionVector(positions.substring(2, 4))};
    }

    /**
     * The method that takes an array of 2 positions also called a bound vector
     * and returns a pair of coordinates you would need to add to the starting position
     * to get a final position
     *
     * @param boundVector the array of 2 positions the starting position and the final position
     * @return free displacement vector, which added to the starting position gets you the final position
     */
    static int[] getFreeDisplacementVector(int[][] boundVector) {
        return new int[]{boundVector[1][0] - boundVector[0][0], boundVector[1][1] - boundVector[0][1]};
    }

    /**
     * The method creates ORTHODIAGONAL ITERATION VECTOR from FREE VECTOR
     * that represents orthogonal or diagonal displacement.
     * In either orthogonal or diagonal displacement,
     * adding vector that is returned by this method
     * to vector used to represent the initial position on a board
     * will produce vector representing
     * the next position in either orthogonal or diagonal movement.
     *
     * @param freeVector int[] object representing FREE VECTOR
     * @return int[] object representing ORTHODIAGONAL ITERATION VECTOR
     */
    static int[] getOrthodiagonalIterationVector(int[] freeVector) {
        int[] iterationVector = new int[2];
        iterationVector[0] = (freeVector[0] != 0) ? freeVector[0] / Math.abs(freeVector[0]) : 0;
        iterationVector[1] = (freeVector[1] != 0) ? freeVector[1] / Math.abs(freeVector[1]) : 0;
        return iterationVector;
    }

    /**
     * It's checking whether 2 vectors a and b
     * where a and b are each composed of x and y coordinates,
     * where x is coordinate at index 0 and y coordinate is at index 1 of each array representing vector,
     * have x coordinates equal to one another and y coordinates equal to one another
     *
     * @param a int[] object representing first vector to check for equality
     * @param b int[] object representing second vector to check for equality
     * @return true if vectors are equal, false otherwise
     */
    static boolean areVectorsEqual(int[] a, int[] b) {
        return a[0] == b[0] && a[1] == b[1];
    }

    /**
     * The method is adding 2 vectors.
     * It's creating new vector c from 2 vectors a and b,
     * where a, b and c are each composed of x and y coordinates,
     * where x coordinate is at index 0 and y coordinate is at index 1 of each array representing vector,
     * and x coordinate of c is the sum of x coordinates of a and b,
     * and y coordinate of c is the sum of y coordinates of a and b.
     *
     * @param a int[] object representing first vector to add
     * @param b int[] object representing second vector to add
     * @return int[] object representing vector that is a result of addition of vectors a and b
     */
    static int[] getVectorSum(int[] a, int[] b) {
        return new int[]{a[0] + b[0], a[1] + b[1]};
    }

    /**
     * The method accepts the coordinate pair that represents displacement in some direction
     * and returns the coordinate pair that represents displacement in the opposite direction
     *
     * @param a the given displacement
     * @return the displacement that is opposite to the given one
     */
    static int[] getOppositeVector(int[] a) {
        return new int[]{-a[0], -a[1]};
    }

    /**
     * The method accepts the coordinate pair that represents displacement in some direction
     * and returns the coordinate pair that represents displacement perpendicular to the given direction
     * and pointing to the right
     *
     * @param a the given displacement
     * @return the displacement that is perpendicular to the given one and pointing to the right
     */
    static int[] getRightPerpendicularVector(int[] a) {
        return new int[]{a[1], -a[0]};
    }

    /**
     * The method accepts the coordinate pair that represents displacement in some direction
     * and returns the coordinate pair that represents displacement perpendicular to the given direction
     * and pointing to the left
     *
     * @param a the given displacement
     * @return the displacement that is perpendicular to the given one and pointing to the left
     */
    static int[] getLeftPerpendicularVector(int[] a) {
        return new int[]{-a[1], a[0]};
    }

    /**
     * The method checks whether the coordinate pair representing a displacement represents displacement
     * equal to 0 in all directions
     *
     * @param freeVector the coordinate pair representing a displacement
     * @return true if displacement is none, otherwise false
     */
    static boolean isZeroVector(int[] freeVector) {
        return freeVector[0] == 0 && freeVector[1] == 0;
    }

    /**
     * The method returns an array of iteration vectors that are used to move around the board
     * oriented in some way when it comes to displacement given as an argument.
     * At index 0 in the direction of displacement or forward,
     * at index 1 rightward, at index 2 backward and at index 3 leftward.
     * For free vectors that represent the displacement that is none( zero vectors)
     * the method arbitrarily chooses the iteration towards positive x coordinates as a direction forward
     * and all other directions are referring to that main one.
     *
     * @param freeVector vector that represents displacement in some direction
     * @return array that represents iteration vectors in 4 directions with one in the original direction,
     * two others perpendicular and one opposite
     */
    static int[][] getDirections(int[] freeVector) {
        int[] forward, rightward, backward, leftward;
        forward = isZeroVector(freeVector) ? new int[]{1, 0} : getOrthodiagonalIterationVector(freeVector);
        rightward = getRightPerpendicularVector(forward);
        backward = getOppositeVector(forward);
        leftward = getLeftPerpendicularVector(forward);
        return new int[][]{forward, rightward, backward, leftward};
    }

    /**
     * The method takes a position and directions according to which the method should see directions
     * and returns an array of 3 elements one at index 1 being the position in the center,
     * at index 0 being the position left of center and at index 2 being the position right of center
     *
     * @param position   position being the center
     * @param directions that method should refer to
     * @return array containing 3 positions at specified positions
     */
    static int[][] getMiddleSection(int[] position, int[][] directions) {
        return new int[][]{getVectorSum(position, directions[3]), position, getVectorSum(position, directions[1])};
    }

    /**
     * The method takes a position and directions according to which the method should see directions
     * and returns an array of 3 elements one at index 1 being the position in the center,
     * at index 0 being the position left of center and at index 2 being the position right of center
     *
     * @param position   position that is forwards to the center
     * @param directions that method should refer to
     * @return array containing 3 positions at specified positions
     */
    static int[][] getFirstEdgeSection(int[] position, int[][] directions) {
        return getMiddleSection(getVectorSum(position, directions[2]), directions);
    }

    /**
     * The method takes a position and directions according to which the method should see directions
     * and returns an array of 3 elements one at index 1 being the position in the center,
     * at index 0 being the position left of center and at index 2 being the position right of center
     *
     * @param position   position that is backwards to the center
     * @param directions that method should refer to
     * @return array containing 3 positions at specified positions
     */
    static int[][] getLastEdgeSection(int[] position, int[][] directions) {
        return getMiddleSection(getVectorSum(position, directions[0]), directions);
    }

    /**
     * The method checks whether freeVector that represents displacement is orthogonal
     * and forms a line of length numberOfTiles
     *
     * @param freeVector    pair of coordinates that represents displacement
     * @param numberOfTiles length of the line that should be formed
     * @return true if freeVector represents valid line of length numberOfTiles, otherwise false
     */
    static boolean isValidLine(int[] freeVector, int numberOfTiles) {
        return (freeVector[0] == 0 || freeVector[1] == 0)
                && Math.max(Math.abs(freeVector[0]), Math.abs(freeVector[1])) + 1 == numberOfTiles;
    }

    /**
     * The method checks whether the ship can be placed
     * between and including two ends of the ship represented by the boundVector
     * at the given board and that this ship has numberOfTiles as the number of its tiles
     *
     * @param board         board to fit the ship in
     * @param boundVector   represents two ends of the ship
     * @param numberOfTiles represents the length of the ship that should fit on the board
     * @return true if the ship can be placed on the board, otherwise false
     */
    static boolean isValidShipPlacement(char[][] board, int[][] boundVector, int numberOfTiles) {
        int[] freeVector = getFreeDisplacementVector(boundVector);

        int[][] directions = getDirections(freeVector);
        int[][] section;

        section = getFirstEdgeSection(boundVector[0], directions);
        if (!isValidEdgeSection(board, section))
            return false;

        section = getLastEdgeSection(boundVector[1], directions);
        if (!isValidEdgeSection(board, section))
            return false;

        int[] position = boundVector[0];

        for (int i = 0; i < numberOfTiles; i++) {
            section = getMiddleSection(position, directions);
            if (!isValidMiddleSection(board, section))
                return false;
            position = getVectorSum(position, directions[0]);
        }
        return true;
    }

    /**
     * The method takes boundVector that represents two ends of the ship
     * and returns all positions between and including those 2 ends
     *
     * @param boundVector representation of 2 ends of the ship
     * @return all positions between and including 2 ends of the ship
     */
    static int[][] getAllPositionsWithin(int[][] boundVector) {
        int[] freeVector = getFreeDisplacementVector(boundVector);
        int[][] directions = getDirections(freeVector);
        int length = getOrthodiagonalLineLength(freeVector);
        int[] position = boundVector[0];
        int[][] result = new int[length][];
        for (int i = 0; i < length; i++) {
            result[i] = position;
            position = getVectorSum(position, directions[0]);
        }
        return result;
    }

    /**
     * The method looks for first null element in the array and inserts the new element there
     *
     * @param array   array to insert the element into
     * @param element the element to insert
     */
    static void insertIntoFirstFreePosition(int[][][] array, int[][] element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                array[i] = element;
                break;
            }
        }
    }

    /**
     * The method places a ship described by boundVector
     * on a given board and inserts an array of positions of its parts
     * into an array of ships
     *
     * @param board         to place the ship on
     * @param ships         an array of ships to insert all positions of the new ship into
     * @param boundVector   represents two ends of the ship that should be placed on the board
     * @param numberOfTiles number of tiles the inserted ship should have
     */
    static void setAShip(char[][] board, int[][][] ships, int[][] boundVector, int numberOfTiles) {
        int[][] ship = getAllPositionsWithin(boundVector);
        insertIntoFirstFreePosition(ships, ship);
        int[] freeVector = getFreeDisplacementVector(boundVector);
        int[][] directions = getDirections(freeVector);
        int[] position = boundVector[0];
        for (int i = 0; i < numberOfTiles; i++) {
            setBoardElement(board, position, shipField);
            position = getVectorSum(position, directions[0]);
        }

    }

    /**
     * The method checks for validity of ship placement
     * described by String representation of two ends of the ship
     * that should have length of numberOfTiles
     * The purpose of this method is to allow for
     * displaying better adjusted error messages
     * by splitting the requirements of having a valid ship placement
     * into multiple boolean values placed in the array
     *
     * @param board         to check for a valid ship placement
     * @param inputText     the string that should contain 2 positions of 2 ends of the ship
     * @param numberOfTiles the length the ship should have
     * @return values at each index are true if the given requirement is not met, otherwise false
     * at index 0 check for String validity, at index 1 check for being a line of the given length
     * and at index 2 check for the ship being able to fit into the board
     */
    static boolean[] getChecks(char[][] board, String inputText, int numberOfTiles) {
        boolean[] checks = new boolean[3];
        checks[0] = !inputText.matches("([a-j][0-9]){2}");
        checks[1] = checks[0] || !isValidLine(getFreeDisplacementVector(getBoundDisplacementVector(inputText)), numberOfTiles);
        checks[2] = checks[1] || !isValidShipPlacement(board, getBoundDisplacementVector(inputText), numberOfTiles);
        return checks;
    }

    /**
     * The method returns an array of coordinate pairs that added to one position give you the position
     * of the other end of the line placed length-1 tiles away. Each element in the array represents displacement
     * in one of four orthogonal directions
     *
     * @param length length of the line created by the displacement
     * @return array of vectors to add to give you another end of the orthogonal line of given length
     */
    static int[][] getOrthogonalFreeVectors(int length) {
        int coordinate = length - 1;
        return new int[][]{{0, coordinate}, {0, -coordinate}, {coordinate, 0}, {-coordinate, 0}};
    }

    /**
     * The method returns an array of pairs of coordinate pairs
     * that represent pairs of positions that represent both ends of the ship.
     * Each element in the array represents both ends of the ship
     * created by moving position by length in one of four orthogonal directions
     *
     * @param length length of ship
     * @return array where each element is a pair of coordinate pairs that represents both ends of the ship
     */
    static int[][][] getOrthogonalBoundVectors(int[] position, int length) {
        int[][][] boundVectors = new int[4][2][2];
        int[][] freeVectors = getOrthogonalFreeVectors(length);
        for (int i = 0; i < 4; i++) {
            boundVectors[i] = new int[][]{position, getVectorSum(position, freeVectors[i])};
        }
        return boundVectors;
    }

    /**
     * The method checks if a given ship placed on a given board is sunk or not
     *
     * @param board that has a ship placed on it
     * @param ship  array of positions containing all positions on the board that have one of the parts of the ship
     * @return true if the ship is sunk, otherwise false
     */
    static boolean isSunk(char[][] board, int[][] ship) {
        for (int[] position : ship) {
            if (getBoardElement(board, position) == shipField) {
                return false;
            }
        }
        return true;
    }

    /**
     * The method takes a shot to the shotPosition at enemyPlacementsBoard
     * and modifies both enemyPlacementsBoard and enemyShips if the shot turns out to be a hit.
     * The method always modifies allyShotsBoard unless the player shoots in the same place twice or more.
     *
     * @param shotPosition         the position at enemyPlacementsBoard ally player wants to shoot at
     * @param enemyPlacementsBoard board that contains placements of enemy ships
     * @param enemyShips           an array that contains all positions of all parts of all ships of the enemy
     * @param allyShotsBoard       the board that represents allied state of knowledge about enemy ship placements
     * @return "hit" when ally damaged enemy ship,
     * *                       "sunk" when ally sunked enemy ship,
     * *                       "wasted" if ally shot in the same spot twice or more
     */
    static String shoot(int[] shotPosition, char[][] enemyPlacementsBoard, int[][][] enemyShips, char[][] allyShotsBoard) {
        for (int i = 0; i < enemyShips.length && enemyShips[i] != null; i++) {
            for (int j = 0; j < enemyShips[i].length; j++) {
                if (areVectorsEqual(shotPosition, enemyShips[i][j])) {
                    if (getBoardElement(enemyPlacementsBoard, shotPosition) == shipField) {
                        setBoardElement(enemyPlacementsBoard, shotPosition, wreckField);
                        setBoardElement(allyShotsBoard, shotPosition, wreckField);
                        return isSunk(enemyPlacementsBoard, enemyShips[i]) ? "sunk" : "hit";
                    }
                }
            }
        }
        if (getBoardElement(allyShotsBoard, shotPosition) == hiddenField) {
            setBoardElement(allyShotsBoard, shotPosition, emptyField);
            return "miss";
        }
        return "wasted";
    }

    /**
     * The method checks if all ships on a given board, with positions placed at ships array have been sunken
     *
     * @param board to look for tiles
     * @param ships array of ship placements with each ship placement being an array of all positions of the ship
     * @return true if all ships have been sunken, otherwise false
     */
    static boolean areAllShipsSunken(char[][] board, int[][][] ships) {
        for (int i = 0; i < ships.length && ships[i] != null; i++) {
            if (!isSunk(board, ships[i]))
                return false;
        }
        return true;
    }

    /**
     * The method asks a user to enter a valid shot position and repeatedly does so until it gets
     * the String that represents a valid position
     *
     * @return valid position String
     */
    static String getValidShotPosition() {
        Scanner keyboard = new Scanner(System.in);
        String promptMessage = "Enter position you want to shoot at: ";
        String errorMessage = "Incorrect format of position! Try mXnY, where m,n are letters from a to j and X,Y are numbers from 0 to 9";
        String inputText;
        System.out.print(promptMessage);
        inputText = keyboard.nextLine();
        while (!inputText.matches("[a-j][0-9]")) {
            System.out.println(errorMessage);
            System.out.print(promptMessage);
            inputText = keyboard.nextLine();
        }
        return inputText;
    }

    /**
     * The method looks for first null element in the array and inserts the new element there
     *
     * @param array   array to insert the element into
     * @param element the element to insert
     */
    static void insertIntoFirstFreePosition(int[][] array, int[] element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                array[i] = element;
                break;
            }
        }
    }

    /**
     * The method returns an array of all positions that contain a hiddenTile character on the shotsBoard
     *
     * @param shotsBoard the array to look for positions
     * @return the array of all positions that are hidden on a given board
     */
    static int[][] getArrayOfHiddenPositions(char[][] shotsBoard) {
        int[][] buffer = new int[100][];
        int count = 0;
        int[] position;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                position = new int[]{i, j};
                if (getBoardElement(shotsBoard, position) == hiddenField) {
                    insertIntoFirstFreePosition(buffer, position);
                    count++;
                }
            }
        }
        return Arrays.copyOf(buffer, count);
    }

    /**
     * The method returns random position
     *
     * @return random position
     */
    static int[] getRandomPosition() {
        Random random = new Random();
        return new int[]{random.nextInt(10), random.nextInt(10)};
    }

    /**
     * The method returns the position the computer should shoot at next
     *
     * @return the position the computer will shoot at
     */
    static int[] getShotPositionOfTheComputer() {
        Random random = new Random();
        int[][] hidden = getArrayOfHiddenPositions(computerShotsBoard);
        return hidden[random.nextInt(hidden.length)];
    }

    /**
     * The method returns the length of the line that is created by a given freeVector
     *
     * @param freeVector the given displacement
     * @return the length of the line that is created by a given freeVector
     */
    static int getOrthodiagonalLineLength(int[] freeVector) {
        return Math.max(Math.abs(freeVector[0]), Math.abs(freeVector[1])) + 1;
    }

    /**
     * The method prompts a user to enter coordinates of the ship of the given length
     *
     * @param numberOfTiles given length
     */
    static void letUserPlaceAShip(int numberOfTiles) {
        Scanner keyboard = new Scanner(System.in);
        boolean[] checks;
        String inputText;
        String promptMessage = "Enter positions of both ends of the " + numberOfTiles + "-tiled ship: ";
        String[] errorMessages = new String[3];
        errorMessages[0] = "Incorrect format of positions! Try mXnY, where m,n are letters from a to j and X,Y are numbers from 0 to 9";
        errorMessages[1] = "Pair of coordinates should be on one orthogonal line and represent ship of length " + numberOfTiles;
        errorMessages[2] = "Ship represented by coordinates should not intersect other ships or be to close to them.";
        String successMessage = "The ship is placed!";

        System.out.print(getBoardAsString(playerPlacementsBoard));
        System.out.print(promptMessage);
        inputText = keyboard.nextLine();
        checks = getChecks(playerPlacementsBoard, inputText, numberOfTiles);
        while (checks[0] || checks[1] || checks[2]) {
            if (checks[0]) {
                System.out.println(errorMessages[0]);
            }
            if (checks[1]) {
                System.out.println(errorMessages[1]);
            }
            if (checks[2]) {
                System.out.println(errorMessages[2]);
            }
            System.out.print(promptMessage);
            inputText = keyboard.nextLine();
            checks = getChecks(playerPlacementsBoard, inputText, numberOfTiles);
        }
        setAShip(playerPlacementsBoard, playerShips, getBoundDisplacementVector(inputText), numberOfTiles);
        System.out.println(successMessage);
    }

    /**
     * The method allows a user to place all of its ships
     */
    static void letUserPlaceShips() {
        for (int i = 0; i < 5; i++) {
            letUserPlaceAShip(i + 1);
        }
        System.out.print(getBoardAsString(playerPlacementsBoard));
        System.out.println("All ships are placed!");
    }

    /**
     * The method randomly generates valid ship placements
     * and puts them on the appropriate boards of the computer player
     *
     * @param numberOfTiles length of the ship that should be randomly generated
     */
    static void letComputerPlaceAShip(int numberOfTiles) {
        Random random = new Random();
        int[] position;
        int[][][] boundVectors;
        int randomIndex;
        do {
            position = getRandomPosition();
            boundVectors = getOrthogonalBoundVectors(position, numberOfTiles);
            randomIndex = random.nextInt(4);
        }
        while (!isValidShipPlacement(computerPlacementsBoard, boundVectors[randomIndex], numberOfTiles));
        setAShip(computerPlacementsBoard, computerShips, boundVectors[randomIndex], numberOfTiles);
    }

    /**
     * The method places all the ships of computer player
     */
    static void letComputerPlaceShips() {
        for (int i = 0; i < 5; i++) {
            letComputerPlaceAShip(i + 1);
        }
        System.out.println("The computer has placed its ships!");
    }

    /**
     * The method generates a position to shoot at and makes computer player shoot at the board of the human player.
     * Then it displays information about the position human player was shot at, if it hit one of human player ships
     * and the state of the ship placements board of the human player
     */
    static void letComputerTakeAShot() {
        int[] position = getShotPositionOfTheComputer();
        String message = shoot(position, playerPlacementsBoard, playerShips, computerShotsBoard);
        System.out.println("Enemy took a shot on " + getPositionString(position));
        System.out.println("Result: " + message);
        System.out.println("Your board after enemy shot:");
        System.out.print(getBoardAsString(playerPlacementsBoard));
    }

    /**
     * The method prompts a user to enter a shot position and then shoots at the board of the computer player.
     * Then it displays an information about position of our shot
     * and whether we hit one of computer player ships or not
     */
    static void letUserTakeAShot() {
        System.out.println("State of enemy ship placements we know of(" + hiddenField + " means unknown location):");
        System.out.print(getBoardAsString(playerShotsBoard));
        String position = getValidShotPosition();
        String message = shoot(getPositionVector(position), computerPlacementsBoard, computerShips, playerShotsBoard);
        System.out.println("You took a shot on " + position);
        System.out.println("Result: " + message);
    }

    /**
     * The method allows a user to play battleship with a computer
     */
    static void playBattleshipWithAComputer() {
        letUserPlaceShips();
        letComputerPlaceShips();
        String winner = "nobody";
        while (winner.equals("nobody")) {
            letUserTakeAShot();
            if (areAllShipsSunken(computerPlacementsBoard, computerShips)) {
                winner = "player";
            } else {
                letComputerTakeAShot();
                if (areAllShipsSunken(playerPlacementsBoard, playerShips)) {
                    winner = "computer";
                }
            }
        }
        System.out.println(winner + " wins!");
    }

    /**
     * The main method of the program
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        playBattleshipWithAComputer();
    }
}
