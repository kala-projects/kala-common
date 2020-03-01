package asia.kala.collection.mutable;

import asia.kala.collection.IndexedSeq;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class JDKConverters {
    public static class MutableCollectionAsJava<E, C extends MutableCollection<E>> extends AbstractCollection<E> {
        protected final C collection;

        public MutableCollectionAsJava(C collection) {
            this.collection = collection;
        }

        @Override
        public int size() {
            return collection.size();
        }

        public <T> T[] toArray(IntFunction<T[]> generator) {
            return collection.toArray(generator);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return collection.iterator();
        }

        @Override
        public Spliterator<E> spliterator() {
            return collection.spliterator();
        }

        @Override
        public Stream<E> stream() {
            return collection.stream();
        }

        @Override
        public Stream<E> parallelStream() {
            return collection.parallelStream();
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            collection.forEach(action);
        }
    }

    public static class MutableSeqAsJava<E, C extends MutableSeq<E>>
            extends asia.kala.collection.JDKConverters.SeqAsJava<E, C> {
        public MutableSeqAsJava(@NotNull C seq) {
            super(seq);
        }

        @Override
        public E set(int index, E element) {
            E ans = seq.get(index);
            seq.set(index, element);
            return ans;
        }

        @Override
        public void sort(Comparator<? super E> c) {
            seq.sort(c);
        }

    }

    public static class MIndexedSeqAsJava<E, C extends MutableSeq<E> & IndexedSeq<E>>
            extends MutableSeqAsJava<E, C> implements RandomAccess {
        public MIndexedSeqAsJava(@NotNull C seq) {
            super(seq);
        }
    }

    public static class BufferAsJava<E, C extends Buffer<E>> extends MutableSeqAsJava<E, C> {
        public BufferAsJava(@NotNull C seq) {
            super(seq);
        }

        @Override
        public boolean add(E e) {
            seq.append(e);
            return true;
        }

        @Override
        public void add(int index, E element) {
            seq.insert(index, element);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            seq.insertAll(index, c);
            return !c.isEmpty();
        }

        @Override
        public E remove(int index) {
            return seq.remove(index);
        }

        @Override
        public void clear() {
            seq.clear();
        }
    }

    public static class IndexedBufferAsJava<E, C extends Buffer<E> & IndexedSeq<E>>
            extends BufferAsJava<E, C> implements RandomAccess {

        public IndexedBufferAsJava(@NotNull C seq) {
            super(seq);
        }
    }

    public static class ListWrapper<E>
            extends asia.kala.collection.JDKConverters.ListWrapper<E> implements MutableSeq<E> {

        public ListWrapper(@NotNull List<E> list) {
            super(list);
        }

        @Override
        public void set(int index, E newValue) {
            list.set(index, newValue);
        }

        @Override
        public void sort(@NotNull Comparator<? super E> comparator) {
            list.sort(comparator);
        }
    }

    public static class RandomAccessListWrapper<E> extends ListWrapper<E> implements IndexedSeq<E> {
        public RandomAccessListWrapper(@NotNull List<E> list) {
            super(list);
        }
    }

    public static class ResizableListWrapper<E> extends ListWrapper<E> implements Buffer<E> {
        public ResizableListWrapper(@NotNull List<E> list) {
            super(list);
        }

        @Override
        public void append(E value) {
            list.add(value);
        }

        @Override
        public void prepend(E value) {
            list.add(0, value);
        }

        @Override
        public void insert(int index, E element) {
            list.add(index, element);
        }

        @Override
        public E remove(int index) {
            return list.remove(index);
        }

        @Override
        public void clear() {
            list.clear();
        }
    }

    public static class RandomAccessResizableListWrapper<E>
            extends ResizableListWrapper<E> implements IndexedSeq<E> {
        public RandomAccessResizableListWrapper(@NotNull List<E> list) {
            super(list);
        }
    }
}
