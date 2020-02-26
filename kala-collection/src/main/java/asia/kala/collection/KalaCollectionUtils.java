package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import asia.kala.collection.immutable.IArray;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.collection.mutable.MArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unchecked")
@StaticClass
@ApiStatus.Internal
public final class KalaCollectionUtils {
    public static Object[] asArray(@NotNull Iterable<?> it) {
        assert it != null;
        if (it instanceof MArray<?>) {
            return ((MArray<?>) it).getArray();
        }
        if (it instanceof TraversableOnce<?>) {
            return ((TraversableOnce<?>) it).toArray(Object[]::new);
        }
        if (it instanceof Collection<?>) {
            return ((Collection<?>) it).toArray();
        }
        ArrayBuffer<Object> buffer = new ArrayBuffer<>();
        for (Object o : it) {
            buffer.append(o);
        }
        return buffer.toArray(Object[]::new);
    }

    public static <E> IndexedSeq<E> asIndexedSeq(Object collection) {
        if (collection instanceof IndexedSeq<?>) {
            return (IndexedSeq<E>) collection;
        }
        if (collection instanceof java.util.List<?> && collection instanceof RandomAccess) {
            return new JDKConverters.RandomAccessListWrapper<>(((List<E>) collection));
        }
        if (collection instanceof TraversableOnce<?>) {
            return (MArray<E>) MArray.wrap(((TraversableOnce<?>) collection).toArray(Object[]::new));
        }
        if (collection instanceof Object[]) {
            return MArray.wrap(((E[]) collection));
        }

        if (collection instanceof Collection<?>) {
            return (MArray<E>) MArray.wrap(((Collection<?>) collection).toArray());
        }
        if (collection instanceof Iterable<?>) {
            return ArrayBuffer.from(((Iterable<E>) collection));
        }
        if (collection instanceof Iterator<?>) {
            return ArrayBuffer.from(Enumerator.fromJava(((Iterator<E>) collection)));
        }

        throw new IllegalArgumentException();
    }

    public static <E> IndexedSeq<E> tryToIndexedSeq(Object collection) {
        if (collection instanceof IndexedSeq<?>) {
            return ((IndexedSeq<E>) collection);
        }
        if (collection instanceof List<?> && collection instanceof RandomAccess) {
            return new JDKConverters.RandomAccessListWrapper<>(((List<E>) collection));
        }
        return null;
    }

    public static <E> Seq<E> asSeq(Object collection) {
        if (collection instanceof Seq<?>) {
            return ((Seq<E>) collection);
        }

        if (collection instanceof java.util.List<?>) {
            if (collection instanceof RandomAccess) {
                return new JDKConverters.RandomAccessListWrapper<>((List<E>) collection);
            }
            return new JDKConverters.ListWrapper<>((List<E>) collection);
        }

        if (collection instanceof Object[]) {
            return MArray.wrap(((E[]) collection));
        }

        if (collection instanceof Collection<?>) {
            return (MArray<E>) MArray.wrap(((Collection<?>) collection).toArray());
        }
        if (collection instanceof Iterable<?>) {
            return IArray.from(((Iterable<E>) collection));
        }
        if (collection instanceof Iterator<?>) {
            return IArray.from(Enumerator.fromJava(((Iterator<E>) collection)));
        }

        throw new IllegalArgumentException();
    }

    public static int hash(@NotNull Iterator<?> it) {
        assert it != null;

        int ans = 0;
        while (it.hasNext()) {
            ans = ans * 31 + Objects.hashCode(it.next());
        }
        return ans;
    }

    public static int hash(@NotNull Object[] arr, int start, int count) {
        int ans = 0;
        for (int i = start; i < start + count; i++) {
            ans = 31 * ans + Objects.hashCode(arr[i]);
        }
        return ans;
    }

    public static int knowSize(@NotNull Object collection) {
        if (collection instanceof TraversableOnce<?>) {
            return ((TraversableOnce<?>) collection).knownSize();
        }
        if (collection instanceof Collection<?>) {
            return ((Collection<?>) collection).size();
        }

        return -1;
    }
}
