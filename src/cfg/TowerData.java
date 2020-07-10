package cfg;

import java.util.NavigableMap;
import java.util.TreeMap;

/* represents the data regarding a single tower type */
public class TowerData extends CSVReader {
    private static final String DATA_PATH = "res/towers.csv";

    /**
     * parses tower data according to the preset tower data filepath
     * format: entityName;name;spritePath;attackSpritePath;cost;attackDamage;attackIntervalFloor;attackIntervalCeil;attackRadius;isAirSupport
     *
     * @return a Map with entityName as key and TowerData as value, in the same order as the CSV
     */
    public static NavigableMap<String,TowerData> loadFromFile() {
        NavigableMap<String,TowerData> result = new TreeMap<String,TowerData>();
        for (String[] ss : parse(DATA_PATH)) {
            int i = 1;
            String nname = ss[i++];
            String nspritePath = ss[i++];
            String nattackSpritePath = ss[i++];
            int ncost = Integer.parseInt(ss[i++]);
            int nattackDamage = Integer.parseInt(ss[i++]);
            double nattackIntervalFloor = Double.parseDouble(ss[i++]);
            double nattackIntervalCeil = Double.parseDouble(ss[i++]);
            int nattackRadius = Integer.parseInt(ss[i++]);
            boolean nisAirSupport = Boolean.parseBoolean(ss[i++]);
            result.put(ss[0], new TowerData(nname, nspritePath, nattackSpritePath, ncost, nattackDamage, nattackIntervalFloor, nattackIntervalCeil, nattackRadius, nisAirSupport));
        }
        return result.descendingMap();
    }

    // immutable data fields
    public final String name;
    public final String spritePath;
    public final String attackSpritePath;
    public final int cost;
    public final int attackDamage;
    public final double attackIntervalFloor;
    public final double attackIntervalCeil;
    public final double attackRadius;
    public final boolean isAirSupport;

    // constructor
    private TowerData(String nname,
        String nspritePath,
        String nattackSpritePath,
        int ncost,
        int nattackDamage,
        double nattackIntervalFloor,
        double nattackIntervalCeil,
        int nattackRadius,
        boolean nisAirSupport) {
        name = nname;
        spritePath = nspritePath;
        attackSpritePath = nattackSpritePath;
        cost = ncost;
        attackDamage = nattackDamage;
        attackIntervalFloor = nattackIntervalFloor;
        attackIntervalCeil = nattackIntervalCeil;
        attackRadius = nattackRadius;
        isAirSupport = nisAirSupport;
    }
}