package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Array;
import java.nio.channels.AlreadyBoundException;
import java.util.Arrays;
import java.util.Objects;

@StaticClass
@SuppressWarnings("unchecked")
public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static <E> E[] wrapInArray(E element) {
        Class<?> cls = element == null ? Object.class : element.getClass();
        Object arr = Array.newInstance(cls, 1);
        Array.set(arr, 0, element);
        return (E[]) arr;
    }

    public static <E> E[][] spilt(@NotNull E[] array, int size) {
        Objects.requireNonNull(array);
        if (size <= 0) {
            throw new IllegalArgumentException();
        }

        int length = array.length;
        if (length == 0) {
            return (E[][]) Array.newInstance(array.getClass(), 0);
        }

        int x = length / size;
        int r = length % size;

        E[][] res = (E[][]) Array.newInstance(array.getClass(), r == 0 ? x : x + 1);

        for (int i = 0; i < x; i++) {
            res[i] = Arrays.copyOfRange(array, i * size, (i + 1) * size);
        }
        if (r != 0) {
            res[x] = Arrays.copyOfRange(array, x * size, x * size + r);
        }

        return res;
    }

    @Contract("null, _ -> null; !null, _ -> !null")
    public static Object deepCopy(Object array, @Range(from = 0, to = Integer.MAX_VALUE) int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("illegal depth: " + depth);
        }

        if (array == null) {
            return null;
        }

        if (depth == 0) {
            return ((Object[]) array).clone();
        }

        int length = Array.getLength(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), length);
        for (int i = 0; i < length; i++) {
            Array.set(newArray, i, deepCopy(Array.get(array, i), depth - 1));
        }
        return newArray;
    }
}
