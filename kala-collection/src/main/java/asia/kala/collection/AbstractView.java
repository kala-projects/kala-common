package asia.kala.collection;

public abstract class AbstractView<E> implements View<E> {
    @Override
    public String toString() {
        return className() + "[<not computed>]";
    }
}
