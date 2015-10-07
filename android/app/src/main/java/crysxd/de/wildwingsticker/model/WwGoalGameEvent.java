package crysxd.de.wildwingsticker.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

/**
 * A special {@link WwGameEvent} which represents a goal.
 */
public class WwGoalGameEvent extends WwPlayerCausedGameEvent implements Serializable {

    public static long serialVersionUID = 2648788746923295397L;


    /* The new score in the format x:x */
    private String mScore;

    /* The number of goals for the home team */
    private int mGoalsHome;

    /* The number of goals for the guest team */
    private int mGoalsGuest;

    /**
     * Creates a new {@link WwGameEvent} instance.
     *
     * @param gameEventJson the {@link JSONObject} which represents the JSON object in the ticker feed
     *                      which should be represented by the new object
     * @throws JSONException
     * @throws ParseException
     */
    WwGoalGameEvent(JSONObject gameEventJson) throws JSONException, ParseException {
        super(gameEventJson);

        /* Save score, player and image url */
        this.mScore = gameEventJson.getString("spielstand");
        String[] scoreParts = this.mScore.split(":");
        this.mGoalsHome = Integer.valueOf(scoreParts[0]);
        this.mGoalsGuest = Integer.valueOf(scoreParts[1]);

    }

    /**
     * Returns the new score as String in the format "x:x", where the first x is the home team's
     * goals and the second x is the guest team's goals.
     *
     * @return Returns the new score as String in the format "x:x"
     */
    public String getScore() {
        return this.mScore;

    }

    /**
     * Returns the new number of goals for the home team.
     *
     * @return the new number of goals for the home team
     */
    public int getGoalsHome() {
        return this.mGoalsHome;

    }

    /**
     * Returns the new number of goals for the guest team.
     *
     * @return the new number of goals for the guest team
     */
    public int getGoalsGuest() {
        return this.mGoalsGuest;

    }

    /**
     * Returns weather this goal was scored by the home team or the guest.
     *
     * @return true if the goal was scored by the home team.
     */
    public boolean isGoalForHome() {
        return this.getType() == 4;

    }
}
