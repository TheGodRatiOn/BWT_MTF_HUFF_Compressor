package itmo.mit.encoder;

import java.util.List;

public class BoundaryIndex {
    private final int begin;
    private final int end;

    public BoundaryIndex(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public boolean isGreaterThan(BoundaryIndex index, String string) {
        return string.substring(this.begin, this.end).compareTo(
                string.substring(index.begin, index.end)) <= 0;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public static void quickStringSort(List<BoundaryIndex> list, String string, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(list, string, begin, end);

            quickStringSort(list, string, begin, partitionIndex - 1);
            quickStringSort(list, string, partitionIndex + 1, end);
        }
    }

    private static int partition(List<BoundaryIndex> list, String string, int begin, int end) {
        BoundaryIndex pivot = list.get(end);
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (list.get(j).isGreaterThan(pivot, string)) {
                i++;

                BoundaryIndex swapTemp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, swapTemp);
            }
        }

        BoundaryIndex swapTemp = list.get(i + 1);
        list.set(i + 1, list.get(end));
        list.set(end, swapTemp);

        return i + 1;
    }
}
