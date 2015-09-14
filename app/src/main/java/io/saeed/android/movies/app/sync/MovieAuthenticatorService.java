package io.saeed.android.movies.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access the authenticator.

 */
public class MovieAuthenticatorService extends Service {
    private MovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create new authenticator service
        mAuthenticator = new MovieAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
