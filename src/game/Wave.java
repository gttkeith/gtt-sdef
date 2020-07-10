package game;

import java.util.ArrayList;
import java.util.List;
import bagel.util.Point;
import cfg.EventData;

/* a Wave consists of stages of EventData, executed sequentially
   wave spawning incrementation and logic is included in the class */
public class Wave {
    // wave end rewards
    private static final int WAVE_REWARD_BASE = 150;
    private static final int WAVE_REWARD_MULTIPLIER = 100;

    /**
     * computes reward for completing a wave
     *
     * @param waveNum number of wave that was just completed
     */
    public static int waveCompleteReward (int waveNum) {
        if (waveNum==0) return 0;
        else return WAVE_REWARD_BASE + WAVE_REWARD_MULTIPLIER * waveNum;
    }

    private final List<EventData> events;
    private int phase = 0;
    private double intervals = 0.0;
    private double intervalProgress = 1.0;

    // constructor
    public Wave() {events = new ArrayList<EventData>();}
    public Wave (List<EventData> nevents) {events = nevents;}

    // getters
    public boolean isComplete() {return phase>=events.size();}

    /**
     * increments wave phase, interval and interval progress
     * loads new EventData upon event completion
     * does nothing if Wave is complete
     *
     * @param frameTime amount of real time that one frame is shown for
     * @param nmovementPath a List of Points which newly spawned Enemy entities will travel on
     */
    public void updateAll (double frameTime, List<Point> nmovementPath) {
        if (isComplete()) return;
        double progress = frameTime/getCurrentEventData().interval;
        intervalProgress += progress;
        intervals += progress;
        if (intervalProgress >= 1.0) {
            intervalProgress = 0.0;
            if (getCurrentEventData().eventType==EventData.eventTypeFromName(EventData.SPAWN)) {
                Enemy.addEnemy(getCurrentEventData().enemyToSpawn, nmovementPath);
            }
        }
        if (intervals >= getCurrentEventData().numToSpawn) {
            intervals=0;
            phase++;
        }
    }

    private EventData getCurrentEventData() {return events.size()>phase ? events.get(phase) : EventData.blank();}
}