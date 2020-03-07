package asia.kala.collection.mutable;

import asia.kala.collection.CollectionFactory;
import asia.kala.collection.immutable.ImmutableInternal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.IntFunction;


public final class LinkedBuffer<E> extends ImmutableInternal.LinkedBufferImpl<E> implements Serializable {

    private static final long serialVersionUID = -711764064295266660L;

    private static final LinkedBuffer.Factory<?> FACTORY = new LinkedBuffer.Factory<>();

    //region Factory methods

    @SuppressWarnings("unchecked")
    public static <E> CollectionFactory<E, ?, LinkedBuffer<E>> factory() {
        return (Factory<E>) FACTORY;
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
    public static <E> LinkedBuffer<E> from(E @NotNull [] elements) {
        return LinkedBuffer.<E>factory().from(elements);
    }

    @NotNull
    public static <E> LinkedBuffer<E> from(@NotNull Iterable<? extends E> iterable) {
        return LinkedBuffer.<E>factory().from(iterable);
    }

    //endregion

    //region MutableCollection

    @Override
    public final String className() {
        return "LinkedBuffer";
    }

    @NotNull
    @Override
    public final <U> CollectionFactory<U, ?, LinkedBuffer<U>> iterableFactory() {
        return factory();
    }

    @NotNull
    @Override
    public final BufferEditor<E, LinkedBuffer<E>> edit() {
        return new BufferEditor<>(this);
    }

    @NotNull
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

    //endregion

    //region Serialization

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.write(size());
        for (E e : this) {
            out.writeObject(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            this.append((E) in.readObject());
        }
    }

    //endregion

    private static final class Factory<E> extends AbstractBufferFactory<E, LinkedBuffer<E>> {
        @Override
        public final LinkedBuffer<E> newBuilder() {
            return new LinkedBuffer<>();
        }
    }
}