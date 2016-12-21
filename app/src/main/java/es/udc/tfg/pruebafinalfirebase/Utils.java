package es.udc.tfg.pruebafinalfirebase;

/**
 * Created by Usuario on 19/12/2016.
 */

public class Utils {
    public static String generateValidEmail(String email){
        return email.split("@")[0].replace(".","__");
    }
}
