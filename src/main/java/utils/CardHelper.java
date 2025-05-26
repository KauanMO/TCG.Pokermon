package utils;


import rest.dtos.card.ExternalCardDTO;

import java.util.ArrayList;
import java.util.List;

public class CardHelper {
    public static double getCardPrice(ExternalCardDTO card) {
        if (card.cardmarket() != null && card.cardmarket().prices().averageSellPrice() != null) {
            return card.cardmarket().prices().averageSellPrice();
        }

        List<Double> prices = new ArrayList<>();

        var normal = card.tcgplayer().prices().normal();
        if (normal != null) {
            addIfNotNull(prices, normal.low(), normal.mid(), normal.high(), normal.market(), normal.directLow());
        }

        var holofoil = card.tcgplayer().prices().holofoil();
        if (holofoil != null) {
            addIfNotNull(prices, holofoil.low(), holofoil.mid(), holofoil.high(), holofoil.market(), holofoil.directLow());
        }

        var reverse = card.tcgplayer().prices().reverseHolofoil();
        if (reverse != null) {
            addIfNotNull(prices, reverse.low(), reverse.mid(), reverse.high(), reverse.market(), reverse.directLow());
        }

        if (prices.isEmpty()) return 0.0;

        return prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private static void addIfNotNull(List<Double> list, Double... values) {
        for (Double v : values) {
            if (v != null) list.add(v);
        }
    }
}
