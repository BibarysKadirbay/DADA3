public class Edge {
    public final String from;
    public final String to;
    public final int weight;

    public Edge(String from, String to, int weight) {
        // enforce canonical order in tie-breaking (for stable JSON output)
        if (from.compareTo(to) <= 0) {
            this.from = from;
            this.to = to;
        } else {
            this.from = to;
            this.to = from;
        }
        this.weight = weight;
    }
}