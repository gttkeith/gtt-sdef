package game;

/* an identifiable game entity, with its own name and ID */
public abstract class GameEntity {
    private static int index = 0;

    public final int id;
    public final String entityName;
    public final Sprite sprite;

    // constructor
    public GameEntity (String nentityName, String spritePath) {
        id = index++;
        entityName = nentityName;
        sprite = new Sprite(spritePath);
    }
}