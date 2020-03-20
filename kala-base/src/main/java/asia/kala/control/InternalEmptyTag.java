package asia.kala.control;

import java.io.Serializable;

final class InternalEmptyTag implements Serializable {
    private static final long serialVersionUID = 0L;

    private static final int hashMagic = -Option.hashMagic;

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
