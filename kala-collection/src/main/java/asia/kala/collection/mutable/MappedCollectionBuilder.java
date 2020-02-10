package asia.kala.collection.mutable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

final class MappedCollectionBuilder<E, To, OldTo> implements CollectionBuilder<E, To> {
    @NotNull
    private final CollectionBuilder<E, OldTo> source;

    @NotNull
    private final Function<? super OldTo, ? extends To> mapper;

    MappedCollectionBuilder(@NotNull CollectionBuilder<E, OldTo> source, @NotNull Function<? super OldTo, ? extends To> mapper) {
        assert source != null;
        assert mapper != null;

        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public final void add(E element) {
        source.add(element);
    }

    @Override
    public final void clear() {
        source.clear();
    }

    @Override
    public final To build() {
        return mapper.apply(source.build());
    }

    @Override
    public final void sizeHint(int site) {
        source.sizeHint(site);
    }
}
