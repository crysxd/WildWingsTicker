package crysxd.de.wildwingsticker.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.util.HashMap;
import java.util.Map;

import crysxd.de.wildwingsticker.BuildConfig;
import crysxd.de.wildwingsticker.R;

/**
 * Created by cwuer on 10/3/15.
 */
public class WwRegisterPushTask extends AsyncTask<Void, Void, Void> {

    /* A context */
    private final Context CONTEXT;

    public WwRegisterPushTask(Context con) {
        this.CONTEXT = con;

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            /* Fetch token for GCM */
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this.CONTEXT.getApplicationContext());
            String id = gcm.register(this.CONTEXT.getString(R.string.gcm_project_number));

            /* Log */
            if(BuildConfig.DEBUG) {
                Log.i(this.getClass().getSimpleName(), "Will register for push notifications with id " + id);
            }

            /* Create API call parameters */
            Map<String, String> apiCallParams = new HashMap<>();
            apiCallParams.put("id", id);

            /* Create API call */
            WwServerApiCall apiCall = new WwServerApiCall("push/register");
            apiCall.performPostApiCall(apiCallParams);

            /* Log */
            if(BuildConfig.DEBUG) {
                Log.i(this.getClass().getSimpleName(), "Registration successful");
            }

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while registering for push notifications", e);

        }

        return null;

    }
}
