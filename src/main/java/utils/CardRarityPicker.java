package utils;

import enums.CardRarityEnum;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class CardRarityPicker {
    private static final NavigableMap<Integer, CardRarityEnum> rarityMap = new TreeMap<>();
    private static final Random random = new Random();

    static {
        int cumulativeWeight = 0;

        for (CardRarityEnum rarity : CardRarityEnum.values()) {
            cumulativeWeight += rarity.getWeight();
            rarityMap.put(cumulativeWeight, rarity);
        }
    }

    public static CardRarityEnum pickRarity() {
        int roll = random.nextInt(100) + 1;
        return rarityMap.ceilingEntry(roll).getValue();
    }
}
