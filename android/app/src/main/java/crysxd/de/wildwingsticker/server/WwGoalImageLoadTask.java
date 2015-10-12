package crysxd.de.wildwingsticker.server;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.net.URLEncoder;

import crysxd.de.wildwingsticker.R;

/**
 * Created by cwuer on 10/9/15.
 */
public class WwGoalImageLoadTask extends WwImageLoadTask {

    public WwGoalImageLoadTask(Context con, ImageView bmImage) {
        super(con, bmImage);
    }

    @Override
    protected void onPreExecute() {
        this.getImageView().setImageResource(R.drawable.ice);

    }

    @Override
    protected Drawable doInBackground(String... teamName) {
        try {
            String tn = URLEncoder.encode(teamName[0], "UTF-8").replace("+", "%20");
            String url = new WwServerURLBuilder("image/team/" + tn + "/goal").build().toString();
            return super.doInBackground(url);

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while building url", e);
            return null;

        }
    }
}
