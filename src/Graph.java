import java.util.*;

public class Graph {
    public final List<String> nodes;       // index -> label
    public final Map<String, Integer> id;  // label -> index
    public final List<Edge> edges;         // undirected edges

    public Graph(List<String> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.id = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) id.put(nodes.get(i), i);
        this.edges = edges;
    }

    public int verticesCount() { return nodes.size(); }
    public int edgesCount() { return edges.size(); }

    @SuppressWarnings("unchecked")
    public static Graph fromJson(List<Object> nodesJson, List<Object> edgesJson) {
        List<String> nodes = new ArrayList<>();
        for (Object o : nodesJson) nodes.add((String) o);

        List<Edge> edges = new ArrayList<>();
        for (Object eo : edgesJson) {
            Map<String, Object> em = (Map<String, Object>) eo;
            String from = (String) em.get("from");
            String to = (String) em.get("to");
            int w = ((Number) em.get("weight")).intValue();
            edges.add(new Edge(from, to, w));
        }
        return new Graph(nodes, edges);
    }
}