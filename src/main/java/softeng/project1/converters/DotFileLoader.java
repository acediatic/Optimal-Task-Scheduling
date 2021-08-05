package softeng.project1.converters;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.IOException;

/**
 * DotFileLoader uses GraphStream to read the DOT file and adds it to it's graph representation.
 */
public class DotFileLoader {
    private Graph graph = new DefaultGraph("graph");
    private FileSource fileSource;
    //FileSource fs = FileSourceFactory.sourceFor("path/to/my/file");

    public DotFileLoader() {
        //
    }
    public DotFileLoader(String path) {
        try {
            fileSource = FileSourceFactory.sourceFor(path);
            fileSource.addSink(graph);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException thrown for file source path");
        }
    }

    public void drawGraph() {
        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
        graph.display();
    }

}
