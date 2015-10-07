package crysxd.de.wildwingsticker.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A abstract class representing a event in the game. In the ticker feed, every event is represented
 * by a object.
 */
public abstract class WwGameEvent implements Serializable {

    public static long serialVersionUID = 2648788746923295396L;

    /* The unique id */
    private int mId;

    /* The String holding a textual representation of the  game's time */
    private String mTimeString;

    /* The value of mTimeString in ms */
    private long mTime;

    /* The number of minutes since the start of the game */
    private int mMinute;

    /* The UNIX timestamp when the event was send */
    private long mTimeSend;

    /* The type of this event. For further description see #getType() */
    private int mType;

    /**
     * Creates a new {@link WwGameEvent} instance.
     *
     * @param gameEventJson the {@link JSONObject} which represents the JSON object in the ticker feed
     *                      which should be represented by the new object
     * @throws JSONException
     * @throws ParseException
     */
    WwGameEvent(JSONObject gameEventJson) throws JSONException, ParseException {
        /* Save id and time string in format mm:ss */
        this.mId = gameEventJson.getInt("id");
        this.mTimeString = gameEventJson.getString("zeit");
        this.mType = gameEventJson.getInt("art");

        /* Convert the textual representation of the send time into ms since epoch */
        this.mTimeSend = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(gameEventJson.getString("eingabe")).getTime();

        /* Parse the time string into ms */
        String[] timeParts = this.mTimeString.split(":");
        this.mTime = (Integer.valueOf(timeParts[0]) * 60 + Integer.valueOf(timeParts[1])) * 1000;
        this.mMinute = Integer.valueOf(timeParts[0]) + 1;

    }

    /**
     * Returns this event's unique id. Each id represents the number of the event in the stream. The
     * id is reset to 1 in every game. If the Events are sorted by this id, they are ordered by time.
     *
     * @return  this event's unique id
     */
    public int getId() {
        return this.mId;

    }

    /**
     * Returns a {@link String} in the format mm:ss representing the point of time in the game in
     * which the event occurred.
     *
     * @return  {@link String} in the format mm:ss
     */
    public String getTimeString() {
        return this.mTimeString;

    }

    /**
     * Returns the point of time in the game when the event occurs in milliseconds since the start.
     *
     * @return the point of time in the game when the event occurs in milliseconds since the start
     */
    public long getTime() {
        return this.mTime;

    }

    /**
     * Returns a UNIX timestamp in ms representing the point of time when the event was send into the
     * ticker feed.
     *
     * @return a UNIX timestamp in ms
     */
    public long getTimeSend() {
        return this.mTimeSend;

    }

    /**
     * Returns the number of minutes since the game start. The lowest value is 1.
     *
     * @return the number of minutes since the game start
     */
    public int getMinute() {
        return this.mMinute;
    }

    /**
     * Returns the type of this event. Possible return values:
     * 1: Text Event<br>
     * 2: Penalty home<br>
     * 3: Penalty guest<br>
     * 4: Goal Home<br>
     * 5: Goal Guest<br>
     *
     * @return the type of this event
     */
    public int getType() {
        return mType;
    }
}
