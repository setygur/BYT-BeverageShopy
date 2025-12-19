package models.aspects;

public final class HotDrink implements TemperatureAspect {
    @Override public boolean equals(Object o) { return o instanceof HotDrink; }
    @Override public int hashCode() { return 1; }
}
