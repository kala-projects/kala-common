package asia.kala;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * A tuple of more than 9 elements.
 *
 * @author Glavo
 */
@SuppressWarnings("unchecked")
final class TupleXXL extends HList<Object, HList<?, ?>> {
    private static final long serialVersionUID = -1035728226134523579L;

    @NotNull
    private final Object[] values;

    TupleXXL(@NotNull Object[] values) {
        assert values != null;
        assert values.length > 9;

        this.values = values;
    }

    @Override
    public final int arity() {
        return values.length;
    }

    @Override
    public final <U> U elementAt(int index) {
        try {
            return (U) values[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }

    @Override
    public final <H> HList<H, TupleXXL> cons(H head) {
        int arity = arity();
        Object[] arr = new Object[arity + 1];
        arr[0] = head;
        System.arraycopy(values, 0, arr, 1, arity);
        return new TupleXXL(arr).cast();
    }

    @NotNull
    @Override
    public final Object[] toJavaArray() {
        return values.clone();
    }

    @NotNull
    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public final <U> U[] toJavaArray(@NotNull IntFunction<U[]> generator) {
        U[] arr = generator.apply(arity());
        System.arraycopy(values, 0, arr, 0, arity());
        return arr;
    }

    @Override
    public final Object head() {
        return values[0];
    }

    @Override
    @NotNull
    public final HList<?, ?> tail() {
        int arity = arity();
        if (arity == 10) {
            return new Tuple9<>(
                    values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9]
            );
        }
        return new TupleXXL(Arrays.copyOfRange(values, 1, arity));
    }

    final <U> U cast() {
        return (U) this;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TupleXXL)) {
            return false;
        }
        TupleXXL tupleXXL = (TupleXXL) o;
        return Arrays.equals(values, tupleXXL.values);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public final String toString() {
        int arity = arity();
        String[] strList = new String[arity];
        for (int i = 0; i < arity; i++) {
            strList[i] = Objects.toString(values[i]);
        }
        return "(" + String.join(", ", strList) + ")";
    }
}
