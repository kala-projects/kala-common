package asia.kala;

import asia.kala.annotations.StaticClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Objects;

/**
 * Provide methods to simply input and output from the console.
 */
@StaticClass
public final class Terminal {
    private Terminal() {
    }

    private static final int BUFFER_SIZE = 128;

    private static InputStream in;

    private static BufferedReader reader;

    private static BufferedReader reader() {
        final InputStream stdin = System.in;

        if (Terminal.in == stdin) {
            return reader;
        }

        synchronized (Terminal.class) {
            if (Terminal.in == stdin) {
                return reader;
            }

            if (stdin == null) {
                Terminal.in = null;
                Terminal.reader = null;
                return null;
            }

            final BufferedReader reader = new BufferedReader(new InputStreamReader(stdin), BUFFER_SIZE);

            Terminal.in = stdin;
            Terminal.reader = reader;

            return reader;
        }
    }

    public static InputStream stdin() {
        return System.in;
    }

    public static PrintStream stdout() {
        return System.out;
    }

    public static PrintStream stderr() {
        return System.err;
    }

    @NotNull
    public static String readLine() {
        try {
            return Objects.requireNonNull(reader(), "System.in is null")
                    .readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    public static String readLine(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        return readLine();
    }

    @NotNull
    public static char[] readPassword() throws UnsupportedOperationException {
        final java.io.Console console = System.console();
        if (console == null) {
            throw new UnsupportedOperationException("System.console() is null");
        }
        return console.readPassword();
    }

    @NotNull
    public static char[] readPassword(String prompt) throws UnsupportedOperationException {
        final java.io.Console console = System.console();
        if (console == null) {
            throw new UnsupportedOperationException("System.console() is null");
        }
        return console.readPassword("%s", prompt);
    }

    public static char readChar() throws InputMismatchException {
        String s = readLine();
        if (s.length() != 1) {
            throw new InputMismatchException("For input string: \"" + s + '"');
        }
        return s.charAt(0);
    }

    public static char readChar(String prompt) throws InputMismatchException {
        String s = readLine(prompt);
        if (s.length() != 1) {
            throw new InputMismatchException("For input string: \"" + s + '"');
        }
        return s.charAt(0);
    }

    @Nullable
    public static Character readCharOrNull() {
        String s = readLine();
        if (s.length() != 1) {
            return null;
        }
        return s.charAt(0);
    }

    @Nullable
    public static Character readCharOrNull(String prompt) {
        String s = readLine(prompt);
        if (s.length() != 1) {
            return null;
        }
        return s.charAt(0);
    }

    public static int readInt() throws InputMismatchException {
        return readInt(10);
    }

    public static int readInt(int radix) throws InputMismatchException {
        try {
            return Integer.parseInt(readLine().trim(), radix);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    public static int readInt(String prompt) throws InputMismatchException {
        return readInt(prompt, 10);
    }

    public static int readInt(String prompt, int radix) throws InputMismatchException {
        try {
            return Integer.parseInt(readLine(prompt).trim(), radix);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @Nullable
    public static Integer readIntOrNull() {
        return readIntOrNull(10);
    }

    @Nullable
    public static Integer readIntOrNull(int radix) {
        try {
            return Integer.parseInt(readLine().trim(), radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Integer readIntOrNull(String prompt) {
        return readIntOrNull(prompt, 10);
    }

    @Nullable
    public static Integer readIntOrNull(String prompt, int radix) {
        try {
            return Integer.parseInt(readLine(prompt).trim(), radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static long readLong() throws InputMismatchException {
        return readLong(10);
    }

    public static long readLong(int radix) throws InputMismatchException {
        try {
            return Long.parseLong(readLine().trim(), radix);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    public static long readLong(String prompt) throws InputMismatchException {
        return readLong(prompt, 10);
    }

    public static long readLong(String prompt, int radix) throws InputMismatchException {
        try {
            return Long.parseLong(readLine(prompt).trim(), radix);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @Nullable
    public static Long readLongOrNull() {
        return readLongOrNull(10);
    }

    @Nullable
    public static Long readLongOrNull(int radix) {
        try {
            return Long.parseLong(readLine().trim(), radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Long readLongOrNull(String prompt) {
        return readLongOrNull(prompt, 10);
    }

    @Nullable
    public static Long readLongOrNull(String prompt, int radix) {
        try {
            return Long.parseLong(readLine(prompt).trim(), radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static BigInteger readBigInteger() throws InputMismatchException {
        return readBigInteger(10);
    }

    @NotNull
    public static BigInteger readBigInteger(int radix) throws InputMismatchException {
        try {
            return new BigInteger(readLine().trim(), radix);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @NotNull
    public static BigInteger readBigInteger(String prompt) throws InputMismatchException {
        return readBigInteger(prompt, 10);
    }

    @NotNull
    public static BigInteger readBigInteger(String prompt, int radix) throws InputMismatchException {
        try {
            return new BigInteger(readLine(prompt).trim(), radix);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @Nullable
    public static BigInteger readBigIntegerOrNull() throws InputMismatchException {
        return readBigIntegerOrNull(10);
    }

    @Nullable
    public static BigInteger readBigIntegerOrNull(int radix) throws InputMismatchException {
        try {
            return new BigInteger(readLine().trim(), radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static BigInteger readBigIntegerOrNull(String prompt) throws InputMismatchException {
        return readBigIntegerOrNull(prompt, 10);
    }

    @Nullable
    public static BigInteger readBigIntegerOrNull(String prompt, int radix) throws InputMismatchException {
        try {
            return new BigInteger(readLine(prompt).trim(), radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static double readDouble() throws InputMismatchException {
        try {
            return Double.parseDouble(readLine().trim());
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    public static double readDouble(String prompt) throws InputMismatchException {
        try {
            return Double.parseDouble(readLine(prompt).trim());
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @Nullable
    public static Double readDoubleOrNull() throws InputMismatchException {
        try {
            return Double.parseDouble(readLine().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Double readDoubleOrNull(String prompt) throws InputMismatchException {
        try {
            return Double.parseDouble(readLine(prompt).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static BigDecimal readBigDecimal() throws InputMismatchException {
        try {
            return new BigDecimal(readLine().trim());
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @NotNull
    public static BigDecimal readBigDecimal(String prompt) throws InputMismatchException {
        try {
            return new BigDecimal(readLine(prompt).trim());
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @NotNull
    public static BigDecimal readBigDecimal(@NotNull MathContext mc) throws InputMismatchException {
        try {
            return new BigDecimal(readLine().trim(), mc);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @NotNull
    public static BigDecimal readBigDecimal(String prompt, @NotNull MathContext mc) throws InputMismatchException {
        try {
            return new BigDecimal(readLine(prompt).trim(), mc);
        } catch (NumberFormatException e) {
            throw new InputMismatchException(e.getMessage());
        }
    }

    @Nullable
    public static BigDecimal readBigDecimalOrNull() throws InputMismatchException {
        try {
            return new BigDecimal(readLine().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static BigDecimal readBigDecimalOrNull(String prompt) throws InputMismatchException {
        try {
            return new BigDecimal(readLine(prompt).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static BigDecimal readBigDecimalOrNull(@NotNull MathContext mc) throws InputMismatchException {
        try {
            return new BigDecimal(readLine().trim(), mc);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static BigDecimal readBigDecimalOrNull(String prompt, @NotNull MathContext mc) throws InputMismatchException {
        try {
            return new BigDecimal(readLine(prompt).trim(), mc);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void print(Object x) {
        if (x == null) {
            System.out.print("null");
        } else if (x.getClass().isArray()) {
            if (x instanceof Object[]) {
                print((Object[]) x);
            } else if (x instanceof char[]) {
                print((char[]) x);
            } else if (x instanceof boolean[]) {
                print((boolean[]) x);
            } else if (x instanceof byte[]) {
                print((byte[]) x);
            } else if (x instanceof short[]) {
                print((short[]) x);
            } else if (x instanceof int[]) {
                print((int[]) x);
            } else if (x instanceof long[]) {
                print((long[]) x);
            } else if (x instanceof float[]) {
                print((float[]) x);
            } else if (x instanceof double[]) {
                print((double[]) x);
            } else {
                throw new AssertionError("Unknown array type: " + x.getClass());
            }
        } else {
            System.out.print(x);
        }
    }

    public static void print(String x) {
        System.out.print(x);
    }

    public static void print(char x) {
        System.out.print(x);
    }

    public static void print(boolean x) {
        System.out.print(x);
    }

    public static void print(byte x) {
        System.out.print(x);
    }

    public static void print(short x) {
        System.out.print(x);
    }

    public static void print(int x) {
        System.out.print(x);
    }

    public static void print(long x) {
        System.out.print(x);
    }

    public static void print(float x) {
        System.out.print(x);
    }

    public static void print(double x) {
        System.out.print(x);
    }

    public static void print(Object[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(char[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(boolean[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(byte[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(short[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(int[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(long[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(float[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void print(double[] array) {
        if (array == null) {
            System.out.print("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.print(']');
        }
    }

    public static void println() {
        System.out.println();
    }

    public static void println(Object x) {
        if (x == null) {
            System.out.println("null");
        } else if (x.getClass().isArray()) {
            if (x instanceof Object[]) {
                println((Object[]) x);
            } else if (x instanceof char[]) {
                println((char[]) x);
            } else if (x instanceof boolean[]) {
                println((boolean[]) x);
            } else if (x instanceof byte[]) {
                println((byte[]) x);
            } else if (x instanceof short[]) {
                println((short[]) x);
            } else if (x instanceof int[]) {
                println((int[]) x);
            } else if (x instanceof long[]) {
                println((long[]) x);
            } else if (x instanceof float[]) {
                println((float[]) x);
            } else if (x instanceof double[]) {
                println((double[]) x);
            } else {
                throw new AssertionError("Unknown array type: " + x.getClass());
            }
        } else {
            System.out.println(x);
        }
    }

    public static void println(String x) {
        System.out.println(x);
    }

    public static void println(char x) {
        System.out.println(x);
    }

    public static void println(boolean x) {
        System.out.println(x);
    }

    public static void println(byte x) {
        System.out.println(x);
    }

    public static void println(short x) {
        System.out.println(x);
    }

    public static void println(int x) {
        System.out.println(x);
    }

    public static void println(long x) {
        System.out.println(x);
    }

    public static void println(float x) {
        System.out.println(x);
    }

    public static void println(double x) {
        System.out.println(x);
    }

    public static void println(Object[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(char[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(boolean[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(byte[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(short[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(int[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(long[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(float[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void println(double[] array) {
        if (array == null) {
            System.out.println("null");
            return;
        }

        final int size = array.length;
        final PrintStream out = System.out;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (out) {
            out.print('[');
            if (size > 0) {
                out.print(array[0]);
                for (int i = 1; i < size; i++) {
                    out.print(", ");
                    out.print(array[i]);
                }
            }
            out.println(']');
        }
    }

    public static void printf(@NotNull String format, Object... args) {
        System.out.printf(format, args);
    }

    public static void printf(Locale locale, @NotNull String format, Object... args) {
        System.out.printf(locale, format, args);
    }

    public static void flushStdout() {
        System.out.flush();
    }
}
