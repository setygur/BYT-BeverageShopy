package models.aspects;

public final class HoneySweetened implements SweetenerAspect {
    @Override public boolean equals(Object o) { return o instanceof HoneySweetened; }
    @Override public int hashCode() { return 10; }
}
