package game;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import bagel.Window;
import bagel.util.Point;

import java.util.HashMap;
import cfg.TowerData;
import cfg.UI;

/* a singular tower that is placed down and attacks */
public class Tower extends GameEntity {
    private static final double AIRPLANE_MOVESPEED = 375;
    private static final double SPRITE_ROTATION_OFFSET = Math.PI/2; // difference between enemy and tower sprites
    
    // all tower data as a Map
    public static final Map<String,TowerData> DATA = TowerData.loadFromFile();
    
    private static boolean planeWillBeHorizontal = true;

    // tracks all created Tower instances
    private static Map<Integer,Tower> allInstances = new HashMap<Integer,Tower>();

    public static boolean getPlaneWillBeHorizontal() {return planeWillBeHorizontal;}

    /**
     * adds a new Tower entity to the registry
     *
     * @param entityName the identifying name of the Tower to be created
     * @param nposition the click position where the tower is placed
     */
    public static void addTower(String entityName, Point nposition) {
        boolean isAirSupport = DATA.get(entityName).isAirSupport;
        if (isAirSupport) {
            if (planeWillBeHorizontal) nposition = new Point(0, nposition.y);
            else nposition = new Point(nposition.x, UI.getInstance().getBuyPanelHeight());
        }
        Tower t = new Tower(entityName, nposition, planeWillBeHorizontal);
        if (isAirSupport) {
            t.sprite.setRotation(planeWillBeHorizontal ? Math.PI/2 : Math.PI);
            planeWillBeHorizontal = !planeWillBeHorizontal;
        }
        allInstances.put(t.id, t);
    }

    // deletes all existing Tower entities
    public static void resetAll() {allInstances = new HashMap<Integer,Tower>();}
    
    /**
     * checks if a Tower is placeable at the current location
     *
     * @param placing the sprite that is currently being placed
     * @param panels a list of UI panels that towers cannot be placed on
     * @param lane the lane on which the tower cannot be placed on
     * @param isAirSupport whether the Tower is air support
     * @return whether the placement would be valid or not
     */
    public static boolean getValidPlacement(Sprite placing, List<Sprite> panels, List<Point> lane, boolean isAirSupport) {
        List<Sprite> collidable = new LinkedList<Sprite>();
        if (isAirSupport) return !PathingManager.collisionCheck(placing, panels);
        for (Tower t : allInstances.values()) {collidable.add(t.sprite);}
        for (Sprite s : panels) {collidable.add(s);}
        return !PathingManager.collisionCheck(placing, collidable) & !PathingManager.laneCollisionCheck(placing, lane);
    }

    /**
     * updates all Tower instances and their associated Projectiles
     *
     * @param frameTime amount of real time that one frame is shown for
     */
    public static void updateAll (double frameTime) {
        List<Integer> toRemove = new LinkedList<Integer>();
        for (Map.Entry<Integer,Tower> et : allInstances.entrySet()) {
            Tower t = et.getValue();
            t.attackProgress+=frameTime;
            List<Projectile> activeProjectiles = new LinkedList<Projectile>();
            Sprite s = t.sprite;
            Point current = s.getPosition();
            if (!t.getData().isAirSupport) {
                // new attacks
                List<Enemy> enemiesWithinRange = Enemy.withinRangeOf(current, t.getData().attackRadius);
                if (t.attackProgress>t.currentAttackInterval & !enemiesWithinRange.isEmpty()) {
                    Enemy chosenEnemy = enemiesWithinRange.get(0);
                    t.resetAttackInterval();
                    t.projectiles.add(new Projectile(chosenEnemy, t.getData().attackSpritePath, current));
                    s.setRotation(PathingManager.newRotation(current, chosenEnemy.sprite.getPosition())+SPRITE_ROTATION_OFFSET);
                }
                // track existing projectiles
                for (Projectile p : t.projectiles) {
                    if (p.updateAndCheckTarget(frameTime)) {p.getTarget().damage(t.getData().attackDamage);}
                    else {activeProjectiles.add(p);}
                }
                t.projectiles = activeProjectiles;
            } else {
                // move airplane
                if (t.isHorizontal) s.setPosition(current.x+AIRPLANE_MOVESPEED*frameTime, current.y);
                else s.setPosition(current.x, current.y+AIRPLANE_MOVESPEED*frameTime);
                // create new bomb if ready
                if (s.getPosition().x>Window.getHeight() | s.getPosition().y>Window.getHeight()) {toRemove.add(et.getKey());}
                if (t.attackProgress>t.currentAttackInterval) {
                    t.resetAttackInterval();
                    t.projectiles.add(new Projectile(t.getData().attackSpritePath, s.getPosition()));
                }
                // track and explode existing bombs
                for (Projectile p : t.projectiles) {
                    if (p.updateAndCheckTarget(frameTime)) {
                        for (Enemy e : Enemy.withinRangeOf(p.getPosition(), t.getData().attackRadius)) {
                            e.damage(t.getData().attackDamage);
                        }
                    } else {activeProjectiles.add(p);}
                } t.projectiles = activeProjectiles;
            }
            s.draw();
        }
    }

    private final boolean isHorizontal;
    private double attackProgress = 0.0;
    private double currentAttackInterval;
    private List<Projectile> projectiles = new LinkedList<Projectile>();

    // constructor
    private Tower (String entityName, Point nposition, boolean nisHorizontal) {
        super(entityName, DATA.get(entityName).spritePath);
        super.sprite.setPosition(nposition);
        resetAttackInterval();
        attackProgress = currentAttackInterval;
        isHorizontal = nisHorizontal;
    }

    private TowerData getData() {return DATA.get(super.entityName);}

    private void resetAttackInterval() {
        attackProgress = 0.0;
        currentAttackInterval = Math.random()*(getData().attackIntervalCeil-getData().attackIntervalFloor)+getData().attackIntervalFloor;
    }
}