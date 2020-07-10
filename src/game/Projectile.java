package game;

import bagel.util.Point;

public class Projectile {
    private static final double PROJECTILE_SPEED = 750;
    private static final double BOMB_EXPLODE_DELAY = 2;

    private double aliveFor;
    private Enemy target;
    private final Sprite sprite;

    // constructor
    public Projectile (String nspritePath, Point nposition) {
        sprite = new Sprite(nspritePath, nposition);
    }
    public Projectile (Enemy ntarget, String nspritePath, Point nposition) {
        target = ntarget;
        sprite = new Sprite(nspritePath, nposition);
    }

    // getters
    public Point getPosition() {return sprite.getPosition();}
    public double getAliveFor() {return aliveFor;}
    public Enemy getTarget() {return target;}

    /**
     * updates a Projectile's position and rotation and draws it
     * also checks for target collision and if a bomb has timed out
     *
     * @param frameTime amount of real time that one frame is shown for
     * @return whether the Projectile has reached it target
     */
    public boolean updateAndCheckTarget (double frameTime) {
        aliveFor += frameTime;
        double distMoved = PROJECTILE_SPEED*frameTime;
        if (target != null) {
            sprite.setPosition(PathingManager.newPosition(sprite.getPosition(), target.sprite.getPosition(), distMoved));
            sprite.setRotation(PathingManager.newRotation(sprite.getPosition(), target.sprite.getPosition()));
        }
        sprite.draw();
        return (target != null)? PathingManager.collisionCheck(sprite, target.sprite) : aliveFor>BOMB_EXPLODE_DELAY;
    }
}