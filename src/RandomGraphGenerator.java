import java.util.*;

public class RandomGraphGenerator {
    public static List<LinkedHashMap<String, Object>> makeFiveGraphs() {
        int[] sizes = {5, 6, 7, 5, 6};
        double[] densities = {0.6, 0.5, 0.4, 0.7, 0.5};
        Random rnd = new Random(42);

        List<LinkedHashMap<String, Object>> graphs = new ArrayList<>();
        for (int gi = 0; gi < 5; gi++) {
            int n = sizes[gi];
            double p = densities[gi];

            List<String> nodes = new ArrayList<>();
            for (int i = 0; i < n; i++) nodes.add(String.valueOf((char)('A' + i)));

            // ensure connectivity with a shuffled chain
            List<String> chain = new ArrayList<>(nodes); Collections.shuffle(chain, rnd);
            List<Object> edges = new ArrayList<>();
            for (int i = 0; i < n - 1; i++) {
                String a = chain.get(i), b = chain.get(i+1);
                int w = 1 + rnd.nextInt(10);
                edges.add(edgeObj(a, b, w));
            }

            // add extra edges with probability p
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    String a = nodes.get(i), b = nodes.get(j);
                    if (hasEdge(edges, a, b)) continue;
                    if (rnd.nextDouble() < p) {
                        int w = 1 + rnd.nextInt(10);
                        edges.add(edgeObj(a, b, w));
                    }
                }
            }

            LinkedHashMap<String, Object> g = new LinkedHashMap<>();
            g.put("id", gi + 1);
            g.put("nodes", nodes);
            g.put("edges", edges);
            graphs.add(g);
        }
        return graphs;
    }

    private static boolean hasEdge(List<Object> edges, String a, String b) {
        for (Object eo : edges) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) eo;
            String f = (String) m.get("from");
            String t = (String) m.get("to");
            if ((f.equals(a) && t.equals(b)) || (f.equals(b) && t.equals(a))) return true;
        }
        return false;
    }

    private static LinkedHashMap<String, Object> edgeObj(String from, String to, int w) {
        // Keep order: from, to, weight
        LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
        obj.put("from", from);
        obj.put("to", to);
        obj.put("weight", w);
        return obj;
    }
}