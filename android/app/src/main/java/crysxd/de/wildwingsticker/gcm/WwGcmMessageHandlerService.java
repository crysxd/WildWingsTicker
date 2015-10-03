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

import org.json.JSONObject;

import crysxd.de.wildwingsticker.R;

/**
 * Created by cwuer on 10/3/15.
 */
public class WwGcmMessageHandlerService extends IntentService {


    public WwGcmMessageHandlerService() {
        super("crysxd.de.wildwingsticker.gcm.WwGcmMessageHandlerService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(this.getClass().getSimpleName(), "Received message");

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        try {
            String json = extras.getString("data");
            JSONObject data = new JSONObject(json);
            JSONObject spielstand = data.getJSONObject("spielstand");

            String spielstandString = spielstand.getInt("ToreHeim") + ":" + spielstand.getInt("ToreGast");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Neuer Spielstand");
            builder.setContentText(spielstandString);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            builder.setSmallIcon(R.drawable.ic_stat_default);

            NotificationManager m = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            m.notify(0, builder.build());

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while creating notification", e);

        }

        WwGcmBroadcastReceiver.completeWakefulIntent(intent);

    }
}
