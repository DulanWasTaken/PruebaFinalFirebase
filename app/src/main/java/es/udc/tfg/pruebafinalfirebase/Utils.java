package es.udc.tfg.pruebafinalfirebase;

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
            }
        }

        return result+"...";
    }
}
