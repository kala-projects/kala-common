package asia.kala.collection.mutable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

final class MappedBuilder<E, To, OldTo> implements Builder<E, To> {
    @NotNull
    private final Builder<E, OldTo> source;

    @NotNull
    private final Function<? super OldTo, ? extends To> mapper;

    MappedBuilder(@NotNull Builder<E, OldTo> source, @NotNull Function<? super OldTo, ? extends To> mapper) {
        assert source != null;
        assert mapper != null;

        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public final void append(E element) {
        source.append(element);
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
