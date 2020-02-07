package asia.kala.platform;

public final class Platform {
    private Platform() {
    }

    public static final OS SYSTEM = OS.CURRENT;
    public static final Arch ARCH = Arch.CURRENT;
    public static final DataModel DATA_MODEL = DataModel.CURRENT;

    public static final int SHORT_SIZE = DATA_MODEL.shortSize;
    public static final int INT_SIZE = DATA_MODEL.intSize;
    public static final int LONG_SIZE = DATA_MODEL.longSize;
    public static final int LONG_LONG_SIZE = DATA_MODEL.longLongSize;

    public static final String PLATFORM =
            (SYSTEM == OS.UNKNOWN || ARCH == Arch.UNKNOWN) ? "UNKNOWN" : SYSTEM + "-" + ARCH;

    public static boolean isDarwin() {
        return SYSTEM.isDarwin();
    }

    public static boolean isMac() {
        return SYSTEM.isMac();
    }

    public static boolean isIOS() {
        return SYSTEM.isIOS();
    }

    public static boolean isWindows() {
        return SYSTEM.isWindows();
    }

    public static boolean isWindowsCE() {
        return SYSTEM.isWindowsCE();
    }

    public static boolean isBSD() {
        return SYSTEM.isBSD();
    }

    public static boolean isLinux() {
        return SYSTEM.isLinux();
    }

    public static boolean isUnix() {
        return SYSTEM.isUnix();
    }

    public static boolean is64Bit() {
        if (Integer.valueOf(64)
                .equals(Integer.getInteger("sun.arch.data.model",
                        Integer.getInteger("com.ibm.vm.bitmode")))) {
            return true;
        }
        return ARCH.is64Bit();
    }


    /**
     * The platform specific standard C library name
     *
     * @return The standard C library name
     */
    public static String getCLibraryName() {
        if (SYSTEM == OS.WINDOWS) {
            return "msvcrt";
        }
        if (SYSTEM == OS.WINDOWSCE) {
            return "coredll";
        }
        if (SYSTEM == OS.AIX) {
            return is64Bit() ? "libc.a(shr.o)" : "libc.a(shr_64.o)";
        }
        if (SYSTEM.isDarwin()) {
            return "System";
        }
        return "c";
    }


}
