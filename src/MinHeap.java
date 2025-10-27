import java.util.*;
/** Minimal binary heap with operation counting (comparisons inside comparator). */
public class MinHeap<T> {
    private final ArrayList<T> a = new ArrayList<>();
    private final Comparator<T> cmp;
    private long ops = 0;

    public MinHeap(Comparator<T> cmp) { this.cmp = cmp; }

    public boolean isEmpty() { return a.isEmpty(); }

    public void push(T x) {
        a.add(x);
        siftUp(a.size()-1);
    }

    public T pop() {
        T res = a.get(0);
        T last = a.remove(a.size()-1);
        if (!a.isEmpty()) { a.set(0, last); siftDown(0); }
        return res;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int p = (i - 1) >>> 1;
            ops++;
            if (cmp.compare(a.get(i), a.get(p)) < 0) {
                swap(i, p); i = p;
            } else break;
        }
    }

    private void siftDown(int i) {
        int n = a.size();
        while (true) {
            int l = (i << 1) + 1, r = l + 1, best = i;
            if (l < n) { ops++; if (cmp.compare(a.get(l), a.get(best)) < 0) best = l; }
            if (r < n) { ops++; if (cmp.compare(a.get(r), a.get(best)) < 0) best = r; }
            if (best == i) break;
            swap(i, best); i = best;
        }
    }

    private void swap(int i, int j) {
        T t = a.get(i); a.set(i, a.get(j)); a.set(j, t);
    }

    public long operations() { return ops; }
}