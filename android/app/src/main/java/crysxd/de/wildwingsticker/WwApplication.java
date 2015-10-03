package crysxd.de.wildwingsticker;

import android.app.Application;

import crysxd.de.wildwingsticker.server.WwRegisterPushTask;

/**
 * Created by cwuer on 10/3/15.
 */
public class WwApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* Register for push notifications */
        WwRegisterPushTask registerTask = new WwRegisterPushTask(this);
        registerTask.execute();

    }
}
