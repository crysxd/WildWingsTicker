package crysxd.de.wildwingsticker.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

/**
 * A special {@link WwGameEvent} which was caused by a player.
 */
public class WwPlayerCausedGameEvent extends WwGameEvent implements Serializable {

    public static long serialVersionUID = 2648788746923295396L;

    /* The name and number of the player who scored */
    private String mPlayer;

    /* The name of the player's team */
    private String mTeamName;

    /* A URL of a image of the player */
    private String mImageUrl;

    /**
     * Creates a new {@link WwGameEvent} instance.
     *
     * @param gameEventJson the {@link JSONObject} which represents the JSON object in the ticker feed
     *                      which should be represented by the new object
     * @throws JSONException
     * @throws ParseException
     */
    WwPlayerCausedGameEvent(JSONObject gameEventJson) throws JSONException, ParseException {
        super(gameEventJson);

        /* player and image url */
        this.mPlayer = gameEventJson.getString("spieler");
        this.mImageUrl = gameEventJson.getString("bild");
        this.mTeamName = gameEventJson.getString("team");

        if(!this.mImageUrl.isEmpty()) {
            this.mImageUrl = "http://www.bwu-vs.de:8080" + this.mImageUrl;

        }
    }

    /**
     * Returns the name and number of the player who scored. Usually this is in the format
     * "fName lName #xx", but this format can not be guaranteed.
     *
     * @return the name and number of the player who scored
     */
    public String getPlayer() {
        return this.mPlayer;

    }

    /**
     * Returns the scorer's team name.
     *
     * @return the scorer's team name
     */
    public String getPlayerTeamName() {
        return this.mTeamName;

    }

    /**
     * Returns a URL pointing to a image of the player who scored. The returned {@link String}
     * may be empty if no image is available.
     *
     * @return a URL pointing to a image of the player who scored
     */
    public String getImageUrl() {
        return this.mImageUrl;

    }

}
