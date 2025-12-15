/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of INumberList interface.
 * Has to be implemented by each student independently.
 *
 * @author Alexander Podrubailo
 *
 */
public class NumberListImpl implements NumberList {

    /**
     * Internal node for circular singly linked list.
     * Index 0 corresponds to head (most significant digit).
     */
    private static class Node {
        byte value;
        Node next;

        Node(byte v) {
            this.value = v;
            this.next = this;
        }
    }

    private Node head;      
    private int size;
    private int base = 3;   

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
        this.base = 3;
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file", e);
        }
        String content = sb.toString();
        if (content.length() > 0) {
            initFromDecimalString(content);
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        if (value != null && value.trim().length() > 0) {
            initFromDecimalString(value.trim());
        }
    }

    private void initFromDecimalString(String decimalStr) {
        if (decimalStr.startsWith("+")) {
            decimalStr = decimalStr.substring(1);
        }
        for (char c : decimalStr.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException("Invalid decimal string: " + decimalStr);
            }
        }
        BigInteger val = new BigInteger(decimalStr);
        if (val.compareTo(BigInteger.ZERO) == 0) {
            add((byte) 0);
            return;
        }

        java.util.ArrayList<Byte> digits = new java.util.ArrayList<>();
        BigInteger bBase = BigInteger.valueOf(base);
        while (val.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] dr = val.divideAndRemainder(bBase);
            val = dr[0];
            byte digit = dr[1].byteValue();
            digits.add(digit);
        }
        for (int i = digits.size() - 1; i >= 0; --i) {
            add(digits.get(i));
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        String s = toDecimalString();
        try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
            fw.write(s);
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file", e);
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 8971;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger value = toBigInteger();
        NumberListImpl res = new NumberListImpl();
        res.base = 8;
        if (value.compareTo(BigInteger.ZERO) == 0) {
            res.add((byte) 0);
            return res;
        }
        BigInteger b8 = BigInteger.valueOf(8);
        java.util.ArrayList<Byte> digits = new java.util.ArrayList<>();
        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] dr = value.divideAndRemainder(b8);
            value = dr[0];
            digits.add((byte) dr[1].intValue());
        }
        for (int i = digits.size() - 1; i >= 0; --i) {
            res.add(digits.get(i));
        }
        return res;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null) {
            throw new IllegalArgumentException("arg is null");
        }
        BigInteger a = this.toBigInteger();
        BigInteger b;
        int argBase = 3;
        if (arg instanceof NumberListImpl) {
            NumberListImpl other = (NumberListImpl) arg;
            argBase = other.base;
            b = other.toBigInteger();
        } else {
            BigInteger tmp = BigInteger.ZERO;
            for (int i = 0; i < arg.size(); ++i) {
                Byte d = arg.get(i);
                tmp = tmp.multiply(BigInteger.TEN).add(BigInteger.valueOf(d));
            }
            b = tmp;
        }
        if (b.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Division by zero in additionalOperation");
        }
        BigInteger r = a.mod(b);
        NumberListImpl result = new NumberListImpl(r.toString()); 
        if (this.base != 3) {
            NumberListImpl tmp = new NumberListImpl();
            tmp.base = this.base;
            if (r.equals(BigInteger.ZERO)) {
                tmp.add((byte) 0);
                return tmp;
            }
            BigInteger bBase = BigInteger.valueOf(this.base);
            java.util.ArrayList<Byte> digits = new java.util.ArrayList<>();
            while (r.compareTo(BigInteger.ZERO) > 0) {
                BigInteger[] dr = r.divideAndRemainder(bBase);
                r = dr[0];
                digits.add((byte) dr[1].intValue());
            }
            for (int i = digits.size() - 1; i >= 0; --i) {
                tmp.add(digits.get(i));
            }
            return tmp;
        } else {
            return result;
        }
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        return toBigInteger().toString();
    }

    private BigInteger toBigInteger() {
        if (size == 0) {
            return BigInteger.ZERO;
        }
        BigInteger res = BigInteger.ZERO;
        BigInteger bBase = BigInteger.valueOf(base);
        Node cur = head;
        for (int i = 0; i < size; ++i) {
            res = res.multiply(bBase).add(BigInteger.valueOf(cur.value));
            cur = cur.next;
        }
        return res;
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node cur = head;
        for (int i = 0; i < size; ++i) {
            sb.append(cur.value);
            if (i + 1 < size) sb.append(", ");
            cur = cur.next;
        }
        sb.append("] base=").append(base);
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberList)) return false;
        NumberList other = (NumberList) o;
        if (other.size() != this.size) return false;
        for (int i = 0; i < this.size; ++i) {
            Byte a = this.get(i);
            Byte b = other.get(i);
            if (a == null || b == null) {
                if (a != b) return false;
            } else if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) return false;
        byte v = (Byte) o;
        Node cur = head;
        for (int i = 0; i < size; ++i) {
            if (cur.value == v) return true;
            cur = cur.next;
        }
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private int idx = 0;
            private Node cur = head;

            @Override
            public boolean hasNext() {
                return idx < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                byte v = cur.value;
                cur = cur.next;
                idx++;
                return v;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node cur = head;
        for (int i = 0; i < size; ++i) {
            arr[i] = Byte.valueOf(cur.value);
            cur = cur.next;
        }
        return arr;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            T[] arr = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
            for (int i = 0; i < size; ++i) {
                arr[i] = (T) Byte.valueOf(get(i));
            }
            return arr;
        } else {
            for (int i = 0; i < size; ++i) {
                a[i] = (T) Byte.valueOf(get(i));
            }
            if (a.length > size) a[size] = null;
            return a;
        }
    }


    @Override
    public boolean add(Byte e) {
        if (e == null) throw new NullPointerException();
        if (e < 0 || e >= base) throw new IllegalArgumentException("Digit out of range for base " + base);
        Node n = new Node(e);
        if (head == null) {
            head = n;
            size = 1;
            return true;
        } else {
            Node tail = head;
            for (int i = 1; i < size; ++i) tail = tail.next;
            tail.next = n;
            n.next = head;
            size++;
            return true;
        }
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) return false;
        if (head == null) return false;
        byte v = (Byte) o;
        Node cur = head;
        Node prev = null;
        for (int i = 0; i < size; ++i) {
            if (cur.value == v) {
                if (size == 1) {
                    head = null;
                    size = 0;
                    return true;
                } else {
                    if (cur == head) {
                        Node tail = head;
                        for (int j = 1; j < size; ++j) tail = tail.next;
                        head = head.next;
                        tail.next = head;
                    } else {
                        prev.next = cur.next;
                    }
                    size--;
                    return true;
                }
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        if (c == null) throw new NullPointerException();
        boolean changed = false;
        for (Byte b : c) {
            if (add(b)) changed = true;
        }
        return changed;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (c == null) throw new NullPointerException();
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        boolean changed = false;
        int pos = index;
        for (Byte b : c) {
            add(pos, b);
            pos++;
            changed = true;
        }
        return changed;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean changed = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            Byte v = it.next();
            if (c.contains(v)) {
                remove(v);
                changed = true;
            }
        }
        return changed;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean changed = false;
        int i = 0;
        while (i < size) {
            Byte v = get(i);
            if (!c.contains(v)) {
                remove(i);
                changed = true;
            } else {
                i++;
            }
        }
        return changed;
    }


    @Override
    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        checkIndex(index);
        Node cur = head;
        for (int i = 0; i < index; ++i) cur = cur.next;
        return Byte.valueOf(cur.value);
    }


    @Override
    public Byte set(int index, Byte element) {
        if (element == null) throw new NullPointerException();
        if (element < 0 || element >= base) throw new IllegalArgumentException("Digit out of range for base " + base);
        checkIndex(index);
        Node cur = head;
        for (int i = 0; i < index; ++i) cur = cur.next;
        byte old = cur.value;
        cur.value = element;
        return Byte.valueOf(old);
    }


    @Override
    public void add(int index, Byte element) {
        if (element == null) throw new NullPointerException();
        if (element < 0 || element >= base) throw new IllegalArgumentException("Digit out of range for base " + base);
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        Node n = new Node(element);
        if (size == 0) {
            head = n;
            size = 1;
            return;
        }
        if (index == 0) {
            Node tail = head;
            for (int i = 1; i < size; ++i) tail = tail.next;
            n.next = head;
            head = n;
            tail.next = head;
            size++;
            return;
        } else {
            Node prev = head;
            for (int i = 1; i < index; ++i) prev = prev.next;
            n.next = prev.next;
            prev.next = n;
            size++;
            return;
        }
    }


    @Override
    public Byte remove(int index) {
        checkIndex(index);
        if (size == 1 && index == 0) {
            byte v = head.value;
            head = null;
            size = 0;
            return Byte.valueOf(v);
        }
        if (index == 0) {
            Node tail = head;
            for (int i = 1; i < size; ++i) tail = tail.next;
            byte v = head.value;
            head = head.next;
            tail.next = head;
            size--;
            return Byte.valueOf(v);
        } else {
            Node prev = head;
            for (int i = 1; i < index; ++i) prev = prev.next;
            Node cur = prev.next;
            prev.next = cur.next;
            size--;
            return Byte.valueOf(cur.value);
        }
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) return -1;
        byte v = (Byte) o;
        Node cur = head;
        for (int i = 0; i < size; ++i) {
            if (cur.value == v) return i;
            cur = cur.next;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) return -1;
        byte v = (Byte) o;
        Node cur = head;
        int res = -1;
        for (int i = 0; i < size; ++i) {
            if (cur.value == v) res = i;
            cur = cur.next;
        }
        return res;
    }


    @Override
