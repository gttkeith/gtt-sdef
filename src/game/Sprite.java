package game;

import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

/* container for any drawable object within the game
  contains inbuilt storage for position and draw options */
public class Sprite extends Image {
    private Point position = new Point(0.0,0.0);
    private boolean topLeftPos = false; // whether coordinates are relative to center or top-left
    private double rotation = 0.0;
    private boolean useBlend = false;
    private Colour blend = new Colour(0,0,0,1.0);

    // constructors
    public Sprite (String filename) {super(filename);}

    public Sprite (String filename, Point nposition) {
        super(filename);
        position = nposition;
    }

    public Sprite (String filename, Point nposition, boolean ntopLeftPos) {
        super(filename);
        position = nposition;
        topLeftPos = ntopLeftPos;
    }

    public Sprite (String filename, Point nposition, double nrotation) {
        super(filename);
        position = nposition;
        rotation = nrotation;
    }

    // getters
    public Point getPosition() {return position;}
    public double getRotation() {return rotation;}
    public boolean getUseBlend() {return useBlend;}
    public Colour getBlend() {return blend;}

    // setters
    public void setPosition (Point setTo) {position = setTo;}
    public void setPosition (double setToX, double setToY) {position = new Point(setToX,setToY);}
    public void setRotation (double setTo) {rotation = setTo % (2*Math.PI);}
    public void setUseBlend (boolean setTo) {useBlend = setTo;}
    public void setBlend (Colour setTo) {blend = setTo;}

    /**
     * returns the distance from the centre of the Sprite to a set Point
     *
     * @param point the Point which the Sprite is away from
     * @return the Euclidean distance from the Point
     */
    public double getDistanceFrom(Point point) {
        return Math.sqrt(Math.pow(point.x-position.x,2)+Math.pow(point.y-position.y,2));
    }

    // draws the sprite with its stored position and draw options
    public void draw() {
        DrawOptions d = new DrawOptions(); d.setRotation(rotation);
        if (useBlend) d.setBlendColour(blend);
        if (topLeftPos) super.drawFromTopLeft(position.x, position.y,d);
        else super.draw(position.x, position.y, d);
    }

    // creates a bounding box at the sprite's position
    public Rectangle getBoundingBoxAtPosition() {
        if (topLeftPos) return super.getBoundingBoxAt(new Point(position.x+getWidth()/2, position.y+getHeight()/2));
        else return super.getBoundingBoxAt(position);
    }
}