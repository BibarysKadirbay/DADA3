import java.util.*;

public class MergeSort {
    public static <T> void sort(List<T> arr, Comparator<T> cmp) {
        @SuppressWarnings("unchecked")
        T[] a = (T[]) arr.toArray();
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Object[a.length];
        sort(a, aux, 0, a.length, cmp);
        for (int i = 0; i < a.length; i++) arr.set(i, a[i]);
    }

    private static <T> void sort(T[] a, T[] aux, int lo, int hi, Comparator<T> cmp) {
        if (hi - lo <= 1) return;
        int mid = (lo + hi) >>> 1;
        sort(a, aux, lo, mid, cmp);
        sort(a, aux, mid, hi, cmp);
        merge(a, aux, lo, mid, hi, cmp);
    }

    private static <T> void merge(T[] a, T[] aux, int lo, int mid, int hi, Comparator<T> cmp) {
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) {
            if (cmp.compare(a[i], a[j]) <= 0) aux[k++] = a[i++];
            else aux[k++] = a[j++];
        }
        while (i < mid) aux[k++] = a[i++];
        while (j < hi) aux[k++] = a[j++];
        for (k = lo; k < hi; k++) a[k] = aux[k];
    }
}