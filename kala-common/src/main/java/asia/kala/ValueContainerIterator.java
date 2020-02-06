package asia.kala;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

final class ValueContainerIterator<T> implements Iterator<T>, Spliterator<T> {
    private Object valueRef;

    ValueContainerIterator(Object value) {
        this.valueRef = value;
    }

    @Override
    public boolean hasNext() {
        return valueRef != Option.NoneTag.INSTANCE;
    }

    @Override
    public T next() {
        if (valueRef == Option.NoneTag.INSTANCE) {
            throw new NoSuchElementException("OptionIterator.next");
        }
        @SuppressWarnings("unchecked") T v = (T) valueRef;
        valueRef = Option.NoneTag.INSTANCE;
        return v;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action");
        if (hasNext()) {
            action.accept(next());
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return hasNext() ? 1 : 0;
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE | Spliterator.SIZED;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action");
        if (hasNext()) {
            action.accept(next());
        }
    }
}
