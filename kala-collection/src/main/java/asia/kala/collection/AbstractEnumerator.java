package asia.kala.collection;

import asia.kala.annotations.Covariant;

public abstract class AbstractEnumerator<@Covariant E> implements Enumerator<E> {

    @Override
    public String toString() {
        if(hasNext()) {
            return "<non-empty iterator>";
        }
        return "<empty iterator>";
    }
}
