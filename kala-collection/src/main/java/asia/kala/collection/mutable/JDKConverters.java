package asia.kala.collection.mutable;

import asia.kala.collection.IndexedSeq;
import asia.kala.collection.Seq;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.RandomAccess;

@ApiStatus.Internal
public final class JDKConverters {
    public static class MSeqAsJava<E, C extends MSeq<E>>
            extends asia.kala.collection.JDKConverters.SeqAsJava<E, C> {
        public MSeqAsJava(@NotNull C seq) {
            super(seq);
        }

        @Override
        public E set(int index, E element) {
            E ans = seq.get(index);
            seq.set(index, element);
            return ans;
        }
    }

    public static class MIndexedSeqAsJava<E, C extends MSeq<E> & IndexedSeq<E>>
            extends MSeqAsJava<E, C> implements RandomAccess {
        public MIndexedSeqAsJava(@NotNull C seq) {
            super(seq);
        }
    }

    public static class BufferAsJava<E, C extends Buffer<E>> extends MSeqAsJava<E, C> {
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
}
