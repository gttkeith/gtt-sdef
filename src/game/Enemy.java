package game;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import bagel.util.Point;
import cfg.EnemyData;

/* a singular enemy that gets damaged and moves to its target
   static methods manage enemy tracking, movement, death and penalty logic */
public class Enemy extends GameEntity {
    // all enemy data as a Map
    public static final Map<String,EnemyData> DATA = EnemyData.loadFromFile();

    // tracks all created Enemy instances
    private static Map<Integer,Enemy> allInstances = new HashMap<Integer,Enemy>();
    public static int countAllInstances() {return allInstances.size();}

    /**
     * adds a new Enemy entity to the registry
     *
     * @param entityName the identifying name of the Enemy to be spawned
     * @param nmovementPath a List of Points which the Enemy will travel on
     */
    public static void addEnemy(String entityName, List<Point> nmovementPath) {
        Enemy spawn = new Enemy(entityName, nmovementPath);
        allInstances.put(spawn.id, spawn);
    }

    /**
     * updates all Enemy positions and rotations towards their targets
     * redraws all Enemy entities
     * rewards player and spawns children for dead Enemy entities
     * removes dead Enemy entities
     *
     * @param frameTime amount of real time that one frame is shown for
     */
    public static void updateAll (double frameTime) {
        // mark children to be added and entries to be removed
        List<Enemy> toAdd = new LinkedList<Enemy>();
        List<Integer> toRemove = new LinkedList<Integer>();
        for (Map.Entry<Integer,Enemy> ee : allInstances.entrySet()) {
            Enemy e = ee.getValue();
            Sprite s = e.sprite;
            // if dead, spawn children and remove from instance registry
            if (!e.isAlive()) {
                Player.getInstance().reward(e.getData().reward);
                if (!e.getData().childType.equals("")) {
                    for (int i=0; i<e.getData().childNum; i++) {toAdd.add(new Enemy(e.getData().childType, e.movementPath, s.getPosition()));}
                }
                toRemove.add(ee.getKey());
            // if alive, move to end and penalise player if end is reached
            } else {
                if (e.reachedDestination()) {
                    Player.getInstance().penalise(e.getData().penalty);
                    toRemove.add(ee.getKey());
                } else {
                    e.moveToTarget(frameTime);
                    s.draw();
                }
            }
        }
        // add children and remove all marked entries
        for (Enemy e : toAdd) allInstances.put(e.id, e);
        for (int i : toRemove) allInstances.remove(i);
    }

    /**
     * returns a list of Enemies that are within range of a set Point
     *
     * @param point the Point in question
     * @param range the range in question
     * @return the first enemy that is within range of the
     */
    public static List<Enemy> withinRangeOf (Point point, double range) {
        List<Enemy> result = new LinkedList<Enemy>();
        for (Enemy e : allInstances.values()) {
            if (e.sprite.getDistanceFrom(point)<range) {result.add(e);}
        }
        return result;
    }

    private int currentHealth;
    private List<Point> movementPath;

    // constructors
    private Enemy (String entityName, List<Point> nmovementPath) {
        super(entityName, DATA.get(entityName).spritePath);
        currentHealth = getData().maxHealth;
        movementPath = new LinkedList<Point>(nmovementPath);
        super.sprite.setPosition(getTarget());
    }
    private Enemy (String entityName, List<Point> nmovementPath, Point customPostion) {
        super(entityName, DATA.get(entityName).spritePath);
        currentHealth = getData().maxHealth;
        movementPath = new LinkedList<Point>(nmovementPath);
        super.sprite.setPosition(customPostion);    
    }
    
    /**
     * damages an Enemy by a set absolute amount
     *
     * @param amount the amount of damage taken
     */
    public void damage (int amount) {currentHealth-=amount;}

    // derived attributes
    private EnemyData getData() {return DATA.get(entityName);}
    private boolean isAlive() {return currentHealth>0;}
    private boolean reachedDestination() {return movementPath.isEmpty();}
    private Point getTarget() {return reachedDestination() ? sprite.getPosition() : movementPath.get(0);}

    /**
     * updates an Enemy's position and rotation
     * use getSprite().draw() to show updated sprite
     *
     * @param frameTime amount of real time that one frame is shown for
     */
    private void moveToTarget (double frameTime) {
        if (reachedDestination()) return;
        Point current = sprite.getPosition();
        if (getTarget().equals(current)) movementPath.remove(0);
        double distMoved = frameTime * DATA.get(entityName).speed;
        sprite.setPosition(PathingManager.newPosition(current, getTarget(), distMoved));
        sprite.setRotation(PathingManager.newRotation(current, getTarget()));
    }
}