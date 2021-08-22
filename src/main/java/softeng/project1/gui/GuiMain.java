package softeng.project1.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.AlgorithmService;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GuiMain extends Application {

    private static GuiController guiController;
    private static int numProcessors;
    private static int numCores;
    private static Graph inputGraph;
    private static AlgorithmDataCache dataCache;
    private static AlgorithmService algorithmRunner;
    private static List<Node> taskNames;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Calls update method from GuiController to update the view with new data
     */
    public static void notifyGuiUpdate() {
        guiController.updateView(dataCache.readData());
    }

    /**
     * Notifies the gui that the algorithm has completed and stops the Gui from updating view
     */
    public static void notifyGuiCompleted() {
        guiController.stopGui(dataCache.readData());
    }

    /**
     * Starts running the scheduling algorithm
     */
    public static void startAlgorithm() {
        algorithmRunner.start();
    }

    /**
     * Setting up variables to be used in start method
     * @param processors The number of processors to schedule tasks on
     * @param cores The number of cores/threads the scheduling algorithm uses
     * @param graph The input graph
     * @param cache The data cache which polls for data from the algorithm
     * @param names The list of names/IDs for each node in input graph
     * @param algorithmService The Service which runs the algorithm in a background thread
     */
    public static void setupGui(int processors, int cores, Graph graph, AlgorithmDataCache cache,
                                List<Node> names, AlgorithmService algorithmService) {
        numProcessors = processors;
        inputGraph = graph;
        taskNames = names;
        numCores = cores;
        dataCache = cache;
        algorithmRunner = algorithmService;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Sets up FXML file and loads scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        //Sets up fields for the GuiController
        guiController = loader.getController();
        guiController.setup(numProcessors, numCores, inputGraph, taskNames);

        primaryStage.setTitle("Task Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        //Timer polls AlgorithmDataCache object for data every 50ms
        Timer repeat = new Timer();
        repeat.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    GuiData data = dataCache.readData();
                    //If the data object is not null, then the algoritm must have started
                    if (!Objects.isNull(data)) {
                        notifyGuiUpdate();

                        //Stop polling and notify gui once the algorithm finised
                        if (data.getAlgorithmState().equals(AlgorithmState.FINISHED)) {
                            notifyGuiCompleted();
                            cancel();
                        }
                    }
                });
            }
        }, 0, 50);
    }


}
