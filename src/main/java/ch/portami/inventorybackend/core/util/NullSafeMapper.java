package ch.portami.inventorybackend.core.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class NullSafeMapper {

    private NullSafeMapper() {

    }

    public static <T> void applyIfPresent(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }

    public static <T, R> void applyIfPresent(Supplier<T> getter, Function<T, R> mapper,
            Consumer<R> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(mapper.apply(value));
        }
    }

}
