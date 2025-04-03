package utils;

public class StringHelper {
    public static String enumStringBuilder(String rawString) {
        return rawString.toUpperCase().replace(" ", "_");
    }

    public static String generateRarityToQuery() { return "\"" + CardRarityPicker.pickRarity().name().replace("_", " ") + "\""; }
}
