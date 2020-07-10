package game;

/* singleton class for tracking player state */
public class Player {
    private final int STARTING_LIVES = 25;
    private final int STARTING_MONEY = 500;

    private int lives = STARTING_LIVES;
    private int money = STARTING_MONEY;

    private boolean winner = false;
    private boolean loser = false;

    // singleton initialisation
    private static Player instance = null; 
    private Player() {}
    public static Player getInstance() {
        if (instance == null) instance = new Player();
        return instance;
    }

    // getters
    public int getLives() {return lives;}
    public int getMoney() {return money;}
    public boolean isWinner() {return winner;}
    public boolean isLoser() {return loser;}

    // derived attributes
    public boolean canAfford (int cost) {return money>=cost;}
    public boolean isSuspended() {return winner|loser;}

    // state management
    public void resetState() {lives = STARTING_LIVES; money = STARTING_MONEY;}

    public void win() {winner = true;}

    public void penalise (int livesLost) {
        if (!isSuspended()) lives = Math.max(0, lives-livesLost);
        loser = lives<=0;
    }

    public void reward (int amount) {money+=amount;}

    public boolean spendIfEnough (int cost) {
        if (isSuspended()) return false;
        if (canAfford(cost)) {money-=cost; return true;}
        else {return false;}
    }
}