package cfg;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/* represents the data regarding a single level, containing a map of waves */
public class LevelData extends CSVReader {
    private static final String DATA_PATH = "res/levels.csv";

    /**
     * parses level data according to the preset level data filepath
     * format: levelNumber;mapFilePath
     *
     * @return a Map with levelNumber as key and LevelData as value, in the same order as the CSV
     */
    public static NavigableMap<Integer,LevelData> loadFromFile() {
        NavigableMap<Integer,LevelData> result = new TreeMap<Integer,LevelData>();
        for (String[] ss : parse(DATA_PATH)) {
            result.put(Integer.parseInt(ss[0]), new LevelData(ss[1], EventData.loadFromFile(ss[2])));
        }
        return result;
    }

    public final String mapPath;
    public final NavigableMap<Integer,List<EventData>> waves;

    // constructor
    private LevelData (String nmapPath, NavigableMap<Integer,List<EventData>> nwaves) {
        mapPath = nmapPath;
        waves = nwaves;
    }
}