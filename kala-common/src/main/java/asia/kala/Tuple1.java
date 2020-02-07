package asia.kala;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 *  A tuple of 1 element.
 *
 * @param <T1> type of the 1st element
 *
 * @author Glavo
 */
public final class Tuple1<T1> extends HList<T1, Tuple0> implements Serializable {
    private static final long serialVersionUID = -2553287320045901284L;

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * Constructs a tuple of 1 element.
     *
     * @param t1 the 1st element
     */
    public Tuple1(T1 t1) {
        this._1 = t1;
    }

    @SuppressWarnings("unchecked")
    public static <T1> Tuple1<T1> narrow(HList<? extends T1, ? extends Tuple0> tuple) {
        return (Tuple1<T1>) tuple;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int arity() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <U> U elementAt(int index) {
        switch (index) {
            case 0:
                return (U) _1;
            default:
                throw new IndexOutOfBoundsException("Index out of range: " +index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public final <U> U[] toJavaArray(@NotNull IntFunction<U[]> generator) {
        U[] arr = generator.apply(arity());
        arr[0] = (U) this._1;
        return arr;
    }

    /**
     * Returns the 1st element of this tuple.
     *
     * @return the 1st element of this tuple
     */
    public final T1 component1() {
        return _1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T1 head() {
        return _1;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public final Tuple0 tail() {
        return Tuple.of();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract("_ -> new")
    @Override
    public final <H> Tuple2<H, T1> cons(H head) {
        return new Tuple2<>(head, _1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int hash = 0;
        hash = 31 * hash + Objects.hashCode(_1);
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple1<?>)) {
            return false;
        }
        Tuple1<?> t = (Tuple1<?>) o;
        return Objects.equals(_1, t._1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "(" + _1 + ")";
    }
}
