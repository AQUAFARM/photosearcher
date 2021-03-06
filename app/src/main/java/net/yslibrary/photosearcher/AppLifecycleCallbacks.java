package net.yslibrary.photosearcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.OkHttpClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import android.util.Log;

import java.io.InputStream;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by yshrsmz on 15/08/24.
 */
public class AppLifecycleCallbacks implements App.LifecycleCallbacks {

    protected final App mApp;

    protected final OkHttpClient mOkHttpClient;

    public AppLifecycleCallbacks(App app, OkHttpClient okHttpClient) {
        this.mApp = app;
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    public void onCreate() {
        setupFabric();
        setupLogger();
        setupGlide();
    }

    /**
     * setup Twitter, Crashlytics
     */
    protected void setupFabric() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                BuildConfig.TWITTER_API_KEY,
                BuildConfig.TWITTER_API_SECRET);
        Fabric.with(mApp, new Twitter(authConfig), new Crashlytics());
    }

    protected void setupLogger() {
        Timber.plant(new CrashReportingTree());
    }

    protected void setupGlide() {
        Glide.get(mApp).register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(mOkHttpClient));
    }

    @Override
    public void onTerminate() {

    }

    /**
     * A tree which logs important information for crash reporting.
     *
     * @see <a href="https://github.com/JakeWharton/timber/blob/master/timber-sample/src/main/java/com/example/timber/ExampleApp.java">JakeWharton/timber
     * sample app</a>
     */
    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Crashlytics.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR || priority == Log.WARN) {
                    Crashlytics.logException(t);
                }
            }
        }
    }
}
