package com.example.just_hungry;

public class Utils {

    private static Utils instance;

    public Utils(){

    }

    public static synchronized Utils getInstance() {
        if (null != instance){
            return instance;
        } else {
            instance = new Utils();
            return instance;
        }
    }
}
