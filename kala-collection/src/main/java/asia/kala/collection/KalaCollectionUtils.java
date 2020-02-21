package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import asia.kala.collection.mutable.ArrayBuffer;
import asia.kala.collection.mutable.MArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
}
