package asia.kala;

public interface Value<T> {
    default boolean isReady() {
        return true;
    }


}
