package dev.ioza.kingdom.contracts;

public record TavernRoutePredicate(String name, String value) {

    public static TavernRoutePredicate domain(String value) {
        return new TavernRoutePredicate("Host", value);
    }

    public static TavernRoutePredicate path(String value) {
        return new TavernRoutePredicate("Path", value);
    }

}
