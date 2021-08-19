package softeng.project1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AStarHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.algorithms.astar.parallel.ParallelAStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.sequential.SequentialAStarSchedulingAlgorithm;
import softeng.project1.graph.Schedule;
import softeng.project1.gui.GuiController;
import softeng.project1.io.AStarIOHandler;
import softeng.project1.io.CommandLineProcessor;
import softeng.project1.io.IOHandler;

import java.util.List;
import java.util.HashMap;
import java.util.PriorityQueue;


public final class App extends Application {

    private GuiController guiController;
    private static int numProcessors;
    private static List<int[]> testSchedule;
    private static Graph inputGraph;


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
        IOHandler ioHandler = new AStarIOHandler(
                clp.getInputFileName(),
                clp.getOutputFileName(),
                clp.getGraphName(),
                clp.getNumProcessors()
        );

        Schedule originalSchedule = ioHandler.readFile();
        HeuristicManager heuristicManager = new AStarHeuristicManager(ioHandler.getSumWeights(), clp.getNumProcessors());
        SchedulingAlgorithm algorithm;

        if (clp.getNumThreads() > 1) {
            algorithm = new ParallelAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    clp.getNumThreads()
            );
        } else {
            algorithm = new SequentialAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    new HashMap<>(),
                    new PriorityQueue<>()
            );
        }

        numProcessors = clp.getNumProcessors();
        inputGraph = ((AStarIOHandler) ioHandler).getGraph();
        testSchedule = algorithm.generateSchedule();

        System.out.println(testSchedule);

        String result = ioHandler.writeFile(testSchedule);
        System.out.println(result);

        System.out.println("Successfully created " + clp.getOutputFileName() + '\n');
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/MainScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        guiController = loader.getController();
        guiController.setup(numProcessors, inputGraph);
        guiController.updateScheduleView(testSchedule);

        primaryStage.setTitle("Task Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
