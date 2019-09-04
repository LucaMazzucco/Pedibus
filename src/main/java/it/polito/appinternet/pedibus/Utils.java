package it.polito.appinternet.pedibus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {


    public static int myCompareUnixDate(long longA, long longB){
        Date a = new Date(longA*1000);
        Date b = new Date(longB*1000);
        return myCompareDate(a,b);
    }

    public static int myCompareUnixTime(long longA, long longB){
        Date a = new Date(longA*1000);
        Date b = new Date(longB*1000);
        return myCompareTime(a,b);
    }

    public static int myCompareDate(Date a, Date b){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        String dateA = sdf.format(a);
        String dateB = sdf.format(b);
        return dateA.compareTo(dateB);
    }

    public static int myCompareTime(Date a, Date b){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dateA = sdf.format(a);
        String dateB = sdf.format(b);
        return dateA.compareTo(dateB);
    }
}
