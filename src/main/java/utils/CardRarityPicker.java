package utils;

import enums.CardRarity;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class CardRarityPicker {
    private static final NavigableMap<Integer, CardRarity> rarityMap = new TreeMap<>();
    private static final Random random = new Random();

    static {
        int cumulativeWeight = 0;

        for (CardRarity rarity : CardRarity.values()) {
            cumulativeWeight += rarity.getWeight();
            rarityMap.put(cumulativeWeight, rarity);
        }
    }

    public static CardRarity pickRarity() {
        int roll = random.nextInt(100) + 1;
        return rarityMap.ceilingEntry(roll).getValue();
    }
}
