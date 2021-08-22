package softeng.project1.io;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.*;


public class ListIOHandler {
    /**
     * Reads in DOT file from specified path and stores as Graph Object
     */
    public Graph readFile(String inputFilePath) {
        Graph graph = new DefaultGraph("graph");

        FileSourceDOT fileSource = new FileSourceDOT();
        fileSource.addSink(graph);

        //Reads DOT file into graph object
        try {
            fileSource.readAll(inputFilePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            fileSource.removeSink(graph);
        }

        return graph;
    }

    /**
     * Writes graphObject out as DOT File
     */

    public void writeFile(Graph graph, String graphName, String outputFileName) {
        try {
            // tempOutput contains the graph output before renaming.
            String tempOutputName = outputFileName.substring(0, outputFileName.length() - 4) + "tempOutput.dot";

            // writes the graph to the temp file.
            FileSinkDOT fileSink = new FileSinkDOT(true);
            fileSink.writeAll(graph, tempOutputName);

            // setup new file with correct name, and reader on temp file with graph info.
            File oldOutput = new File(tempOutputName);
            File newOutput = new File(outputFileName);
            BufferedReader reader = new BufferedReader(new FileReader(oldOutput));
            BufferedWriter output = new BufferedWriter(new FileWriter(newOutput));

            // burn first line, to replace.
            reader.readLine();

            // Add new first line (name is surrounded in quotation marks)
            output.write("digraph \"" + graphName + "-output\" {\n");

            // replace rest of file
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append('\n');
            }
            output.close();
            reader.close();

            // delete original (temp) file
            if (!oldOutput.delete()) {
                throw new IOException();
            }

        } catch (IOException e) {
            System.err.println("Error: unable to write output file");
            System.exit(1);
        }
    }
}
