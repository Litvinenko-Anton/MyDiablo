package com.lego.mydiablo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeroUtils {

    private HeroUtils(){}

    public static String castGender(int i){
        if (i == 0){
            return "m";
        }else {
            return "f";
        }
    }

    public static int castGender(String s){
        if ("m".equals(s)){
            return 0;
        }else {
            return 1;
        }
    }

    public static String getDate(long timestamp) {
        try {
            DateFormat sdf = new SimpleDateFormat("mm:ss.S");
            Date netDate = new Date(timestamp);
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

}
