package asia.kala.collection.immutable;

import asia.kala.Option;
import asia.kala.collection.Enumerator;
import asia.kala.collection.mutable.AbstractBuffer;
import asia.kala.collection.mutable.LinkedBuffer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

@ApiStatus.Internal
public final class Internal {
    /**
     * Internal implementation of {@link LinkedBuffer}.
     *
     * @see LinkedBuffer
     */
    @ApiStatus.Internal
    public static abstract class LinkedBufferImpl<E> extends AbstractBuffer<E> {
        IList.MCons<E> first = null;
        IList.MCons<E> last = null;

        int len = 0;

        private boolean aliased = false;

        private void ensureUnaliased() {
            if (aliased) {
                LinkedBufferImpl<E> buffer = new LinkedBuffer<>();
                buffer.appendAll(this);
                this.first = buffer.first;
                this.last = buffer.last;
                aliased = false;
            }
        }

        @Override
        public final E get(int index) {
            if (index < 0 || index >= len) {
                throw new IndexOutOfBoundsException("Index out of range: " + index);
            }
            if (index == len - 1) {
                return last.head;
            }

            return first.get(index);
        }

        @NotNull
        @Override
        public final Option<E> getOption(int index) {
            if (index < 0 || index >= len) {
                return Option.none();
            }
            if (index == len - 1) {
                return Option.some(last.head);
            }

            return Option.some(first.get(index));
        }

        @Override
        public final void append(E value) {
            IList.MCons<E> i = new IList.MCons<>(value, IList.nil());
            if (len == 0) {
                first = i;
            } else {
                last.tail = i;
            }
            last = i;
            ++len;
        }

        @Override
        public final void prepend(E value) {
            ensureUnaliased();
            if (len == 0) {
                append(value);
                return;
            }
            first = new IList.MCons<>(value, first);
            ++len;
        }

        @Override
        public void insert(int index, E element) {
            ensureUnaliased();
            if (index < 0 || index > len) {
                throw new IndexOutOfBoundsException("Index out of range: " + index);
            }
            if (index == len) {
                append(element);
                return;
            }

            if (index == 0) {
                prepend(element);
                return;
            }
            ensureUnaliased();
            IList<E> i = first;
            int c = 1;

            while (c++ != index) {
                i = i.tail();
            }

            ((IList.MCons<E>) i).tail = new IList.MCons<>(element, i.tail());
            ++len;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final E remove(int index) {
            if (index < 0 || index >= len) {
                throw new IndexOutOfBoundsException("Index out of range: " + index);
            }

            if (index == 0) {
                E v = first.head;
                if (len == 1) {
                    first = last = null;
                    aliased = false;
                } else {
                    first = (IList.MCons<E>) first.tail;
                }
                --len;
                return v;
            }

            ensureUnaliased();
            IList<E> i = first;
            int c = 1;

            while (c++ != index) {
                i = i.tail();
            }
            E v = i.tail().head();
            ((IList.MCons<E>) i).tail = i.tail().tail();
            --len;
            return v;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final void remove(int index, int count) {
            if (count < 0) {
                throw new IllegalArgumentException("count: " + count);
            }
            if (index < 0 || index + count > len) {
                throw new IndexOutOfBoundsException(String.format("%d to %d is out of bounds", index, index + count));
            }

            if (count == 0) {
                return;
            }
            if (count == 1) {
                remove(index);
                return;
            }
            if (count == len) {
                clear();
                return;
            }
            if (index == 0) {
                int c = count;
                while (c-- > 0) {
                    first = (IList.MCons<E>) first.tail;
                }
                len -= count;
                return;
            }

            ensureUnaliased();
            IList<E> i = first;
            int c = 1;
            while (c++ != index) {
                i = i.tail();
            }

            IList<E> t = i.tail();
            c = count;
            while (c-- > 0) {
                t = t.tail();
            }

            ((IList.MCons<E>) i).tail = t;
            len -= count;
        }

        public final void clear() {
            first = last = null;
            len = 0;
            aliased = false;
        }

        @Override
        public final IList<E> toIList() {
            aliased = true;
            return first;
        }

        @Override
        public final void set(int index, E newValue) {
            int len = this.len;
            if (index < 0 || index >= len) {
                throw new IndexOutOfBoundsException("Index out of range: " + index);
            }

            ensureUnaliased();
            if (index == len - 1) {
                last.head = newValue;
                return;
            }

            IList<E> l = first;
            while (--index >= 0) {
                l = l.tail();
            }
            ((IList.MCons<E>) l).head = newValue;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final void sort(@NotNull Comparator<? super E> comparator) {
            if (len == 0) {
                return;
            }
            Object[] values = toObjectArray();
            Arrays.sort(values, (Comparator<? super Object>) comparator);

            IList.MCons<E> c = first;
            for (Object value : values) {
                //noinspection ConstantConditions
                c.head = (E) value;
                c = c.tail instanceof IList.MCons<?>
                        ? (IList.MCons<E>) c.tail
                        : null;
            }
        }

        @Override
        public final int size() {
            return len;
        }

        @Override
        public final int knownSize() {
            return len;
        }

        @NotNull
        @Override
        public final Enumerator<E> iterator() {
            if (len == 0) {
                return Enumerator.empty();
            }
            return first.iterator();
        }
    }
}
