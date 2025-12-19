package models.aspects;

public final class SugarSweetened implements SweetenerAspect {
    @Override public boolean equals(Object o) { return o instanceof SugarSweetened; }
    @Override public int hashCode() { return 20; }
}
