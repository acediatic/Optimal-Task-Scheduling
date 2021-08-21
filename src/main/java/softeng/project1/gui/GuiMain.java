package softeng.project1.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;

import java.io.File;
import java.util.List;
import java.util.Map;

public class GuiMain extends Application {

    private static GuiController guiController;
    private static int numProcessors;
    private static int numCores;
    private static List<int[]> testSchedule;
    private static Graph inputGraph;
    private static Map<Short, String> taskNames;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        guiController = loader.getController();
        guiController.setup(numProcessors, numCores, inputGraph, taskNames);
        guiController.updateScheduleView(testSchedule);

        primaryStage.setTitle("Task Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void setupGui(int processors, int cores, List<int[]> schedule, Graph graph, Map<Short, String> names){
        numProcessors = processors;
        testSchedule = schedule;
        inputGraph = graph;
        taskNames = names;
        numCores = cores;
    }
}
