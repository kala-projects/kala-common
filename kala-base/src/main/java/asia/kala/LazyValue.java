package asia.kala;

import asia.kala.annotations.Covariant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LazyValue<@Covariant T> implements Value<T>, Serializable {
    private static final long serialVersionUID = 7403692951772568981L;

    private transient volatile Supplier<? extends T> supplier;
    private T value;

    @Contract("_ -> param1")
    @SuppressWarnings("unchecked")
    public static <T> LazyValue<T> narrow(LazyValue<? extends T> value) {
        return (LazyValue<T>) value;
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> LazyValue<T> of(@NotNull Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        return new LazyValue<>(supplier);
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> LazyValue<T> ofValue(T value) {
        return new LazyValue<>(value);
    }

    private LazyValue(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    private LazyValue(T value) {
        this.value = value;
    }

    //
    // -- Value
    //

    @Override
    public final T get() {
        if (supplier != null) {
            synchronized (this) {
                Supplier<? extends T> s = supplier;
                if (s != null) {
                    value = s.get();
                    supplier = null;
                }
            }
        }
        return value;
    }

    @NotNull
    @Override
    @Contract("_ -> new")
    public final <U> LazyValue<U> map(@NotNull Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return LazyValue.of(() -> mapper.apply(value));
    }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(get());
    }


    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        value = (T) in.readObject();
    }
}
