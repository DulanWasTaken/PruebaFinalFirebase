package es.udc.tfg.pruebafinalfirebase;

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Usuario on 19/12/2016.
 */

public class Utils {
    public static String generateValidEmail(String email){
        return email.split("@")[0].replace(".","__");
    }

    public static String generateValidPhoneNumber(String number){
        String result = number;
        if (number.startsWith("+")){
            result = result.substring(3);
        }
        result = result.replaceAll("\\s","");

        return result;
    }

    public static String longToDate(long time){
        Date date = new Date(time);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        String result = df2.format(date);

        Date end = new Date();


        long diffInSeconds = (end.getTime() - date.getTime()) / 1000;

        long diff[] = new long[] { 0, 0, 0, 0 };
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        if (diff[0]<5){
            if (diff[0]>1)
                result = ("Hace "+diff[0]+" días");
            else if (diff[0]==1)
                result = ("Ayer");
            else if (diff[1]>1)
                result = ("Hace "+diff[1]+" horas");
            else if (diff[1]==1)
                result = ("Hace una hora");
            else if (diff[2]>1)
                result = ("Hace "+diff[2]+" minutos");
            else if (diff[2]==1)
                result = ("Hace un minuto");
            else
                result = ("Ahora");
        }

        return result;
    }

    public static String longToShortDate(long time){
        Date date = new Date(time);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        String result = df2.format(date);

        Date end = new Date();


        long diffInSeconds = (end.getTime() - date.getTime()) / 1000;

        long diff[] = new long[] { 0, 0, 0, 0 };
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        if (diff[0]<5){
            if (diff[0]>1)
                result = result;
            else if (diff[0]==1)
                result = result;
            else if (diff[1]>1)
                result = ("Today");
            else if (diff[1]==1)
                result = ("Today");
            else if (diff[2]>1)
                result = ("Now");
            else if (diff[2]==1)
                result = ("Now");
            else
                result = ("Now");
        }

        return result;
    }


    public static String listToString (ArrayList<String> groupMembers){
        String result = "";
        for (String member : groupMembers){
            if((result+member).length()<30){
                if (result.equals("")){
                    result=member;
                }else{
                    result = result+", "+member;
                }
            }else{
                result = result+"...";
            }
        }

        return result;
    }

    public static Float stringToHueColor(String str){
        int hash = str.hashCode();
        int red = (hash & 0xFF0000) >> 16;
        int green = (hash & 0x00FF00) >> 8;
        int blue = hash & 0x0000FF;

        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        return hsv[0];
    }
}
