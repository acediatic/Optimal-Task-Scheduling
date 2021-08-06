package softeng.project1.converters;

import softeng.project1.graph.ImmutableTaskGraph;
import org.graphstream.graph.*;
import softeng.project1.graph.ImmutableTaskNode;

import java.util.HashMap;
import java.util.Map;

public class GraphHandler {

    /**
     * Converts the graph read in from DotFileLoader to a suitable representation
     * using our graph classes to help create schedule
     */
    public static ImmutableTaskGraph convertFromGraph(Graph graph) {

        Map<String, ImmutableTaskNode> graphNodes = new HashMap<String, ImmutableTaskNode>();

        //Iterates through nodes and converts them into ImmutableTaskNodes
        for(int i = 0; i < graph.getNodeCount(); i++){
          Node graphNode = graph.getNode(i);
          int nodeCost = (int) Double.parseDouble(graphNode.getAttribute("Weight").toString());

          ImmutableTaskNode node = new ImmutableTaskNode(i, nodeCost);
          graphNodes.put(graphNode.getId(), node);
        }

        //Returns original immutable task graph from DOT File. Will have free nodes = general nodes
        return new ImmutableTaskGraph(graphNodes, graphNodes);
    }


    /**
     * Generates output file in DOT format for a given graph
     */
    public static void generateDOT() {
    }

}
