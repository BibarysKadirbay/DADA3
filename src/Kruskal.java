import java.util.*;

public class Kruskal {
    public static class Result {
        public final List<Edge> mstEdges;
        public final int totalCost;
        public final long operationsCount;
        Result(List<Edge> mstEdges, int totalCost, long ops) {
            this.mstEdges = mstEdges; this.totalCost = totalCost; this.operationsCount = ops;
        }
    }

    public static Result run(Graph g) {
        // Sort edges by weight (stable tie-breakers) with comparison counting
        List<Edge> sorted = new ArrayList<>(g.edges);
        long[] comps = new long[1];
        MergeSort.sort(sorted, (e1, e2) -> {
            comps[0]++;
            if (e1.weight != e2.weight) return Integer.compare(e1.weight, e2.weight);
            int c = e1.from.compareTo(e2.from);
            if (c != 0) return c;
            return e1.to.compareTo(e2.to);
        });

        DisjointSet dsu = new DisjointSet(g.nodes);
        long ops = comps[0];
        List<Edge> mst = new ArrayList<>();
        int total = 0;
        for (Edge e : sorted) {
            int a = dsu.find(e.from);
            int b = dsu.find(e.to);
            ops += 2; // finds
            if (a != b) {
                dsu.union(a, b);
                ops++; // union
                mst.add(e);
                total += e.weight;
                if (mst.size() == g.nodes.size()-1) break;
            }
        }
        return new Result(mst, total, ops);
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