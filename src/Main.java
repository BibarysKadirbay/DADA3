import java.io.*;
import java.util.*;

public class Main {
    private static final String DEFAULT_INPUT = "src/input_example.json";
    private static final String DEFAULT_OUTPUT = "output.json";

    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].equalsIgnoreCase("generate")) {
                // Generate 5 graphs into input_example.json then solve to output.json
                List<LinkedHashMap<String, Object>> graphs = RandomGraphGenerator.makeFiveGraphs();
                LinkedHashMap<String, Object> inputRoot = new LinkedHashMap<>();
                inputRoot.put("graphs", graphs);
                IOUtils.writeAllText(DEFAULT_INPUT, SimpleJson.stringify(inputRoot, true));
                System.out.println("Generated: " + DEFAULT_INPUT);
                process(DEFAULT_INPUT, DEFAULT_OUTPUT);
                System.out.println("Wrote: " + DEFAULT_OUTPUT);
                return;
            }

            String in = args.length >= 1 ? args[0] : DEFAULT_INPUT;
            String out = args.length >= 2 ? args[1] : DEFAULT_OUTPUT;
            process(in, out);
            System.out.println("Wrote: " + out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void process(String inputPath, String outputPath) throws IOException {
        String jsonText = IOUtils.readAllText(inputPath);
        Object parsed = SimpleJson.parse(jsonText);
        if (!(parsed instanceof Map)) {
            throw new IllegalArgumentException("Root JSON must be an object with 'graphs'");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> root = (Map<String, Object>) parsed;
        @SuppressWarnings("unchecked")
        List<Object> graphsArr = (List<Object>) root.get("graphs");
        if (graphsArr == null) throw new IllegalArgumentException("Missing 'graphs'");

        // Prepare output root with ordering identical to the example
        LinkedHashMap<String, Object> outRoot = new LinkedHashMap<>();
        List<Object> results = new ArrayList<>();
        outRoot.put("results", results);

        for (Object gobj : graphsArr) {
            @SuppressWarnings("unchecked")
            Map<String, Object> gmap = (Map<String, Object>) gobj;

            int graphId = ((Number) gmap.get("id")).intValue();
            @SuppressWarnings("unchecked")
            List<Object> nodesJson = (List<Object>) gmap.get("nodes");
            @SuppressWarnings("unchecked")
            List<Object> edgesJson = (List<Object>) gmap.get("edges");

            // Build Graph
            Graph graph = Graph.fromJson(nodesJson, edgesJson);

            // Run Prim
            long t0 = System.nanoTime();
            Prim.Result primRes = Prim.run(graph);
            long t1 = System.nanoTime();

            // Run Kruskal
            long t2 = System.nanoTime();
            Kruskal.Result kruskalRes = Kruskal.run(graph);
            long t3 = System.nanoTime();

            // times in ms
            double primMs = (t1 - t0) / 1_000_000.0;
            double kruskalMs = (t3 - t2) / 1_000_000.0;

            // Build ordered output object for this graph
            LinkedHashMap<String, Object> outGraph = new LinkedHashMap<>();
            outGraph.put("graph_id", graphId);

            LinkedHashMap<String, Object> inputStats = new LinkedHashMap<>();
            inputStats.put("vertices", graph.verticesCount());
            inputStats.put("edges", graph.edgesCount());
            outGraph.put("input_stats", inputStats);

            LinkedHashMap<String, Object> primObj = new LinkedHashMap<>();
            primObj.put("mst_edges", Prim.edgesToJson(primRes.mstEdges));
            primObj.put("total_cost", primRes.totalCost);
            primObj.put("operations_count", primRes.operationsCount);
            primObj.put("execution_time_ms", round3(primMs));
            outGraph.put("prim", primObj);

            LinkedHashMap<String, Object> krObj = new LinkedHashMap<>();
            krObj.put("mst_edges", Kruskal.edgesToJson(kruskalRes.mstEdges));
            krObj.put("total_cost", kruskalRes.totalCost);
            krObj.put("operations_count", kruskalRes.operationsCount);
            krObj.put("execution_time_ms", round3(kruskalMs));
            outGraph.put("kruskal", krObj);

            results.add(outGraph);
        }

        // write output with pretty formatting
        IOUtils.writeAllText(outputPath, SimpleJson.stringify(outRoot, true));
    }

    private static double round3(double x) {
        return Math.round(x * 1000.0) / 1000.0;
    }
}