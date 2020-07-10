import java.util.List;
import java.util.NavigableMap;
import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.Window;
import cfg.*;
import game.*;

public class ShadowDefend extends AbstractGame {
    // configuration
    private static final int FRAME_RATE = 75; // Hz
    private static final double TIMESCALE_STEP = 1.0;
    private static final double MIN_TIMESCALE = 1.0;
    private static final double MAX_TIMESCALE = 5.0;

    // status string constants
    private static final String WINNER_STATUS = "Winner!";
    private static final String PLACING_STATUS = "Placing";
    private static final String WAVE_IN_PROGRESS_STATUS = "Wave In Progress";
    private static final String AWAITING_STATUS = "Awaiting Start";
    
    // state storage
    private final NavigableMap<Integer,LevelData> levels = LevelData.loadFromFile();
    private final double frameTime = 1.0/FRAME_RATE;
    private TiledMap map;
    private List<Point> lane;
    private NavigableMap<Integer,List<EventData>> waves;
    private Wave currentWave = new Wave();
    private int waveNum = 0;
    private boolean waveRewardGiven = false;
    private double timescale = 1.0;
    private boolean timescaleAdjusting = false;
    
    /* entry point for Bagel game */
    public static void main(String[] args) {
        // create new instance of game and run it
        new ShadowDefend().run();
    }

    /* game setup */
    public ShadowDefend() {
        loadNextLevel();
    }
    
    /* per-frame game state update */
    @Override
    protected void update(Input input) {
        map.draw(0, 0, 0, 0, Window.getWidth(), Window.getHeight());

        // update spawn and wave progress
        currentWave.updateAll(frameTime*timescale, lane);
        // update towers and attacks
        if (!Player.getInstance().isSuspended()) Tower.updateAll(frameTime*timescale);
        // update enemy movement, death andååå penalties
        Enemy.updateAll(frameTime*timescale);

        // finally, check status and draw UI
        String status = AWAITING_STATUS;
        if (Enemy.countAllInstances()!=0) {status = WAVE_IN_PROGRESS_STATUS;}
        if (UI.getInstance().getSelectionActive()) {status = PLACING_STATUS;}
        if (Player.getInstance().isWinner()) {status = WINNER_STATUS;}
        UI.getInstance().drawBuy(Player.getInstance().getMoney());
        UI.getInstance().drawStatus(waveNum, timescale, status, Player.getInstance().getLives());
        UI.getInstance().updateMouse(input, lane);

        // level progress, wave reward, win/lose condition
        if (Player.getInstance().isLoser()) {Window.close();}
        if (currentWave.isComplete() & Enemy.countAllInstances()==0) {
            // level completion/winning
            if (waves.isEmpty()) {
                if (levels.isEmpty()) Player.getInstance().win(); // YOU WIN
                else {
                    Tower.resetAll();
                    Player.getInstance().resetState();
                    timescale = 1.0;
                    waveNum = 0;
                    loadNextLevel();
                }
            // wave completion reward
            } else if (!waveRewardGiven) {
                Player.getInstance().reward(Wave.waveCompleteReward(waveNum));
                waveNum++;
                waveRewardGiven = true;
            }
        }

        // exit shortcut
        if (input.isDown(Keys.ESCAPE)) {
            Window.close();
        }

        // prevent key detection in suspended state
        if (Player.getInstance().isSuspended()) return;

        // advance wave
        if (input.isDown(Keys.S)) {
            // load new wave is current wave is complete and no enemies are left
            if (currentWave.isComplete() & !waves.isEmpty() & Enemy.countAllInstances()==0) {
                currentWave = new Wave(waves.pollFirstEntry().getValue());
                waveRewardGiven = false;
            }
        }

        // timescale adjustment
        if (input.isDown(Keys.K)) {
            if (!timescaleAdjusting) timescale = Math.max(MIN_TIMESCALE, timescale-TIMESCALE_STEP);
            timescaleAdjusting = true;
        }
        if (input.isDown(Keys.L)) {
            if (!timescaleAdjusting) timescale = Math.min(MAX_TIMESCALE, timescale+TIMESCALE_STEP);
            timescaleAdjusting = true;
        }
        if (input.isUp(Keys.K) & input.isUp(Keys.L)) {
            timescaleAdjusting = false;
        }
    }

    // loads the next level in the stored levels Map
    private void loadNextLevel () {
        LevelData l = levels.pollFirstEntry().getValue();
        waves = l.waves;
        map = new TiledMap(l.mapPath);
        lane = map.getAllPolylines().get(0);
    }
}