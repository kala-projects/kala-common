package asia.kala.collection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;

@ApiStatus.Internal
public final class JDKConverters {
    public static class SeqAsJava<E, C extends Seq<E>> extends AbstractList<E> {
        @NotNull
        protected final C seq;

        public SeqAsJava(@NotNull C seq) {
            assert seq != null;

            this.seq = seq;
        }

        @Override
        public final int size() {
            return seq.size();
        }

        @Override
        public final boolean isEmpty() {
            return seq.isEmpty();
        }

        @Override
        public final boolean contains(Object o) {
            return seq.contains(o);
        }

        @NotNull
        @Override
        public final Iterator<E> iterator() {
            return seq.iterator();
        }

        @NotNull
        @Override
        public final Object[] toArray() {
            return seq.toArray(Object[]::new);
        }

        public final <T> T[] toArray(IntFunction<T[]> generator) {
            return seq.toArray(generator);
        }

        @NotNull
        @Override
        @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
        public final <T> T[] toArray(@NotNull T[] a) {
            Objects.requireNonNull(a);

            Object[] arr = toArray();
            if (a.length < arr.length) {
                return (T[]) Arrays.copyOf(arr, arr.length, a.getClass());
            }
            System.arraycopy(arr, 0, a, 0, arr.length);
            return a;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            for (Object e : c) {
                if (!seq.contains(e)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public E get(int index) {
            return seq.get(index);
        }

        @Override
        public int indexOf(Object o) {
            return seq.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return seq.lastIndexOf(o);
        }
    }

    public static class IndexedSeqAsJava<E, C extends IndexedSeq<E>> extends SeqAsJava<E, C> implements RandomAccess {
        public IndexedSeqAsJava(@NotNull C seq) {
            super(seq);
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
        public int indexOf(Object element) {
            return list.indexOf(element);
        }

        @Override
        @SuppressWarnings("SuspiciousMethodCalls")
        public int lastIndexOf(Object element) {
            return list.lastIndexOf(element);
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
    }

    public static class RandomAccessListWrapper<E> extends ListWrapper<E> implements IndexedSeq<E> {
        public RandomAccessListWrapper(@NotNull List<E> list) {
            super(list);
        }
    }
}
