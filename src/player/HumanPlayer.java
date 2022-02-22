package player;

import boards.Board;
import navigation.BoundVector;
import navigation.Position;

import java.util.Scanner;

public class HumanPlayer extends Player {
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public BoundVector supplyShipPlacement(int size, Board target) {
        Scanner keyboard = new Scanner(System.in);
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
        return Helper.getBoundVector(inputText);
    }

    @Override
    public void confirmPlacingAllShips() {
        System.out.println(this.placementsBoard);
        System.out.println("USER PLACED ALL SHIPS");
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
        System.out.println("Enemy shot at: " + positionOfShot);
        System.out.println(placementsBoard);
        System.out.println(result.name());
    }


}
