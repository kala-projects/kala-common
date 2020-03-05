package asia.kala.collection;

import asia.kala.annotations.Covariant;
import asia.kala.collection.mutable.Buffer;
import asia.kala.collection.mutable.MutableCollection;
import asia.kala.collection.mutable.MutableSeq;
import asia.kala.collection.mutable.MutableSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class JDKConverters {
    public static class TraversableAsJava<@Covariant E, C extends Traversable<E>> extends AbstractCollection<E> {
        @NotNull
        protected final C collection;

        public TraversableAsJava(@NotNull C collection) {
            this.collection = collection;
        }

        @Override
        public boolean isEmpty() {
            return collection.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return collection.contains(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return collection.containsAll(c);
        }

        public <T> T[] toArray(IntFunction<T[]> generator) {
            return collection.toArray(generator);
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return collection.toObjectArray();
        }

        @NotNull
        @Override
        @SuppressWarnings("unchecked")
        public final <T> T[] toArray(@NotNull T[] a) {
            Objects.requireNonNull(a);

            T[] arr = toArray(i -> (T[]) Array.newInstance(a.getClass().getComponentType(), i));
            if (a.length < arr.length) {
                return arr;
            }
            System.arraycopy(arr, 0, a, 0, arr.length);
            return a;
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

        @Override
        public int size() {
            return collection.size();
        }
    }

    public static class SeqAsJava<E, C extends Seq<E>> extends AbstractList<E> implements List<E> {
        protected final C collection;

        public SeqAsJava(@NotNull C collection) {
            this.collection = collection;
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E get(int index) {
            return collection.get(index);
        }

        @Override
        public int indexOf(Object o) {
            return collection.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return collection.lastIndexOf(o);
        }

        @Override
        public int size() {
            return collection.size();
        }

        @Override
        public boolean isEmpty() {
            return collection.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return collection.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return collection.containsAll(c);
        }

        public <T> T[] toArray(IntFunction<T[]> generator) {
            return collection.toArray(generator);
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return collection.toObjectArray();
        }

        @NotNull
        @Override
        @SuppressWarnings("unchecked")
        public final <T> T[] toArray(@NotNull T[] a) {
            Objects.requireNonNull(a);

            T[] arr = toArray(i -> (T[]) Array.newInstance(a.getClass().getComponentType(), i));
            if (a.length < arr.length) {
                return arr;
            }
            System.arraycopy(arr, 0, a, 0, arr.length);
            return a;
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

    public static class IndexedSeqAsJava<E, C extends IndexedSeq<E>> extends SeqAsJava<E, C> implements RandomAccess {
        public IndexedSeqAsJava(@NotNull C collection) {
            super(collection);
        }
    }

    public static class SetAsJava<E, C extends Set<E>> extends TraversableAsJava<E, C> implements java.util.Set<E> {
        public SetAsJava(@NotNull C collection) {
            super(collection);
        }

    }

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
            extends SeqAsJava<E, C> {
        public MutableSeqAsJava(@NotNull C collection) {
            super(collection);
        }

        @Override
        public E set(int index, E element) {
            E ans = collection.get(index);
            collection.set(index, element);
            return ans;
        }

        @Override
        public void sort(Comparator<? super E> c) {
            collection.sort(c);
        }

    }

    public static class MutableIndexedSeqAsJava<E, C extends MutableSeq<E> & IndexedSeq<E>>
            extends MutableSeqAsJava<E, C> implements RandomAccess {
        public MutableIndexedSeqAsJava(@NotNull C collection) {
            super(collection);
        }
    }

    public static class BufferAsJava<E, C extends Buffer<E>> extends MutableSeqAsJava<E, C> {
        public BufferAsJava(@NotNull C collection) {
            super(collection);
        }

        @Override
        public boolean add(E e) {
            collection.append(e);
            return true;
        }

        @Override
        public void add(int index, E element) {
            collection.insert(index, element);
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends E> c) {
            collection.insertAll(index, c);
            return !c.isEmpty();
        }

        @Override
        public E remove(int index) {
            return collection.remove(index);
        }

        @Override
        public void clear() {
            collection.clear();
        }
    }

    public static class IndexedBufferAsJava<E, C extends Buffer<E> & IndexedSeq<E>>
            extends BufferAsJava<E, C> implements RandomAccess {
        public IndexedBufferAsJava(@NotNull C collection) {
            super(collection);
        }
    }

    public static class MutableSetAsJava<E, C extends MutableSet<E>>
            extends MutableCollectionAsJava<E, C> implements java.util.Set<E> {
        public MutableSetAsJava(C collection) {
            super(collection);
        }

        // TODO
    }

    public static class CollectionWrapper<E> extends AbstractTraversable<E> implements Traversable<E> {

        @NotNull
        protected final Collection<E> collection;

        public CollectionWrapper(@NotNull Collection<E> collection) {
            this.collection = collection;
        }

        @NotNull
        @Override
        public Enumerator<E> iterator() {
            return Enumerator.fromJava(collection.iterator());
        }

        @NotNull
        @Override
        public Spliterator<E> spliterator() {
            return collection.spliterator();
        }

        @NotNull
        @Override
        public Stream<E> stream() {
            return collection.stream();
        }

        @NotNull
        @Override
        public Stream<E> parallelStream() {
            return collection.parallelStream();
        }

        @NotNull
        @Override
        public Collection<E> asJava() {
            return collection;
        }

        @Override
        public int size() {
            return collection.size();
        }

        @Override
        public int knownSize() {
            return size();
        }
    }

    public static class ListWrapper<E> implements Seq<E> {
        @NotNull
        protected final List<E> list;

        public ListWrapper(@NotNull List<E> list) {
            this.list = list;
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        @SuppressWarnings("SuspiciousMethodCalls")
        public int indexOf(Object value) {
            return list.indexOf(value);
        }

        @Override
        @SuppressWarnings("SuspiciousMethodCalls")
        public int lastIndexOf(Object value) {
            return list.lastIndexOf(value);
        }

        @Override
        @SuppressWarnings("SuspiciousMethodCalls")
        public boolean contains(Object v) {
            return list.contains(v);
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            return Enumerator.fromJava(list.iterator());
        }

        @NotNull
        @Override
        public Enumerator<E> reverseIterator() {
            return new AbstractEnumerator<E>() {
                private final ListIterator<E> it = list.listIterator(list.size());

                @Override
                public final boolean hasNext() {
                    return it.hasPrevious();
                }

                @Override
                public final E next() {
                    return it.previous();
                }
            };
        }

        @NotNull
        @Override
        public final Spliterator<E> spliterator() {
            return list.spliterator();
        }

        @NotNull
        @Override
        public final Stream<E> stream() {
            return list.stream();
        }

        @NotNull
        @Override
        public final Stream<E> parallelStream() {
            return list.parallelStream();
        }

        @NotNull
        @Override
        public List<E> asJava() {
            return list;
        }
    }

    public static class RandomAccessListWrapper<E> extends ListWrapper<E> implements IndexedSeq<E> {
        public RandomAccessListWrapper(@NotNull List<E> list) {
            super(list);
        }
    }

    public static class MutableListWrapper<E>
            extends ListWrapper<E> implements MutableSeq<E> {

        public MutableListWrapper(@NotNull List<E> list) {
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

    public static class RandomAccessMutableListWrapper<E>
            extends MutableListWrapper<E> implements IndexedSeq<E> {
        public RandomAccessMutableListWrapper(@NotNull List<E> list) {
            super(list);
        }
    }

    public static class ResizableListWrapper<E>
            extends MutableListWrapper<E> implements Buffer<E> {
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
