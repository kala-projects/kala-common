package asia.kala.collection.mutable;

import asia.kala.collection.TraversableOnce;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Function;

public class BufferEditor<E, C extends Buffer<E>> extends MSeqEditor<E, C> {
    public BufferEditor(@NotNull C source) {
        super(source);
    }

    @NotNull
    @Contract(value = "_ -> this")
    public BufferEditor<E, C> append(E value) {
        source.append(value);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> appendAll(@NotNull Iterable<? extends E> collection) {
        source.appendAll(collection);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> appendAll(@NotNull E[] collection) {
        source.appendAll(collection);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> prepend(E value) {
        source.prepend(value);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> prependAll(@NotNull Iterable<? extends E> collection) {
        source.prependAll(collection);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> prependAll(@NotNull E[] collection) {
        source.prependAll(collection);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public BufferEditor<E, C> insert(int index, E element) {
        source.insert(index, element);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public BufferEditor<E, C> insertAll(int index, @NotNull Iterable<? extends E> elements) {
        source.insertAll(index, elements);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public BufferEditor<E, C> insertAll(int index, @NotNull E[] elements) {
        source.insertAll(index, elements);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> remove(int index) {
        source.remove(index);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public BufferEditor<E, C> remove(int index, int count) {
        source.remove(index, count);
        return this;
    }

    @NotNull
    @Contract("-> this")
    public BufferEditor<E, C> clear() {
        source.clear();
        return this;
    }

    //
    // -- MSeqEditor
    //

    @NotNull
    @Override
    @Contract("_, _ -> this")
    public BufferEditor<E, C> set(int index, E newValue) {
        source.set(index, newValue);
        return this;
    }

    @NotNull
    @Override
    @Contract("_ -> this")
    public BufferEditor<E, C> mapInPlace(@NotNull Function<? super E, ? extends E> mapper) {
        source.mapInPlace(mapper);
        return this;
    }

    @NotNull
    @Override
    @Contract("-> this")
    public BufferEditor<E, C> sort() {
        source.sort();
        return this;
    }

    @Override
    @NotNull
    @Contract("_ -> this")
    public BufferEditor<E, C> sort(@NotNull Comparator<? super E> comparator) {
        source.sort(comparator);
        return this;
    }
}
