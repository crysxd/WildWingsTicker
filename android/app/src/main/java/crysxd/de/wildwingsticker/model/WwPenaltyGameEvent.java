package crysxd.de.wildwingsticker.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Created by cwuer on 10/4/15.
 */
public class WwPenaltyGameEvent extends WwPlayerCausedGameEvent implements Serializable  {

    public static long serialVersionUID = 2648788746923295399L;

    /**
     * Creates a new {@link WwGameEvent} instance.
     *
     * @param gameEventJson the {@link JSONObject} which represents the JSON object in the ticker feed
     *                      which should be represented by the new object
     * @throws JSONException
     * @throws ParseException
     */
    WwPenaltyGameEvent(JSONObject gameEventJson) throws JSONException, ParseException {
        super(gameEventJson);


    }

    /**
     * Returns weather this penalty was against the home team or the guest.
     *
     * @return true if the  penalty was against the home team
     */
    public boolean isPenaltyAgainstHome() {
        return this.getType() == 2;

    }
}
