package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import asia.kala.collection.immutable.ImmutableArray;
import asia.kala.collection.immutable.ImmutableSeq;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.collection.mutable.LinkedBuffer;
import asia.kala.collection.mutable.MutableArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unchecked")
@StaticClass
@ApiStatus.Internal
public final class KalaCollectionUtils {
    public static Object[] asArray(@NotNull Iterable<?> it) {
        assert it != null;
        if (it instanceof MutableArray<?>) {
            return ((MutableArray<?>) it).getArray();
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
            return (ArraySeq<E>) ArraySeq.wrap(((TraversableOnce<?>) collection).toArray(Object[]::new));
        }
        if (collection instanceof Object[]) {
            return ArraySeq.wrap(((E[]) collection));
        }

        if (collection instanceof Collection<?>) {
            return (ArraySeq<E>) ArraySeq.wrap(((Collection<?>) collection).toArray());
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

    public static <E> Traversable<E> asTraversable(Object collection) {
        if (collection instanceof Traversable<?>) {
            return ((Traversable<E>) collection);
        }

        if (collection instanceof Collection<?>) {
            return new JDKConverters.CollectionWrapper<>(((Collection<E>) collection));
        }

        if (collection instanceof Iterator<?>) {
            Iterator<?> iterator = (Iterator<?>) collection;
            if (!iterator.hasNext()) {
                return ImmutableSeq.empty();
            }
            LinkedBuffer<E> buffer = new LinkedBuffer<>();
            while (iterator.hasNext()) {
                buffer.append(((E) iterator.next()));
            }
            return buffer;
        }

        if (collection instanceof Iterable<?>) {
            LinkedBuffer<E> buffer = new LinkedBuffer<>();
            buffer.appendAll(((Iterable<E>) collection));
            return buffer;
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
            return ArraySeq.wrap(((E[]) collection));
        }

        if (collection instanceof Collection<?>) {
            return (ArraySeq<E>) ArraySeq.wrap(((Collection<?>) collection).toArray());
        }
        if (collection instanceof Iterable<?>) {
            return ImmutableArray.from(((Iterable<E>) collection));
        }
        if (collection instanceof Iterator<?>) {
            return ImmutableArray.from(Enumerator.fromJava(((Iterator<E>) collection)));
        }

        throw new IllegalArgumentException();
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
