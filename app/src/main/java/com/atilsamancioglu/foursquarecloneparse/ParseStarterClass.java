package com.atilsamancioglu.foursquarecloneparse;

import android.app.Application;

import com.parse.Parse;

public class ParseStarterClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("jFetecctqte7kd6IRLXhCVxYRTPJZUzQ1zyml3X4")
        .clientKey("SZRpEsY976GxjVw9inFufJoXArwPzHu1YD1mYIch")
        .server("https://parseapi.back4app.com/")
        .build()
        );

    }
}
