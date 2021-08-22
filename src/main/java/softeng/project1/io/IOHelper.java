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
import java.util.List;

/**
 * A helper class with functions for I/O
 */
public class IOHelper {

    private static final String DEFAULT_GRAPH_ID = "graph";
    private static final String PROCESSING_COST_ATTRIBUTE_KEY = "Weight";
    private static final String SCHEDULE_START_LOCATION_KEY = "Start";
    private static final String SCHEDULE_PROCESSOR_KEY = "Processor";

    /**
     * Reads a file stream and converts it into a GraphStream graph object.
     *
     * @param fileStream the dot file stream to read
     * @return the graph object
     */
    static Graph readFileAsGraphStream(InputStream fileStream) {
        FileSourceDOT dotFile = new FileSourceDOT();

        Graph returnGraph = new DefaultGraph(DEFAULT_GRAPH_ID);
        dotFile.addSink(returnGraph);

        try {
            dotFile.readAll(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dotFile.removeSink(returnGraph);
        }

        return returnGraph;
    }


    /**
     * get the task weight (processing cost) of the input element
     *
     * @param graphElement the graph element
     * @return the graph element's processing weight
     */
    static int getProcessingCost(Element graphElement) {
        return ((Number) graphElement.getAttribute(PROCESSING_COST_ATTRIBUTE_KEY)).intValue();
    }

    /**
     * get the number of parents of the input element
     *
     * @param task the task
     * @return the number of parents
     */
    static int getNumParents(Node task) {
        return task.getInDegree();
    }

    /**
     * get the edges representing the incoming links to this task
     *
     * @param task the task
     * @return the edges coming into this task
     */
    static Edge[] getChildLinks(Node task) {
        return task.leavingEdges().toArray(Edge[]::new);
    }

    /**
     * get the edges representing the outgoing links for this task
     *
     * @param task the task
     * @return the edges going out of this task
     */
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

    /**
     * Adds scheduling information for the current task, so it can be
     * output to file
     *
     * @param task           the task
     * @param schedulingData the corresponding data to add.
     */
    static void addSchedulingToTask(Node task, int[] schedulingData) {
        task.setAttribute(SCHEDULE_PROCESSOR_KEY, schedulingData[1]);
        task.setAttribute(SCHEDULE_START_LOCATION_KEY, schedulingData[2]);
    }

    /**
     * Returns the branching factor for this particular graph.
     * Used to intelligently initialise the size of some datastructures.
     *
     * @param graph the input graph
     * @return the branching factor
     */
    static short getBranchingFactor(Graph graph) {
        short branchingFactor = 0;

        for (int i = 0; i < graph.getNodeCount(); i++) {
            if (graph.getNode(i).getOutDegree() > branchingFactor) {
                branchingFactor = (short) graph.getNode(i).getOutDegree();
            }
        }

        return branchingFactor;
    }

    /**
     * Retrieves the topological ordering of nodes in this graph
     *
     * @param graph the graph
     * @return the list of nodes in topologically sorted order.
     */
    static List<Node> getSortedNodes(Graph graph) {
        //Sorts nodes into a topological ordering
        TopologicalSortDFS sorter = new TopologicalSortDFS();
        sorter.init(graph);
        sorter.compute();
        return sorter.getSortedNodes();
    }

}
