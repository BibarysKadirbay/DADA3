import java.util.*;

public class DisjointSet {
    private final int[] parent;
    private final int[] rank;
    private final Map<String, Integer> index;

    public DisjointSet(List<String> nodes) {
        int n = nodes.size();
        parent = new int[n];
        rank = new int[n];
        index = new HashMap<>();
        for (int i = 0; i < n; i++) {
            parent[i] = i; rank[i] = 0; index.put(nodes.get(i), i);
        }
    }

    public int find(String label) { return find(index.get(label)); }
    public int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    public void union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return;
        if (rank[ra] < rank[rb]) parent[ra] = rb;
        else if (rank[ra] > rank[rb]) parent[rb] = ra;
        else { parent[rb] = ra; rank[ra]++; }
    }
}