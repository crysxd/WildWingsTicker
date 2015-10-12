package crysxd.de.wildwingsticker.model;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import crysxd.de.wildwingsticker.R;

/**
 * Created by cwuer on 10/12/2015.
 */
public class WwMessageHandler {

    private Context mContext;

    public WwMessageHandler(Context con) {
        this.mContext = con;

    }

    public void handleMessage(JSONObject message, boolean showNotifications) throws JSONException, ParseException, IOException {
        JSONObject score = message.getJSONArray("spielstand").getJSONObject(0);
        JSONObject status = message.getJSONArray("spielstatus").getJSONObject(0);

            /* Create a report */
        WwGameReport report = WwGameReportHolder.ic(this.mContext, score, status);

            /* Parse all events and add them to the report */
        JSONArray events = message.getJSONArray("texte");
        for(int i=0; i<events.length(); i++) {
            JSONObject eventJSON = events.getJSONObject(i);
            WwGameEvent event = report.addGameEvent(eventJSON);

            if(event.getType() != 1 && showNotifications) {
                this.showEventNotification(event);

            }
        }

            /* Save the report */
        WwGameReportHolder.persist();

        Log.i(this.getClass().getSimpleName(), "Propagating update...");
        report.propagateUpdate();

    }

    private void showEventNotification(WwGameEvent event) {
        /* Create minute String */
        String minute = event.getMinute() + ". Minute";

        /* Create builder */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.mContext);

        /* Set content title and text */
        switch(event.getType()) {
            case 2:
            case 3:
                WwPenaltyGameEvent e1 = (WwPenaltyGameEvent) event;
                builder.setContentTitle("Strafe gegen die " + e1.getPlayerTeamName());
                builder.setContentText(minute + " | " + e1.getPlayer());
                break;
            case 4:
            case 5:
                WwGoalGameEvent e2 = (WwGoalGameEvent) event;
                builder.setContentTitle("Tor für die " + e2.getPlayerTeamName());
                builder.setContentText(minute + " | " + e2.getScore() + " | " + e2.getPlayer());
                break;
        }

        /* Set sound and small icon */
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.ic_stat_default);

        /* Notify */
        NotificationManager m = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(event.getId(), builder.build());

    }
}
