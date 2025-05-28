package utils;

public class StringHelper {
    public static String enumStringBuilder(String rawString) {
        return rawString.toUpperCase().replace(" ", "_");
    }

    public static String generateRarityToQuery() { return "\"" + CardRarityPicker.pickRarity().name().replace("_", " ") + "\""; }

    public static String hashEmail(String email) {
        if (email == null || !email.contains("@")) return email;

        String[] partes = email.split("@", 2);
        String nome = partes[0];
        String dominio = partes[1];

        String visivel = nome.length() <= 3 ? nome : nome.substring(0, 3);
        String censurado = "*".repeat(Math.max(0, nome.length() - 3));

        return visivel + censurado + "@" + dominio;
    }
}
