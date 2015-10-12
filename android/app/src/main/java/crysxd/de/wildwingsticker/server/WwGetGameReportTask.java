package crysxd.de.wildwingsticker.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import crysxd.de.wildwingsticker.model.WwMessageHandler;

/**
 * Created by cwuer on 10/12/2015.
 */
public class WwGetGameReportTask extends AsyncTask<Void, Void, Void> {

    Context mContext;

    public WwGetGameReportTask(Context con) {
        this.mContext = con;

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Log.i(this.getClass().getSimpleName(), "Loading complete game report...");

            URL url = new URL("http://www.bwu-vs.de:8080/wwticker/xloadwwtickerdaten.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder data = new StringBuilder();
            String line;

            while((line = in.readLine()) != null) {
                data.append(line);
                data.append('\n');
            }

            in.close();

            WwMessageHandler handler = new WwMessageHandler(this.mContext);
            handler.handleMessage(new JSONObject(data.toString()), false);

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while loading game report", e);

        }

        return null;

    }
}
