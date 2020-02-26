package asia.kala.collection.mutable;

import asia.kala.collection.KalaCollectionUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.IntFunction;


public final class LinkedBuffer<E> extends asia.kala.collection.immutable.Internal.LinkedBufferImpl<E>
        implements Serializable {
    private static final long serialVersionUID = 1621067498993048170L;
    private static final int hashMagic = -1383198749;

    public static final LinkedBuffer.Factory<?> FACTORY = new LinkedBuffer.Factory<>();

    @SuppressWarnings("unchecked")
    public static <E> LinkedBuffer.Factory<E> factory() {
        return (LinkedBuffer.Factory<E>) FACTORY;
    }

    @NotNull
    @Contract(" -> new")
    public static <E> LinkedBuffer<E> of() {
        return new LinkedBuffer<>();
    }

    @NotNull
    @SafeVarargs
    @Contract("_ -> new")
    public static <E> LinkedBuffer<E> of(E... elements) {
        return from(elements);
    }

    @NotNull
    @Contract("_ -> new")
    public static <E> LinkedBuffer<E> from(@NotNull E[] elements) {
        return LinkedBuffer.<E>factory().from(elements);
    }

    @NotNull
    public static <E> LinkedBuffer<E> from(@NotNull Iterable<? extends E> iterable) {
        return LinkedBuffer.<E>factory().from(iterable);
    }

    //
    // -- MCollection
    //

    @Override
    public final String className() {
        return "LinkedBuffer";
    }

    @NotNull
    @Override
    public final <U> LinkedBuffer.Factory<U> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public final BufferEditor<E, LinkedBuffer<E>> edit() {
        return new BufferEditor<>(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <U> U[] toArray(@NotNull IntFunction<? extends U[]> generator) {
        U[] arr = generator.apply(size());
        int i = 0;
        for (E e : this) {
            arr[i++] = (U) e;
        }
        return arr;
    }

    //
    // -- Object
    //

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LinkedBuffer<?>)) {
            return false;
        }

        LinkedBuffer<?> other = (LinkedBuffer<?>) obj;

        if (size() != other.size()) {
            return false;
        }

        return this.sameElements((LinkedBuffer<?>) obj);
    }

    @Override
    public final int hashCode() {
        return KalaCollectionUtils.hash(iterator()) + hashMagic;
    }

    //
    // -- Serializable
    //

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(toArray(Object[]::new));
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object[] values = (Object[]) in.readObject();
        for (Object e : values) {
            this.append((E) e);
        }
    }

    public static final class Factory<E> extends AbstractBufferFactory<E, LinkedBuffer<E>> {
        Factory() {
        }

        @Override
        public final LinkedBuffer<E> newBuilder() {
            return new LinkedBuffer<>();
        }
    }
}