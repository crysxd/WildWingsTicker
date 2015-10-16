package crysxd.de.wildwingsticker.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import crysxd.de.wildwingsticker.R;
import crysxd.de.wildwingsticker.view.MainActivity;

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

        /* Create a report (or load the current one and update the meta data */
        WwGameReport report = WwGameReportHolder.ic(this.mContext, score, status);

        /* Parse all events and add them to the report */
        JSONArray events = message.getJSONArray("texte");
        for(int i=0; i<events.length(); i++) {
            JSONObject eventJSON = events.getJSONObject(i);
            WwGameEvent event = report.addGameEvent(eventJSON);

            if(event.getType() != 1 && showNotifications) {
                this.showEventNotification(event, report.isHome(event));

            }
        }

        /* Save the report */
        WwGameReportHolder.persist();

        Log.i(this.getClass().getSimpleName(), "Propagating update...");
        report.propagateUpdate();

    }

    private void showEventNotification(WwGameEvent event, boolean isHome) {
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
                String goal = isHome ? "Tooooor für die" : "Tor für";
                builder.setContentTitle(goal + " " + e2.getPlayerTeamName());
                builder.setContentText(minute + " | " + e2.getScore() + " | " + e2.getPlayer());
                break;
        }

        /* Create Intent */
        Intent i = new Intent(this.mContext, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this.mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Set sound and small icon */
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.ic_stat_default);
        builder.setVibrate(new long[]{400, 200, 400});
        builder.setLights(Color.BLUE, 400, 200);
        builder.setContentIntent(pi);

        /* Notify */
        NotificationManager m = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(event.getId(), builder.build());

    }
}
