package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IOHelper {

    private static final String DEFAULT_GRAPH_ID = "graph";
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";

    static Graph readFileAsGraphStream(InputStream fileStream) {
        FileSourceDOT dotFile = new FileSourceDOT();

        Graph returnGraph = new DefaultGraph(DEFAULT_GRAPH_ID);
        dotFile.addSink(returnGraph);

        try {
            dotFile.readAll(fileStream);
        } catch (IOException e) {
            e.printStackTrace(); // Input should be sanitised outside of this helper method
        } finally {
            dotFile.removeSink(returnGraph);
        }

        return returnGraph;
    }

    static Map<Integer, String> mapTaskNamesToIDs(Graph graph) {

        Map<Integer, String> taskNameIDMap = new HashMap<>();

        for (int i = 0; i < graph.getNodeCount(); i++) {
            taskNameIDMap.put(
                    i,
                    graph.getNode(i).getId()
            );
        }

        return taskNameIDMap;
    }

    static int getProcessingCost(Element graphElement) {
        return (int) graphElement.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY);
    }

    static int getNumParents(Node task) {
        return task.getInDegree();
    }

    static Edge[] getChildLinks (Node task) {
        return task.leavingEdges().toArray(Edge[]::new);
    }

}
