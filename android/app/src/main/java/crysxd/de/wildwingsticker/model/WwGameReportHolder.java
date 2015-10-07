package crysxd.de.wildwingsticker.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A singleton wrapper for {@link WwGameReport} which holds always the currently active game.
 */
public class WwGameReportHolder {

    /* The singleton instance */
    private static WwGameReport mSingleton;

    /* The save file in which the game should be saved */
    private static File mSaveFile;

    /**
     * Returns the current {@link WwGameReport} or creates a new one if necessary.
     *
     * @param con a {@link Context}
     * @param score the {@link JSONObject} representing the "spielstand" object in the feed
     * @param status the {@link JSONObject} representing the "spielstatus" object in the feed
     * @return a {@link WwGameReport} for the current game
     * @throws JSONException
     * @throws IOException
     */
    public static synchronized WwGameReport ic(Context con, JSONObject score, JSONObject status) throws JSONException, IOException {
        /* try to load */
        load(con);

        /* If no game report is available or the loaded one is older the 12h, create a new one */
        if(mSingleton == null || mSingleton.getLastEventTimestamp() < System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12)) {
            mSingleton = new WwGameReport(score, status);
            mSingleton.persist(mSaveFile);

        }

        /* Return it */
        return mSingleton;

    }

    /**
     * Returns the current {@link WwGameReport} or null, if no instance is available
     * @return the current {@link WwGameReport}
     */
    public static synchronized WwGameReport i(Context con) {
        load(con);
        return mSingleton;

    }

    /**
     * Persits the current singleton object into the default save file.
     */
    public static void persist() throws IOException {
        if(mSingleton != null) {
            mSingleton.persist(mSaveFile);
        }
    }

    /**
     * Trys to load the persisted object. If {@link #mSingleton} is already set (not null) nothing is
     * done. If an error occurs, the value of {@link #mSingleton} is not changed.
     *
     * @param con a {@link Context}
     */
    private static void load(Context con) {
        /* Create save file */
        if(mSaveFile == null) {
            mSaveFile = new File(con.getCacheDir(), "gameReport");
        }

        /* If no game report is available and the save file exists, try to load it */
        if(mSingleton == null && mSaveFile.exists()) {
            try {
                mSingleton = WwGameReport.restoreGameReport(mSaveFile);
            } catch (Exception e) {
                Log.e(WwGameReportHolder.class.getSimpleName(), "Error while restoring WwGamReport", e);
            }
        }
    }

}
