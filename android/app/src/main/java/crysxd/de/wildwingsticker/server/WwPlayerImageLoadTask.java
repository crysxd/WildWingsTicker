package crysxd.de.wildwingsticker.server;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.net.URLEncoder;

/**
 * Created by cwuer on 10/9/15.
 */
public class WwPlayerImageLoadTask extends WwImageLoadTask {

    public WwPlayerImageLoadTask(ImageView bmImage) {
        super(bmImage);
    }

    @Override
    protected Drawable doInBackground(String... names) {
        try {
            String tn = URLEncoder.encode(names[0], "UTF-8").replace("+", "%20");
            String pn = URLEncoder.encode(names[1], "UTF-8").replace("+", "%20");

            String url = new WwServerURLBuilder("image/player/" + tn + "/" + pn).build().toString();
            return super.doInBackground(url);

        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error shil building url", e);
            return null;

        }
    }
}
