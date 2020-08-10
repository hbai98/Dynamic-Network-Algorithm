package Tools;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Functions {

    static <K, V extends Comparable<? super V>>
    SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                Map.Entry.comparingByValue()
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     * merge byte[]
     *
     * @return merged byte array
     */
    static public byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * sub byte[]
     *
     * @param b      original array
     * @param off    index, start point to sub from b
     * @param length length
     * @return subByte array
     */
    static public byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    /**
     * Arrays.stream(arr) or IntStream.of(arr)。
     * <ol>
     *  <li>Use Arrays.stream transform int[] into IntStream。</li>
     *  <li>Use IntStream boxed() to wrap it. Transform IntStream into Stream<Integer>.</li>
     *  <li>Use Stream's collect()，Transform Stream<T> to List<T>.</li>
     * </ol>
     * @return Integer list
     */
    static public List<Integer> IntArrayToList(int[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    /**
     * Use Stream's toArray()，pass IntFunction<A[]> generator
     * @return Integer Array
     */
    static public Integer[] IntArrayToIntegerArray(int[] array) {
        return  Arrays.stream(array).boxed().toArray(Integer[]::new);
    }

    /**
     * USe mapToInt():Stream<Integer> evokes Integer::valueOf -> IntStream
     * IntStream's toArray()->int[]。
     * @return int array
     */
    static public int[] ListToIntArray(List<Integer> list) {
        return list.stream().mapToInt(Integer::valueOf).toArray();
    }

    static public boolean isIdentifier(String str) {
        Pattern p = Pattern.compile("[a-zA-Z][[0-9]|[a-zA-Z]]*");
        Matcher m = p.matcher(str);
        return m.find();
    }

    static public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
