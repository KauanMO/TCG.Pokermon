package enums;

import lombok.Getter;

@Getter
public enum CardRarityEnum {
    COMMON(30),
    UNCOMMON(20),
    RARE(15),
    RARE_HOLO(8),
    RARE_ULTRA(5),
    RARE_SECRET(3),
    RARE_RAINBOW(2),
    RARE_SHINING(2),
    RARE_SHINY(2),
    RARE_HOLO_EX(1),
    RARE_HOLO_GX(1),
    RARE_HOLO_V(1),
    RARE_HOLO_VMAX(1),
    RARE_HOLO_LV_X(1),
    RARE_HOLO_STAR(1),
    RARE_PRIME(1),
    RARE_ACE(1),
    RARE_BREAK(1),
    RARE_PRISM_STAR(1),
    LEGEND(1),
    PROMO(5),
    AMAZING_RARE(3);

    private final int weight;

    CardRarityEnum(int weight) {
        this.weight = weight;
    }
}