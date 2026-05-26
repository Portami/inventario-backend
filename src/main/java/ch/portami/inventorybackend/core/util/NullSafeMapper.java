package ch.portami.inventorybackend.core.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for null-safe mapping operations.
 *
 * <p>Provides convenience methods to conditionally apply values from a source
 * to a target only when the source value is non-null, avoiding explicit null checks at every call site.
 *
 * <p>This class cannot be instantiated.
 */
public final class NullSafeMapper {

    private NullSafeMapper() {

    }

    /**
     * Applies the value obtained from the given supplier to the given consumer, but only if the value is non-null.
     *
     * @param getter the supplier providing the value to check
     * @param setter the consumer to receive the value if it is non-null
     * @param <T>    the type of the value
     */
    public static <T> void applyIfPresent(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Applies the value obtained from the given supplier to the given consumer after transforming it with the provided
     * mapping function, but only if the source value is non-null.
     *
     * @param getter the supplier providing the source value to check
     * @param mapper the function used to transform the source value before it is passed to the consumer
     * @param setter the consumer to receive the transformed value if the source value is non-null
     * @param <T>    the type of the source value
     * @param <R>    the type of the transformed value
     */
    public static <T, R> void applyIfPresent(Supplier<T> getter, Function<T, R> mapper,
            Consumer<R> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(mapper.apply(value));
        }
    }

}
