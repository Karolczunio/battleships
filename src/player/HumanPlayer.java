package player;

import boards.Board;
import navigation.BoundVector;
import navigation.Position;

import java.util.Scanner;

public class HumanPlayer extends Player {
    static int shipNR = 0;
final static String[] placements = {"a1e1", "g1g1", "i1i1", "d5d5", "i5i7", "b7b9", "f10i10"};
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public BoundVector supplyShipPlacement(int size, Board target) {
        /*Scanner keyboard = new Scanner(System.in);
        String inputText;
        String promptMessage = "Enter positions of both ends of the " + size + "-tiled ship: ";
        String errorMessage = "Invalid ship placement!";
        System.out.println(target);
        System.out.print(promptMessage);
        inputText = keyboard.nextLine();
        while (!inputText.matches("([a-j]([1-9]|10)){2}") || !Helper.isValidShipPlacement(size, Helper.getBoundVector(inputText), target)) {
            System.out.println(errorMessage);
            System.out.println(target);
            System.out.print(promptMessage);
            inputText = keyboard.nextLine();
        }
        return Helper.getBoundVector(inputText);*/
        return Helper.getBoundVector(placements[shipNR++]);
    }

    @Override
    public Position supplyShotPosition() {
        Scanner keyboard = new Scanner(System.in);
        String promptMessage = "Enter position you want to shoot at: ";
        String errorMessage = "Incorrect position!";
        String inputText;

        System.out.print(promptMessage);
        inputText = keyboard.nextLine();
        while (!inputText.matches("[a-j]([1-9]|10)")) {
            System.out.println(errorMessage);
            System.out.print(promptMessage);
            inputText = keyboard.nextLine();
        }
        return Helper.getPosition(inputText);
    }

    @Override
    public void confirmShot(ShotResult result, Position positionOfShot) {
        System.out.println("You shot at: " + positionOfShot);
        System.out.println(shotsBoard);
        System.out.println(result.name());
    }

    @Override
    public void confirmDamage(ShotResult result, Position positionOfShot) {
        System.out.println("You shot at: " + positionOfShot);
        System.out.println(placementsBoard);
        System.out.println(result.name());
    }


}
