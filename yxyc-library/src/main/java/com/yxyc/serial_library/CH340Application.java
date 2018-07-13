package com.yxyc.serial_library;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.yxyc.serial_library.driver.CH340Driver;

/**
 * Function:CH340Master全局初始化类
 */

public class CH340Application extends Application {

    /**
     * Global application context.
     */
    @SuppressLint("StaticFieldLeak")
    static Context sContext;

    /**
     * Application context is null.
     */
    public static final String APPLICATION_CONTEXT_IS_NULL = "Application context is null. Maybe you need call CH340Application.initialize(Context) method.";

    /**
     * Construct of CommonApplication. Initialize application context.
     */
    public CH340Application() {
        sContext = this;
    }

    /**
     * Use initialize(Context).
     *
     * @param context Application context.
     */
    public static void initialize(Context context) {
        sContext = context;
        initCH340Driver();
    }

    /**
     * init init CH340 driver.
     */
    private static void initCH340Driver() {
        CH340Driver.initCH340(sContext);
    }

    /**
     * Get the global application context.
     *
     * @return Application context.
     */
    public static Context getContext() {
        if (sContext == null) {
            throw new RuntimeException(APPLICATION_CONTEXT_IS_NULL);
        }
        return sContext;
    }
}
