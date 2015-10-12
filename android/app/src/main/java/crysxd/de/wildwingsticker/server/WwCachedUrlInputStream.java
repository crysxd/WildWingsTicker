package crysxd.de.wildwingsticker.server;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by cwuer on 10/11/2015.
 */
public class WwCachedUrlInputStream extends InputStream {

    private URL mURL;
    private File mCacheDirectory;
    private InputStream mUnderlyingStream;

    public WwCachedUrlInputStream(Context con, URL source) throws IOException {
        this.mURL = source;
        this.mCacheDirectory = con.getCacheDir();

        if(!this.isCacheFilePresent()) {
            try {
                InputStream in = new BufferedInputStream(source.openStream());
                OutputStream out = new BufferedOutputStream(new FileOutputStream(this.getCacheFile()));
                byte buffer[] = new byte[1024];
                int count = 0;

                while((count = in.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }

                in.close();
                out.close();

            } catch (FileNotFoundException e) {
                this.getCacheFile().createNewFile();

            }
        }

        if(this.isCacheFileEmpty()) {
            throw new FileNotFoundException();
        }

        this.mUnderlyingStream = new BufferedInputStream(new FileInputStream(this.getCacheFile()));

    }

    private boolean isCacheFilePresent() {
        return this.getCacheFile().exists();

    }

    private boolean isCacheFileEmpty() {
        return this.getCacheFile().length() == 0;

    }


    private File getCacheFile() {
        return new File(this.mCacheDirectory, this.mURL.hashCode() + "");

    }

    @Override
    public int read() throws IOException {
        return this.mUnderlyingStream.read();

    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return this.mUnderlyingStream.read(buffer);

    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return this.mUnderlyingStream.read(buffer, byteOffset, byteCount);

    }

    @Override
    public int available() throws IOException {
        return this.mUnderlyingStream.available();

    }

    @Override
    public boolean markSupported() {
        return this.mUnderlyingStream.markSupported();

    }

    @Override
    public void mark(int readlimit) {
        this.mUnderlyingStream.mark(readlimit);

    }

    @Override
    public long skip(long byteCount) throws IOException {
        return this.mUnderlyingStream.skip(byteCount);

    }

    @Override
    public synchronized void reset() throws IOException {
        this.mUnderlyingStream.reset();

    }

    @Override
    public void close() throws IOException {
        this.mUnderlyingStream.close();
    }
}
