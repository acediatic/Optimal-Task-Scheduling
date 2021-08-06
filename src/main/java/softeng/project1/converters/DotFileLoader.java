package softeng.project1.converters;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.algorithm.TopologicalSortDFS;

public class DotFileLoader {
    private Graph graph = new DefaultGraph("graph");
    private List<Node> topologicalOrdering;

    /**
     * DotFileLoader Constructor
     * @param path String containing path to the DOT file for given graph
     */
    public DotFileLoader(String path) {
        FileSourceDOT fileSource = new FileSourceDOT();
        fileSource.addSink(graph);

        System.setProperty("org.graphstream.ui", "swing");
        //Reads DOT file into graph object
        try {
            fileSource.readAll(path);
        } catch( IOException e) {
            System.out.println(e.getMessage());
        } finally {
            fileSource.removeSink(graph);
        }


        //Generates topological ordering for nodes in the graph.
        TopologicalSortDFS sort = new TopologicalSortDFS();
        sort.init(graph);
        sort.compute();
        topologicalOrdering = sort.getSortedNodes();
    }

    /**
     * Displays Graph in a Java Swing window
     */
    public void displayGraph(){
        graph.display();
    }

    /**
     * Graph Getter
     * @return Graph Object
     */
    public Graph getGraph(){
        return graph;
    }
}
