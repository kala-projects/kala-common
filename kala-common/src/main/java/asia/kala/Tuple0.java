package asia.kala;

import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * A tuple of no elements.
 *
 * @author Glavo
 */
public final class Tuple0 extends Tuple {

    private static final long serialVersionUID = -6688467811848818630L;

    public static final Tuple0 INSTANCE = new Tuple0();

    private Tuple0() {
    }

    //
    // -- Tuple
    //

    @Override
    public final int arity() {
        return 0;
    }

    @Override
    public final <U> U elementAt(int index) {
        throw new IndexOutOfBoundsException("Tuple0.elementAt");
    }

    @NotNull
    @Override
    public final  <U> U[] toJavaArray(@NotNull IntFunction<U[]> generator) {
        return generator.apply(0);
    }

    @Override
    public final <H> Tuple1<H> cons(H head) {
        return new Tuple1<>(head);
    }

    //
    // -- Object
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "()";
    }

    //
    // -- Serializable
    //

    private Object readResolve() {
        return INSTANCE;
    }
}
