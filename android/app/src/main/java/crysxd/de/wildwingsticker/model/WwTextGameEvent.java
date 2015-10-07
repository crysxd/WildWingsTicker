package crysxd.de.wildwingsticker.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

/**
 * A special {@link WwGameEvent} which represents a text in the ticker live feed.
 */
public class WwTextGameEvent extends WwGameEvent implements Serializable {

    public static long serialVersionUID = 2648788746923295398L;

    /* The text send */
    private String mText;

    /**
     * Creates a new {@link WwGameEvent} instance.
     *
     * @param gameEventJson the {@link JSONObject} which represents the JSON object in the ticker feed
     *                      which should be represented by the new object
     * @throws JSONException
     * @throws ParseException
     */
    WwTextGameEvent(JSONObject gameEventJson) throws JSONException, ParseException {
        super(gameEventJson);

        /* Save text */
        this.mText = gameEventJson.getString("text");

    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText() {
        return this.mText;

    }
}
