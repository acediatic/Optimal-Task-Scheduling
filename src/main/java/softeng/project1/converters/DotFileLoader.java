package softeng.project1.converters;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.io.IOException;
import org.graphstream.stream.file.FileSourceDOT;

public class DotFileLoader {
    private Graph graph = new DefaultGraph("graph");

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
