import player.BotPlayer;
import player.HumanPlayer;
import player.Match;

public class Main {
    public static void main(String[] args) {
        HumanPlayer humanPlayer = new HumanPlayer("human");
        BotPlayer botPlayer = new BotPlayer("bot");
        Match.create(humanPlayer, botPlayer).play();
    }
}
