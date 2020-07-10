package cfg;

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/* represents the data regarding a single event */
public class EventData extends CSVReader {
    public static final String SPAWN = "spawn";
    public static final String DELAY = "delay";
    // event type ordering
    private static final String[] EVENT_TYPES = {SPAWN, DELAY};

    /**
     * returns the number representing the event type, from an event's name
     *
     * @param eventName the name of the event in text
     * @return the number it is mapped to in the EventData map, -1 if not found
     */
    public static int eventTypeFromName(String eventName) {
        for (int i=0; i<EVENT_TYPES.length; i++) {if (EVENT_TYPES[i].equals(eventName)) return i;}
        return -1;
    }

    /**
     * parses event data according to the given waves filepath
     * format: waveNum,eventName,numToSpawn,typeToSpawn,interval
     *
     * @param filePath the waves file to be parsed
     * @return a Map with waveNum as key and list of EventData as value, in the same order as the CSV
     */
    public static NavigableMap<Integer,List<EventData>> loadFromFile(String filePath) {
        NavigableMap<Integer,List<EventData>> result = new TreeMap<Integer,List<EventData>>();
        for (String[] ss : parse(filePath)) {
            int i = 0;
            int nwaveNum = Integer.parseInt(ss[i++]);
            int neventType = eventTypeFromName(ss[i++]);
            // if event is a spawn event, scan for enemy type and amount to spawn
            int nnumToSpawn = 1;
            String nenemyToSpawn = "";
            if (neventType==eventTypeFromName(EventData.SPAWN)) {
                nnumToSpawn = Integer.parseInt(ss[i++]);
                nenemyToSpawn = ss[i++];
            }
            double ninterval = Double.parseDouble(ss[i++])/1000.0; // intervals are stored in seconds
            EventData toBeAdded = new EventData(neventType, nnumToSpawn, nenemyToSpawn, ninterval);
            List<EventData> existingList;
            if (result.containsKey(nwaveNum)) {existingList = result.get(nwaveNum);}
            else {existingList = new LinkedList<EventData>();}
            existingList.add(toBeAdded);
            result.put(nwaveNum, existingList);
        }
        return result;
    }

    // blank event creator
    public static EventData blank() {return new EventData(0,1,"",1.0);}

    // immutable data fields
    public final int eventType;
    public final int numToSpawn;
    public final String enemyToSpawn;
    public final double interval;

    // constructor
    private EventData(int neventType, int nnumToSpawn, String nenemyToSpawn, double ninterval) {
        eventType = neventType;
        numToSpawn = nnumToSpawn;
        enemyToSpawn = nenemyToSpawn;
        interval = ninterval;
    }
}