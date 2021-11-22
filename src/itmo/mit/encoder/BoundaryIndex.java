package itmo.mit.encoder;

import java.util.ArrayList;
import java.util.List;

public class BoundaryIndex {
    private final int begin;
    private final int end;

    public BoundaryIndex(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    private static void mergeStringList(List<BoundaryIndex> list, String string, int begin, int middle, int end) {
        final int len1 = middle - begin;
        final int len2 = end - middle;

        List<BoundaryIndex> left = new ArrayList<>();
        List<BoundaryIndex> right = new ArrayList<>();

        for (int i = 0; i < len1; i++) {
            left.add(list.get(begin + i));
        }

        for (int i = 0; i < len2; i++) {
            right.add(list.get(middle + i));
        }

        int i = 0;
        int j = 0;
        int k = begin;

        while (i < len1 && j < len2) {
            if (string.substring(left.get(i).begin, left.get(i).end).compareTo(
                    string.substring(right.get(j).begin, right.get(j).end)) <= 0) {
                list.set(k, left.get(i));
                i++;
            } else {
                list.set(k, right.get(j));
                j++;
            }
            k++;
        }

        while (i < len1) {
            list.set(k, left.get(i));
            k++;
            i++;
        }

        while (j < len2) {
            list.set(k, right.get(j));
            k++;
            j++;
        }
    }

    public static void mergeStringSort(List<BoundaryIndex> list, String string) {
        for (int i = 1; i < list.size(); i *= 2) {
            for (int j = 0; j < list.size() - i; j += 2 * i) {
                mergeStringList(list, string, j, j + i, Math.min(j + (2 * i), list.size()));
            }
        }
    }
}
