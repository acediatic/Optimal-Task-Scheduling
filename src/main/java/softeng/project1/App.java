package softeng.project1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.Viewer;
import softeng.project1.algorithms.valid.ListSchedulingAlgorithm;
import softeng.project1.gui.GuiController;
import softeng.project1.io.CommandLineProcessor;
import softeng.project1.io.IOHandler;

import java.io.File;
import java.util.List;


public final class App extends Application {

    private GuiController guiController;
    /**
     * Runs the scheduling program
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "javafx");
        CommandLineProcessor clp = new CommandLineProcessor(args);

        String runInformation = "***** Outsourced to Pakistan - Scheduling Algorithm *****\n" +
                String.format("- Creating schedule for input graph from file: %s\n", clp.getInputFileName()) +
                String.format("- Number of Processors Available for Tasks: %o\n", clp.getNumProcessors()) +
                String.format("- Storing output in file: %s\n\n", clp.getOutputFileName()) +
                String.format("-- Cores used in determining schedule: %o\n", clp.getNumThreads()) +
                "-- visualisation " + (clp.isVisual() ? "on" : "off") +
                "\n";

        System.out.println(runInformation);

        // use clp here to make choices about what parts to execute.

        ListSchedulingAlgorithm listScheduler = new ListSchedulingAlgorithm(IOHandler.readFile(clp.getInputFileName()), clp.getNumProcessors());

        List<int[]> schedule = listScheduler.generateSchedule();

        testSchedule = schedule;
        numProcessors = clp.getNumProcessors();

        IOHandler.writeFile(listScheduler.scheduleToGraph(schedule), clp.getGraphName(), clp.getOutputFileName());

        System.out.println("Successfully created " + clp.getOutputFileName() + '\n');
        launch(args);
    }

    private static int numProcessors;
    private static List<int[]> testSchedule;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/MainScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        guiController = loader.getController();

        guiController.setup(numProcessors);

        guiController.setup(numProcessors);
        guiController.updateScheduleView(testSchedule);

        primaryStage.setTitle("Task Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
