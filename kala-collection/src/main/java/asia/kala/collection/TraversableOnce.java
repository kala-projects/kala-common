package asia.kala.collection;

import asia.kala.Foldable;
import asia.kala.Functor;
import org.jetbrains.annotations.NotNull;

public interface TraversableOnce<T> extends Iterable<T>, Foldable<T>, Functor<T> {
    @NotNull
    @Override
    Iterator<T> iterator();
}
