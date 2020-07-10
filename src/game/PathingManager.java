package game;

import java.util.LinkedList;
import java.util.List;

import bagel.util.Point;
import bagel.util.Rectangle;

/* collision, movement and dynamic rotation for entities and projectiles */
public class PathingManager {
    private static final double LANE_WIDTH = 25;

    /**
     * computes if a sprite collides with another sprite
     *
     * @param colliding the sprite to be tested for collision
     * @param collidable the sprite that can be collided into
     * @return whether the sprite will collide
     */
    protected static boolean collisionCheck (Sprite colliding, Sprite collidable) {
        Rectangle collidingBound = colliding.getBoundingBoxAtPosition();
        Rectangle collidableBound = collidable.getBoundingBoxAtPosition();
        if (collides(collidingBound, collidableBound)) return true;
        return false;
    }

    /**
     * computes if a sprite collides with any of a list of sprites
     *
     * @param colliding the sprite to be tested for collision
     * @param collidable the sprites that can be collided into
     * @return whether the sprite will collide
     */
    protected static boolean collisionCheck (Sprite colliding, List<Sprite> collidable) {
        for (Sprite s : collidable) {if (collisionCheck(colliding, s)) return true;}
        return false;
    }

    /**
     * computes if a sprite collides with the hitbox of a given lane
     *
     * @param colliding the sprite to be tested for collision
     * @param lane a list of points indicating a lane
     * @return whether the sprite will collide
     */
    protected static boolean laneCollisionCheck (Sprite colliding, List<Point> lane) {
        List<Rectangle> laneBoxes = new LinkedList<Rectangle>();
        for (int i=1; i<lane.size(); i++) {
            Point point1 = lane.get(i-1);
            Point point2 = lane.get(i);
            double angle = Math.atan2((point1.y-point2.y), (point1.x-point2.x));
            if (Math.abs(Math.abs(angle) % (Math.PI/2)-Math.PI/4)<Math.PI/8) {
                int segments = (int)(Math.sqrt(Math.pow(point1.y-point2.y, 2) + Math.pow(point1.x-point2.x, 2))/LANE_WIDTH+0.5)*2;
                double currentX = point1.x;
                double currentY = point1.y;
                for (int j=0; j<segments; j++) {
                    laneBoxes.add(new Rectangle(new Point(currentX-LANE_WIDTH/2, currentY-LANE_WIDTH/2), LANE_WIDTH, LANE_WIDTH));
                    currentX+=(point2.x-point1.x)/segments;
                    currentY+=(point2.y-point1.y)/segments;
                }
            } else {
                Point topLeft = new Point(Math.min(point1.x, point2.x)-LANE_WIDTH/2, Math.min(point1.y, point2.y)-LANE_WIDTH/2);
                laneBoxes.add(new Rectangle(topLeft, Math.abs(point1.x-point2.x)+LANE_WIDTH, Math.abs(point1.y-point2.y)+LANE_WIDTH));
            }
        }
        for (Rectangle laneBox : laneBoxes) {
            if (collides(laneBox, colliding.getBoundingBoxAtPosition())) return true;
        }
        return false;
    }

    /**
     * returns the new position of a moving entity after one interval of movement
     *
     * @param current where the Moveable is at
     * @param destination where the Moveable needs to go
     * @param distMoved the distance that can be moved in one interval
     * @return where the Moveable will be at after one interval of movement
     */
    protected static Point newPosition (Point current, Point destination, double distMoved) {
        boolean staticX = destination.x == current.x;
        boolean staticY = destination.y == current.y;
        // horizontal-only movement
        if (!staticX & staticY) {
            return new Point(moveOnAxis(current.x, destination.x, distMoved), current.y);
        }
        // vertical-only movement
        else if (staticX & !staticY) {
            return new Point(current.x, moveOnAxis(current.y, destination.y, distMoved));
        }
        // diagonal movement
        else if (!staticX & !staticY) {
            double ratio = Math.abs((destination.x-current.x)/(destination.y-current.y));
            // calculate new position using Euclidian distance
            double xMovement = Math.sqrt(distMoved*distMoved/(1+1/(ratio*ratio)));
            double newX = moveOnAxis(current.x, destination.x, xMovement);
            double newY = moveOnAxis(current.y, destination.y, xMovement/ratio);
            return new Point(newX, newY);
        }
        return current;
    }

    /**
     * computes a new rotation angle of a moving entity during an interval of its movement
     *
     * @param current where the Moveable is at
     * @param destination where the Moveable needs to go
     * @return the angle at which the Moveable should face, in radians
     */
    protected static double newRotation (Point current, Point destination) {
        return Math.atan2((destination.y-current.y), (destination.x-current.x));
    }

    /**
     * computes if 2 Rectangles overlap
     *
     * @param r1 a Rectangle
     * @param r2 another Rectangle
     * @return whether the 2 Rectangles overlap
     */
    private static boolean collides (Rectangle r1, Rectangle r2) {
        double r1L = r1.left();
        double r1R = r1.right();
        double r1T = r1.top();
        double r1B = r1.bottom();
        double r2L = r2.left();
        double r2R = r2.right();
        double r2T = r2.top();
        double r2B = r2.bottom();
        return !(r2R<r1L | r1R<r2L | r2B<r1T | r1B<r2T);
    }

    /**
     * computes one-axis movements, moving directly to destination if possible
     *
     * @param current the current value
     * @param destination the value that would ideally be reached
     * @param distMoved the distance that can be moved in one interval
     * @return the moved value after one interval of movement
     */
    private static double moveOnAxis (double current, double destination, double distMoved) {
        boolean withinDistance = Math.abs(destination - current) < distMoved;
        return withinDistance ? destination : current + distMoved * (destination>current ? 1 : -1);
    }
}