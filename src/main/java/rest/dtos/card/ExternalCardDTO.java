package rest.dtos.card;

import rest.dtos.cardSet.ExternalSetDTO;

import java.util.List;

public record ExternalCardDTO(
    String id,
    String name,
    String rarity,
    String flavorText,
    List<String> types,
    List<String> subtypes,
    String evolvesFrom,
    ExternalCardImagesDTO images,
    ExternalSetDTO set,
    CardMarket cardmarket,
    TcgPlayer tcgplayer
) {
    public record CardMarket (CardMarketPrices prices) { }
    public record TcgPlayer (TcgPlayerPrices prices) {}

    public record CardMarketPrices(Double averageSellPrice) { }
    public record TcgPlayerPrices(TcgPlayerNormalPrice normal,
                                  TcgPlayerHolofoilPrice holofoil,
                                  TcgPlayerReverseHolofoilPrice reverseHolofoil) {}

    public record TcgPlayerNormalPrice(Double low,
                                       Double mid,
                                       Double high,
                                       Double market,
                                       Double directLow) {}
    public record TcgPlayerHolofoilPrice(Double low,
                                       Double mid,
                                       Double high,
                                       Double market,
                                       Double directLow) {}
    public record TcgPlayerReverseHolofoilPrice(Double low,
                                         Double mid,
                                         Double high,
                                         Double market,
                                         Double directLow) {}
}