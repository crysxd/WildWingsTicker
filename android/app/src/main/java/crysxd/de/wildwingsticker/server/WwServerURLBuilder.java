package crysxd.de.wildwingsticker.server;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cwuer on 10/9/15.
 */
public class WwServerURLBuilder {

    private final String HOST = "wwticker.nunki.uberspace.de";
    private final int MAJOR_SERVER_API_VERSION = 1;
    private final int MINOR_SERVER_API_VERSION = 0;

    private String mProtocol = "https";
    private String mPath;

    public WwServerURLBuilder(String path) {
        this.mPath = path;

    }

    public void setProtocol(String protocol) {
        this.mProtocol = protocol;
    }

    public URL build() throws MalformedURLException {
        return new URL(this.mProtocol + "://" + this.HOST + "/api/" +
                this.MAJOR_SERVER_API_VERSION + "." + this.MINOR_SERVER_API_VERSION + "/" + this.mPath);

    }
}
