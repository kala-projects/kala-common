package asia.kala.platform;

public enum DataModel {

    ILP32(16, 32, 32, 64),
    LLP64(16, 32, 32, 64),
    LP64(16, 32, 64, 64),
    ;

    public static final DataModel CURRENT = detectDM();

    final int shortSize;
    final int intSize;
    final int longSize;
    final int longLongSize;

    DataModel(int shortSize, int intSize, int longSize, int longLongSize) {
        this.shortSize = shortSize;
        this.intSize = intSize;
        this.longSize = longSize;
        this.longLongSize = longLongSize;
    }

    public int getShortSize() {
        return shortSize;
    }

    public int getIntSize() {
        return intSize;
    }

    public int getLongSize() {
        return longSize;
    }

    public int getLongLongSize() {
        return longLongSize;
    }

    private static DataModel detectDM() {
        if (Platform.is64Bit()) {
            if (Platform.isWindows()) {
                return LLP64;
            }
            return LP64;
        }
        return ILP32;
    }
}