public ListIterator<Byte> listIterator() {
    return new NumberListIterator(0);
}

@Override
public ListIterator<Byte> listIterator(int index) {
    if (index < 0 || index > size)
        throw new IndexOutOfBoundsException();
    return new NumberListIterator(index);
}



    @Override
public List<Byte> subList(int fromIndex, int toIndex) {
    if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
        throw new IndexOutOfBoundsException();

    NumberListImpl sub = new NumberListImpl();
    sub.base = this.base;

    for (int i = fromIndex; i < toIndex; i++) {
        sub.add(this.get(i));
    }

    return sub;
}



    @Override
    public boolean swap(int index1, int index2) {
        checkIndex(index1);
        checkIndex(index2);
        if (index1 == index2) return true;
        Node n1 = head, n2 = head;
        for (int i = 0; i < index1; ++i) n1 = n1.next;
        for (int i = 0; i < index2; ++i) n2 = n2.next;
        byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }


    @Override
    public void sortAscending() {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node cur = head;
            for (int i = 0; i < size - 1; ++i) {
                if (cur.value > cur.next.value) {
                    byte t = cur.value;
                    cur.value = cur.next.value;
                    cur.next.value = t;
                    swapped = true;
                }
                cur = cur.next;
            }
        } while (swapped);
    }


    @Override
    public void sortDescending() {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node cur = head;
            for (int i = 0; i < size - 1; ++i) {
                if (cur.value < cur.next.value) {
                    byte t = cur.value;
                    cur.value = cur.next.value;
                    cur.next.value = t;
                    swapped = true;
                }
                cur = cur.next;
            }
        } while (swapped);
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        head = head.next;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) return;
        Node tail = head;
        for (int i = 1; i < size; ++i) tail = tail.next;
        head = tail;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
    }

    private class NumberListIterator implements ListIterator<Byte> {

    private int cursor; 
    private int lastReturned = -1;

    NumberListIterator(int index) {
        this.cursor = index;
    }

    @Override
    public boolean hasNext() {
        return cursor < size;
    }

    @Override
    public Byte next() {
        if (!hasNext())
            throw new java.util.NoSuchElementException();
        lastReturned = cursor;
        return get(cursor++);
    }

    @Override
    public boolean hasPrevious() {
        return cursor > 0;
    }

    @Override
    public Byte previous() {
        if (!hasPrevious())
            throw new java.util.NoSuchElementException();
        cursor--;
        lastReturned = cursor;
        return get(cursor);
    }

    @Override
    public int nextIndex() {
        return cursor;
    }

    @Override
    public int previousIndex() {
        return cursor - 1;
    }

    @Override
    public void remove() {
        if (lastReturned < 0)
            throw new IllegalStateException();
        NumberListImpl.this.remove(lastReturned);
        if (lastReturned < cursor) cursor--;
        lastReturned = -1;
    }

    @Override
    public void set(Byte e) {
        if (lastReturned < 0)
            throw new IllegalStateException();
        NumberListImpl.this.set(lastReturned, e);
    }

    @Override
    public void add(Byte e) {
        NumberListImpl.this.add(cursor, e);
        cursor++;
        lastReturned = -1;
    }
}

}

