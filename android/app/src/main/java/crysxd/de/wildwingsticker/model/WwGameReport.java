package crysxd.de.wildwingsticker.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.TreeMap;

/**
 * A object which represents a collection of {@link WwGameEvent}s.
 */
public class WwGameReport extends TreeMap<Integer, WwGameEvent> {

    /**
     * Restores a persisted {@link WwGameReport} object from the given file
     *
     * @param restoreFile the file in which the object is saved
     * @return the restored object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static WwGameReport restoreGameReport(File restoreFile) throws IOException, ClassNotFoundException {
        ObjectInputStream ois =  new ObjectInputStream(new FileInputStream(restoreFile));
        WwGameReport gameReport = (WwGameReport) ois.readObject();
        ois.close();
        return gameReport;

    }

    private String mGameState;
    private int mGoalsHome;
    private int mGoalsGuest;
    private String mGuestName;
    private String mHomeName;
    private String mGuestNameShort;
    private String mHomeNameShort;
    private int mEventCount;
    private long mLastEventTimestamp;

    /**
     * Creates a completely new instance without any events as entries.
     *
     * @param score the {@link JSONObject} representing the "spielstand" object in the feed
     * @param status the {@link JSONObject} representing the "spielstatus" object in the feed
     */
    public WwGameReport(JSONObject score, JSONObject status) throws JSONException {
        this.setGameMetaData(score, status);

    }

    /**
     * Persist this object into the given file.
     *
     * @param saveFile the file under which the object should be saved.
     */
    public void persist(File saveFile) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
        oos.writeObject(this);
        oos.close();

    }

    /**
     * Adds a {@link WwGameEvent} to the collection. The {@link WwGameEvent} instance is created
     * using the given {@link JSONObject}.
     *
     * @param gameEventJson the {@link JSONObject} representing the evnt.
     *
     * @return the created {@link WwGameEvent} which is also inserted into the collection
     *
     * @throws JSONException
     * @throws ParseException
     */
    public WwGameEvent addGameEvent(JSONObject gameEventJson) throws JSONException, ParseException {
        /* Fetch the event type */
        int type = gameEventJson.getInt("art");
        WwGameEvent gameEvent = null;

        /* create a object based on the type */
        switch(type){
            case 1: gameEvent = new WwTextGameEvent(gameEventJson); break;
            case 2:
            case 3: gameEvent = new WwPenaltyGameEvent(gameEventJson); break;
            case 4:
            case 5: gameEvent = new WwGoalGameEvent(gameEventJson); break;
            default: throw new JSONException("Unknown message type " + type);
        }

        /* Set latest timestamp */
        if(this.mLastEventTimestamp < gameEvent.getTimeSend()) {
            this.mLastEventTimestamp = gameEvent.getTimeSend();
        }

        /* Insert and return */
        this.put(gameEvent.getId(), gameEvent);
        return gameEvent;

    }

    @Override
    public WwGameEvent remove(Object key) {
        throw new RuntimeException("Removing entries from WwGamReport is prohibited.");

    }

    /**
     * Sets the game meta data like scores and team names from the given {@link JSONObject}s.
     *
     * @param score the {@link JSONObject} representing the "spielstand" object in the feed
     * @param status the {@link JSONObject} representing the "spielstatus" object in the feed
     * @throws JSONException
     */
    public void setGameMetaData(JSONObject score, JSONObject status) throws JSONException {
        /* Save the current meta data */
        this.mGameState = status.getString("SpielStatus");
        this.mGoalsHome = score.getInt("ToreHeim");
        this.mGoalsGuest = score.getInt("ToreGast");
        this.mGuestName = score.getString("GastTeam");
        this.mHomeName = score.getString("HeimTeam");
        this.mEventCount = status.getInt("AnzDatenSaetze");

    }

    public String getGameState() {
        return this.mGameState;

    }

    public String getGuestName() {
        return this.mGuestName;

    }

    public String getHomeName() {
        return this.mHomeName;

    }

    public int getGoalsHome() {
        return this.mGoalsHome;

    }

    public int getGoalsGuest() {
        return this.mGoalsGuest;

    }

    public int getEventCount() {
        return this.mEventCount;

    }

    public long getLastEventTimestamp() {
        return this.mLastEventTimestamp;

    }

    public String getHomeNameShort() {
        return this.mHomeNameShort;

    }

    public String getGuestNameShort() {
        return this.mGuestNameShort;

    }

}
