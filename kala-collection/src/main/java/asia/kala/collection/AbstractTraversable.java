package asia.kala.collection;

public abstract class AbstractTraversable<E> implements Traversable<E> {
    @Override
    public String toString() {
        return joinToString(", ", className() + "[", "]");
    }
}
