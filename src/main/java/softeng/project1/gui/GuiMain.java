package softeng.project1.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import softeng.project1.algorithms.AlgorithmState;
import softeng.project1.algorithms.SchedulingAlgorithm;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GuiMain extends Application {

    private static GuiController guiController;
    private static int numProcessors;
    private static List<int[]> testSchedule;
    private static Graph inputGraph;
    private static AlgorithmDataCache dataCache;
    private static SchedulingAlgorithm algorithm;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        guiController = loader.getController();
        guiController.setup(numProcessors, inputGraph, dataCache);

        primaryStage.setTitle("Task Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> System.exit(0));

        Timer repeat = new Timer();
        repeat.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    GuiData data = dataCache.readData();
                    if(!Objects.isNull(data)){
                        notifyGuiUpdate();

                        if(data.getAlgorithmState().equals(AlgorithmState.FINISHED)){
                            notifyGuiCompleted();
                            cancel();
                        }
                    }
                });
            }
        }, 0, 50);
    }

    public static void notifyGuiUpdate(){
        guiController.updateView(dataCache.readData());
    }

    public static void notifyGuiCompleted(){
        guiController.stopGui();
    }

    public static void setupGui(int processors, Graph graph, AlgorithmDataCache cache, SchedulingAlgorithm algo){
        numProcessors = processors;
        inputGraph = graph;
        dataCache = cache;
        algorithm = algo;
    }

    public static void startAlgorithm(){
        algorithm.generateSchedule();
    }
}
