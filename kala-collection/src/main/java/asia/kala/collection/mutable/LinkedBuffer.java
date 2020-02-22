package asia.kala.collection.mutable;

import asia.kala.collection.immutable.IList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public final class LinkedBuffer<E> extends IList.Builder<E> {
    private static final long serialVersionUID = 1621067498993048170L;
    private static final int hashMagic = -1383198749;

    public static final LinkedBuffer.Factory<?> FACTORY = new LinkedBuffer.Factory<>();

    @SuppressWarnings("unchecked")
    public static <E> LinkedBuffer.Factory<E> factory() {
        return (LinkedBuffer.Factory<E>) FACTORY;
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

    public static final class Factory<E> extends AbstractBufferFactory<E, LinkedBuffer<E>> {
        @Override
        public final LinkedBuffer<E> newBuilder() {
            return new LinkedBuffer<>();
        }
    }
}