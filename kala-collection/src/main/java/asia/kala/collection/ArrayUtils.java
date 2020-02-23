package asia.kala.collection;

import asia.kala.annotations.StaticClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

@StaticClass
public final class ArrayUtils {
    private ArrayUtils() {
    }

    @SuppressWarnings("unchecked")
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
}
