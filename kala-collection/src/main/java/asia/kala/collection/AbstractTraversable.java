package asia.kala.collection;

public abstract class AbstractTraversable<E> implements Traversable<E> {

    @Override
    public int hashCode() {
        return KalaCollectionUtils.hash(iterator());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Traversable<?>) || obj instanceof View<?>) {
            return false;
        }

        return this.sameElements(((Traversable<?>) obj));
    }

    @Override
    public String toString() {
        return joinToString(", ", className() + "[", "]");
    }
}
