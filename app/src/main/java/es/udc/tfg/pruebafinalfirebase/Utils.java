package es.udc.tfg.pruebafinalfirebase;

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
}
