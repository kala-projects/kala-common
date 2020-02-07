package asia.kala.function;

/**
 * Represents a supplier of results, may throw checked exception.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a functional interface whose functional method is {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of the checked exception
 * @author Glavo
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws E;
}
