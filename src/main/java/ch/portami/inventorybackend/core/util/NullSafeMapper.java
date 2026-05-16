package ch.portami.inventorybackend.core.util;

import java.util.function.Consumer;
import java.util.function.Function;

public final class NullSafeMapper {

    private NullSafeMapper() {

    }

    public static <T> void applyIfPresent(java.util.function.Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }

    public static <T, R> void applyIfPresent(java.util.function.Supplier<T> getter, Function<T, R> mapper,
            Consumer<R> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(mapper.apply(value));
        }
    }

}
