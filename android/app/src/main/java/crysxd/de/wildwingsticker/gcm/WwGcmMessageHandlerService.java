package crysxd.de.wildwingsticker.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import crysxd.de.wildwingsticker.R;
import crysxd.de.wildwingsticker.model.WwGameEvent;
import crysxd.de.wildwingsticker.model.WwGameReport;
import crysxd.de.wildwingsticker.model.WwGameReportHolder;
import crysxd.de.wildwingsticker.model.WwGoalGameEvent;
import crysxd.de.wildwingsticker.model.WwPenaltyGameEvent;

/**
 * Created by cwuer on 10/3/15.
 */
public class WwGcmMessageHandlerService extends IntentService {

    public WwGcmMessageHandlerService() {
        super("crysxd.de.wildwingsticker.gcm.WwGcmMessageHandlerService");

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(this.getClass().getSimpleName(), "New service");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i(this.getClass().getSimpleName(), "Received message");
            Bundle extras = intent.getExtras();

            /* Create the file under which the last game report is persisted */


            /* Fetch the data from the extras  */
            String json = extras.getString("data");
            int parts = Integer.valueOf(extras.getString("parts_total"));
            int partIndex = Integer.valueOf(extras.getString("part_index"));
            int messageId = Integer.valueOf(extras.getString("message_id"));

            Log.w(this.getClass().getSimpleName(), "Received part " + partIndex + " of " + parts);

            /* If the received data is part of a multipart message */
            if(parts > 1) {
                /* create a MPM instance and save the part */
                WwGcmMultipartMessage mpm = new WwGcmMultipartMessage(this, messageId, parts);
                mpm.addPart(partIndex, json);

                /* If the multipart message is not yet complete, cancel here */
                if(!mpm.isComplete()) {
                    return;
                }

                /* If the message is complete, load it and remove the cache files */
                else {
                    json = mpm.getMessageAndClear();
                }
            }

            /* Parse the data */
            JSONObject data = new JSONObject(json);
            JSONObject score = data.getJSONArray("spielstand").getJSONObject(0);
            JSONObject status = data.getJSONArray("spielstatus").getJSONObject(0);

            /* Create a report */
            WwGameReport report = WwGameReportHolder.ic(this, score, status);

            /* Parse all events and add them to the report */
            JSONArray events = data.getJSONArray("texte");
            for(int i=0; i<events.length(); i++) {
                JSONObject eventJSON = events.getJSONObject(i);
                WwGameEvent event = report.addGameEvent(eventJSON);

                if(event.getType() != 1) {
                    this.showEventNotification(event);

                }
            }

            /* Save the report */
            WwGameReportHolder.persist();

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while creating notification", e);

        } finally {
            WwGcmBroadcastReceiver.completeWakefulIntent(intent);

        }
    }

    private void showEventNotification(WwGameEvent event) {
        /* Create minute String */
        String minute = event.getMinute() + ". Minute";

        /* Create builder */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        /* Set content title and text */
        switch(event.getType()) {
            case 2:
            case 3:
                WwPenaltyGameEvent e1 = (WwPenaltyGameEvent) event;
                builder.setContentTitle("Strafe gegen die " + e1.getPlayerTeamName());
                builder.setContentText(minute + " | " + e1.getPlayer());
                break;
            case 4:
                WwGoalGameEvent e2 = (WwGoalGameEvent) event;
                builder.setContentTitle("Tor für die " + e2.getPlayerTeamName());
                builder.setContentText(minute + " | " + e2.getScore() + " | " + e2.getPlayer());
                break;
            case 5:
                WwGoalGameEvent e3 = (WwGoalGameEvent) event;
                builder.setContentTitle("Toooor für die Wild Wings!");
                builder.setContentText(minute + " | " + e3.getScore() + " | " + e3.getPlayer());
        }

        /* Set sound and small icon */
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.ic_stat_default);

        /* Notify */
        NotificationManager m = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(event.getId(), builder.build());

    }
}
