package es.udc.tfg.pruebafinalfirebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        SimpleDateFormat dfHours = new SimpleDateFormat("HH:mm");
        //dfHours.setTimeZone(TimeZone.getTimeZone("GTM"));
        String result = df2.format(date);
        String hours = dfHours.format(date);

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
                result = ("Yesterday");
            else if (diff[1]>1)
                result = hours;
            else if (diff[1]==1)
                result = hours;
            else if (diff[2]>1)
                result = hours;
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

    public static float[] stringToHSVColor(String str){
        int hash = str.hashCode();
        int red = (hash & 0xFF0000) >> 16;
        int green = (hash & 0x00FF00) >> 8;
        int blue = hash & 0x0000FF;

        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        return hsv;
    }

    public static String encodeForFirebaseKey(String s) {
        return s
                .replace(".", "%P")
                .replace("$", "%D")
                .replace("#", "%H")
                .replace("[", "%O")
                .replace("]", "%C")
                .replace("/", "%S")
                ;
    }

    public static String decodeFromFirebaseKey(String s) {
        int i = 0;
        int ni;
        String res = "";
        while ((ni = s.indexOf("%", i)) != -1) {
            res += s.substring(i, ni);
            if (ni + 1 < s.length()) {
                char nc = s.charAt(ni + 1);
                if (nc == 'P') {
                    res += '.';
                } else if (nc == 'D') {
                    res += '$';
                } else if (nc == 'H') {
                    res += '#';
                } else if (nc == 'O') {
                    res += '[';
                } else if (nc == 'C') {
                    res += ']';
                } else if (nc == 'S') {
                    res += '/';
                } else {
                    // this case is due to bad encoding
                }
                i = ni + 2;
            } else {
                // this case is due to bad encoding
                break;
            }
        }
        res += s.substring(i);
        return res;
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2, String user, int level,float scale) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.HSVToColor(stringToHSVColor(user)), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bmp1, new Matrix(), paint);

        if(level == MainActivity.NO_LEVEL)
            return bmOverlay;
        bmp2 = generateBadge(bmp2,level,scale);
        bmp2 = Bitmap.createScaledBitmap(bmp2,60,60,false);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public static Bitmap generateBadge (Bitmap bitmap, int level, float scale){
        String text = Integer.toString(level);
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap, new Matrix(),null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextSize((int) (14 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(text, x, y, paint);
        return bmOverlay;
    }
}
