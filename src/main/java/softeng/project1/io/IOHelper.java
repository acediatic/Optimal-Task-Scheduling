package softeng.project1.io;

import org.graphstream.algorithm.TopologicalSortDFS;
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

public class IOHelper {

    private static final String DEFAULT_GRAPH_ID = "graph";
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private static final String SCHEDULE_START_LOCATION_KEY = "Start";
    private static final String SCHEDULE_PROCESSOR_KEY = "Processor";

    static Graph readFileAsGraphStream(InputStream fileStream) {
        FileSourceDOT dotFile = new FileSourceDOT();

        Graph returnGraph = new DefaultGraph(DEFAULT_GRAPH_ID);
        dotFile.addSink(returnGraph);

        try {
            dotFile.readAll(fileStream);
        } catch (IOException e) {
            e.printStackTrace(); // Input should be sanitised outside this helper method
        } finally {
            dotFile.removeSink(returnGraph);
        }

        return returnGraph;
    }

    static Map<Short, String> mapTaskNamesToIDs(Graph graph) {

        Map<Short, String> taskNameIDMap = new HashMap<>();

        for (int i = 0; i < graph.getNodeCount(); i++) {
            taskNameIDMap.put(
                    (short) i,
                    graph.getNode(i).getId()
            );
        }

        return taskNameIDMap;
    }

    static int getProcessingCost(Element graphElement) {
        return ((Number) graphElement.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY)).intValue();
    }

    static int getNumParents(Node task) {
        return task.getInDegree();
    }

    static Edge[] getChildLinks(Node task) {
        return task.leavingEdges().toArray(Edge[]::new);
    }

    static Edge[] getParentLinks(Node task) {
        return task.enteringEdges().toArray(Edge[]::new);
    }

    /*
     * A recursive, dynamic solution to calculating the bottom levels of each of the nodes.
     */
    static int dynamicBottomLevelCalculation(Node taskNode) {
        int numChildren = taskNode.getOutDegree();
        int bottomLevel = getProcessingCost(taskNode);

        // If the node has children, it is not the base case, so recursively determine bottom level for children.
        if (numChildren > 0) {
            int currentMax = 0;
            for (int i = 0; i < numChildren; i++) {
                Node childTaskNode = taskNode.getLeavingEdge(i).getTargetNode();
                currentMax = Math.max(currentMax, dynamicBottomLevelCalculation(childTaskNode));
            }
            bottomLevel += currentMax;
        }

        // either way, this task's cost forms part of its bottom level.
        return bottomLevel;
    }

    static void addSchedulingToTask(Node task, int[] schedulingData) {
        task.setAttribute(SCHEDULE_PROCESSOR_KEY, schedulingData[1]);
        task.setAttribute(SCHEDULE_START_LOCATION_KEY, schedulingData[2]);
    }

    static short getBranchingFactor(Graph graph) {
        short branchingFactor = 0;

        for (int i = 0; i < graph.getNodeCount(); i++) {
            if (graph.getNode(i).getOutDegree() > branchingFactor) {
                branchingFactor = (short) graph.getNode(i).getOutDegree();
            }
        }

        return branchingFactor;
    }

    static List<Node> getSortedNodes(Graph graph) {
        //Sorts nodes into a topological ordering
        TopologicalSortDFS sorter = new TopologicalSortDFS();
        sorter.init(graph);
        sorter.compute();
        List<Node> sortedNodes = sorter.getSortedNodes();

        return sortedNodes;
    }
}
