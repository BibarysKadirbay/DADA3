import java.util.*;

public class Prim {
    public static class Result {
        public final List<Edge> mstEdges;
        public final int totalCost;
        public final long operationsCount;
        Result(List<Edge> mstEdges, int totalCost, long ops) {
            this.mstEdges = mstEdges; this.totalCost = totalCost; this.operationsCount = ops;
        }
    }

    private static class PQItem {
        int weight;
        String u, v;  // u -> v
        PQItem(int w, String u, String v) { this.weight = w; this.u = u; this.v = v; }
    }

    public static Result run(Graph g) {
        Map<String, List<PQItem>> adj = new HashMap<>();
        for (String s : g.nodes) adj.put(s, new ArrayList<>());
        for (Edge e : g.edges) {
            adj.get(e.from).add(new PQItem(e.weight, e.from, e.to));
            adj.get(e.to).add(new PQItem(e.weight, e.to, e.from));
        }

        String start = g.nodes.get(0);
        Set<String> vis = new HashSet<>();
        vis.add(start);

        // custom min-heap with op counting
        MinHeap<PQItem> heap = new MinHeap<>((a,b) -> {
            if (a.weight != b.weight) return Integer.compare(a.weight, b.weight);
            int c = a.u.compareTo(b.u);
            if (c != 0) return c;
            return a.v.compareTo(b.v);
        });

        long ops = 0;
        for (PQItem it : adj.get(start)) { heap.push(it); ops++; }

        List<Edge> mst = new ArrayList<>();
        int total = 0;

        while (!heap.isEmpty() && mst.size() < g.nodes.size()-1) {
            PQItem it = heap.pop(); ops++;
            if (vis.contains(it.v)) continue;
            vis.add(it.v);
            mst.add(new Edge(it.u, it.v, it.weight));
            total += it.weight;
            for (PQItem nxt : adj.get(it.v)) { heap.push(nxt); ops++; }
        }
        return new Result(mst, total, ops + heap.operations());
    }

    public static List<Object> edgesToJson(List<Edge> edges) {
        List<Object> arr = new ArrayList<>();
        for (Edge e : edges) {
            LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
            obj.put("from", e.from);
            obj.put("to", e.to);
            obj.put("weight", e.weight);
            arr.add(obj);
        }
        return arr;
    }
}