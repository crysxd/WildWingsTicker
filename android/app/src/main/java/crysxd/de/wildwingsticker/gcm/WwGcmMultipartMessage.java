package crysxd.de.wildwingsticker.gcm;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by cwuer on 10/4/15.
 */
public class WwGcmMultipartMessage {

    private int mMessageId;
    private int mParts;
    private Context mContext;

    public WwGcmMultipartMessage(Context con, int messageId, int parts) {
        this.mMessageId = messageId;
        this.mParts = parts;
        this.mContext = con;

    }

    public void addPart(int partIndex, String data) throws IOException {
        /* Get file and write */
        File f = this.getFileForPart(partIndex);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(data);
        bw.close();

    }

    private File[] getAllFiles() {
        /* Create array */
        File[] files = new File[this.mParts];

        /* Fill */
        for(int i=0; i<this.mParts; i++) {
            files[i] = this.getFileForPart(i);
        }

        /* Return */
        return files;

    }

    public File getFileForPart(int partIndex) {
        /* Get the cache directory */
        File cacheDir = this.mContext.getCacheDir();

        /* Create file */
        return new File(cacheDir, this.mMessageId + "." + partIndex);

    }

    public boolean isComplete() {
        /* Check if all necessary cache files exist. Return false if one doesn't.*/
        for(File f : this.getAllFiles()) {
            if(!f.exists()) {
                return false;
            }
        }

        /* All exist. Multipart message is completely received */
        return true;

    }

    public String getMessageAndClear() throws IOException {
        /* Create StringBuilder */
        StringBuilder message = new StringBuilder();

        /* Load all files and delete them after loading */
        for(File f : this.getAllFiles()) {
            /* Create reader */
            BufferedReader br = new BufferedReader(new FileReader(f));

            /* Read */
            String line;
            while((line = br.readLine()) != null) {
                message.append(line);
            }

            /* Close the reader and delete the file */
            br.close();
            f.delete();

        }

        /* Return the complete message */
        return message.toString();

    }

}
