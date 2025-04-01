package utils;

public class StringHelper {
    public static String enumStringBuilder(String rawString) {
        return rawString.toUpperCase().replace(" ", "_");
    }
}
