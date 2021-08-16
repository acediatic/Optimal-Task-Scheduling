package softeng.project1.io;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.IOException;
import java.io.InputStream;
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
        return null; // TODO...
    }

    static int getProcessingCost(Node task) {
        return (int) task.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY);
    }

    static int getNumParents(Node task) {
        return task.getInDegree();
    }

    static List<Edge> getChildLinks (Node task) {
        return task.leavingEdges().collect(Collectors.toList());
    }

    static int calculateBottomLevel(Node task) {
        return 0 // TODO... Hopefully Adam can do this..
    }

}
