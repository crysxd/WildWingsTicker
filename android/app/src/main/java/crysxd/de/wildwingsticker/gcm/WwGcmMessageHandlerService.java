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
import crysxd.de.wildwingsticker.model.WwMessageHandler;
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

            /* Parse the data and add it to the report */
            WwMessageHandler handler = new WwMessageHandler(this);
            handler.handleMessage(new JSONObject(json), true);

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while creating notification", e);

        } finally {
            WwGcmBroadcastReceiver.completeWakefulIntent(intent);

        }
    }
}
