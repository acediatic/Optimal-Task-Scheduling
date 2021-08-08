package softeng.project1.converters;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import java.io.IOException;

import org.graphstream.stream.file.FileSourceDOT;



public class IOHandler {
    private final String GRAPH_FILE_PATH;
    private Graph graph = new DefaultGraph("graph");


    public IOHandler(String filePath) {
        GRAPH_FILE_PATH = filePath;
    }

    /**
     * Reads in DOT file from specified path and stores as Graph Object
     */
    public Graph readFile(){
        FileSourceDOT fileSource = new FileSourceDOT();
        fileSource.addSink(graph);

        System.setProperty("org.graphstream.ui", "swing");
        //Reads DOT file into graph object
        try {
            fileSource.readAll(GRAPH_FILE_PATH);
        } catch( IOException e) {
            System.out.println(e.getMessage());
        } finally {
            fileSource.removeSink(graph);
        }

        return getGraph();
    }

    /**
     * Writes graphObject out as DOT File
     */
    public void writeFile(){

    }

    public Graph getGraph(){
        return this.graph;
    }


}
