package crysxd.de.wildwingsticker.server;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import crysxd.de.wildwingsticker.R;

/**
 * Created by cwuer on 10/10/15.
 */
public class WwImageLoadTask extends AsyncTask<String, Void, Drawable> {


    private ImageView mImageView;
    private Resources mResources;
    private Context mContext;

    public WwImageLoadTask(Context con, ImageView bmImage) {
        super();
        this.mImageView = bmImage;
        this.mResources = this.mImageView.getResources();
        this.mContext = con;

    }

    protected Drawable doInBackground(String... urls) {
        try {
            InputStream in = new WwCachedUrlInputStream(this.mContext, new URL(urls[0]));
            BitmapDrawable d =  new BitmapDrawable(this.mResources, in);
            in.close();
            return d;

        } catch (FileNotFoundException | InterruptedIOException e) {
            return null;

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

    protected ImageView getImageView() {
        return this.mImageView;

    }
}
