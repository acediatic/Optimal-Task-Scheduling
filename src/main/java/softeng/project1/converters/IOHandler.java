package softeng.project1.converters;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import java.io.File;
import java.io.IOException;

import org.graphstream.stream.file.FileSourceDOT;

import org.graphstream.stream.file.FileSinkDOT;



public class IOHandler {
    /**
     * Reads in DOT file from specified path and stores as Graph Object
     */
    public static Graph readFile(String inputFilePath){
        Graph graph = new DefaultGraph("graph");

        FileSourceDOT fileSource = new FileSourceDOT();
        fileSource.addSink(graph);

        System.setProperty("org.graphstream.ui", "swing");
        //Reads DOT file into graph object
        try {
            fileSource.readAll(inputFilePath);
        } catch( IOException e) {
            System.out.println(e.getMessage());
        } finally {
            fileSource.removeSink(graph);
        }

        return graph;
    }

    /**
     * Writes graphObject out as DOT File
     */
    public static void writeFile(Graph graph, String outputFileName){
        FileSinkDOT fileSink = new FileSinkDOT(true);

        try {
            fileSink.writeAll(graph, outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
