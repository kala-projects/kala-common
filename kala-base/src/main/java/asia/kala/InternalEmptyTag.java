package asia.kala;

import java.io.Serializable;

final class InternalEmptyTag implements Serializable {
    private static final long serialVersionUID = 0L;

    private static final int hashMagic = 1623337737;

    static final InternalEmptyTag INSTANCE = new InternalEmptyTag();

    private InternalEmptyTag() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public final int hashCode() {
        return hashMagic;
    }
}
