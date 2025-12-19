package models.aspects;

public final class ColdDrink implements TemperatureAspect {
    @Override public boolean equals(Object o) { return o instanceof ColdDrink; }
    @Override public int hashCode() { return 2; }
}