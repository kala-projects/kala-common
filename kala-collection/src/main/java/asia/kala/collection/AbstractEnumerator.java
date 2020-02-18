package asia.kala.collection;

public abstract class AbstractEnumerator<E> implements Enumerator<E> {

    @Override
    public String toString() {
        if(hasNext()) {
            return "<non-empty iterator>";
        }
        return "<empty iterator>";
    }
}
