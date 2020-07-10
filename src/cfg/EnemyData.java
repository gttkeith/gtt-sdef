package cfg;

import java.util.NavigableMap;
import java.util.TreeMap;

/* represents the data regarding a single enemy type */
public class EnemyData extends CSVReader {
    private static final String DATA_PATH = "res/enemies.csv";

    /**
     * parses enemy data according to the preset enemy data filepath
     * format: entityName;name;spritePath;maxHealth;reward;penalty;speed;childType;childNum
     *
     * @return a Map with entityName as key and EnemyData as value, in the same order as the CSV
     */
    public static NavigableMap<String,EnemyData> loadFromFile() {
        NavigableMap<String,EnemyData> result = new TreeMap<String,EnemyData>();
        for (String[] ss : parse(DATA_PATH)) {
            int i = 1;
            String nname = ss[i++];
            String nspritePath = ss[i++];
            int nmaxHealth = Integer.parseInt(ss[i++]);
            int nreward = Integer.parseInt(ss[i++]);
            int npenalty = Integer.parseInt(ss[i++]);
            double nspeed = Double.parseDouble(ss[i++]);
            String nchildType = ss[i++];
            int nchildNum = Integer.parseInt(ss[i++]);
            result.put(ss[0], new EnemyData(nname, nspritePath, nmaxHealth, nreward, npenalty, nspeed, nchildType, nchildNum));
        }
        return result.descendingMap();
    }

    // immutable data fields
    public final String name;
    public final String spritePath;
    public final int maxHealth;
    public final int reward;
    public final int penalty;
    public final double speed;
    public final String childType;
    public final int childNum;

    // constructor
    private EnemyData(String nname, String nspritePath, int nmaxHealth, int nreward, int npenalty, double nspeed, String nchildType, int nchildNum) {
        name = nname;
        spritePath = nspritePath;
        maxHealth = nmaxHealth;
        reward = nreward;
        penalty = npenalty;
        speed = nspeed;
        childType = nchildType;
        childNum = nchildNum;
    }
}