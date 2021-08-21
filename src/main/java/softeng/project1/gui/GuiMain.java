package softeng.project1.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.astar.AlgorithmService;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GuiMain extends Application {

    private static GuiController guiController;
    private static int numProcessors;
    private static int numCores;
    private static Graph inputGraph;
    private static Map<Short, String> taskNames;
    private static AlgorithmDataCache dataCache;
    private static AlgorithmService algorithmRunner;

    public static void main(String[] args) {
        launch(args);
    }

    public static void notifyGuiUpdate() {
        guiController.updateView(dataCache.readData());
    }

    public static void notifyGuiCompleted() {
        guiController.stopGui();
    }

    public static void setupGui(int processors, int cores, Graph graph, AlgorithmDataCache cache,
                                Map<Short, String> names, AlgorithmService algorithmService) {
        numProcessors = processors;
        inputGraph = graph;
        taskNames = names;
        numCores = cores;
        dataCache = cache;
        algorithmRunner = algorithmService;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        guiController = loader.getController();
        guiController.setup(numProcessors, numCores, inputGraph, taskNames, dataCache);

        primaryStage.setTitle("Task Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        // Starts the algorithm service, which finds the optimal schedule.
        algorithmRunner.start();

        Timer repeat = new Timer();
        repeat.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    GuiData data = dataCache.readData();
                    if (!Objects.isNull(data)) {
                        notifyGuiUpdate();

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
