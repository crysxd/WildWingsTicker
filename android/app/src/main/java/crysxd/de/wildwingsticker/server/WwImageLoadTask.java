package crysxd.de.wildwingsticker.server;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by cwuer on 10/10/15.
 */
public class WwImageLoadTask extends AsyncTask<String, Void, Drawable> {


    private ImageView mImageView;
    private Resources mResources;

    public WwImageLoadTask(ImageView bmImage) {
        this.mImageView = bmImage;
        this.mResources = this.mImageView.getResources();

    }

    protected Drawable doInBackground(String... urls) {
        try {
            Log.i(this.getClass().getSimpleName(), "Loading " + urls[0]);
            InputStream in = new URL(urls[0]).openStream();
            return new BitmapDrawable(this.mResources, in);

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error while loading image", e);
            return null;

        }
    }

    protected void onPostExecute(Drawable result) {
        if(result != null) {
            this.mImageView.setImageDrawable(result);

        }
    }
}
