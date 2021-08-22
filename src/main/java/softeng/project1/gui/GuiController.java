package softeng.project1.gui;

import com.sun.javafx.charts.Legend;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.List;

public class GuiController {

    @FXML
    private Button startScheduleButton;

    @FXML
    private CategoryAxis processors;

    @FXML
    private StackedBarChart<String, Number> schedule;

    @FXML
    private Label TimerText;

    @FXML
    private VBox InputContainer;

    @FXML
    private Label numProcessorsLabel;

    @FXML
    private Label numCoresLabel;

    @FXML
    private Label scheduleStatusLabel;

    @FXML
    private Label optimalLengthLabel;

    @FXML
    private Label numTasksLabel;

    @FXML
    private Label scheduleLengthLabel;

    private Timeline timeline;

    private int numProcessors;
    private long timer = 0;
    private List<Node> taskNames;

    /**
     * Initializes the timer
     */
    public void initialize() {
        TimerText.setText("00:00:000");

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), ae -> updateTimer()));
    }

    /**
     * Setups required fields for controller
     *
     * @param numProcessors number of processors to divide tasks on
     */
    public void setup(int numProcessors, int numCores, Graph g, List<Node> taskNames) {
        //Setup fields
        this.numProcessors = numProcessors;

        updateNumProcessors(numProcessors);
        updateNumCores(numCores);
        updateNumTasks(taskNames.size());

        //Setup requirements for schedule display
        List<String> processorNums = new ArrayList<>();
        for (int i = 0; i < numProcessors; i++) {
            processorNums.add(Integer.toString(i + 1));
        }
        processors.setCategories(FXCollections.observableArrayList(processorNums));
        schedule.setAnimated(false);

        //Setup input graph display
        FxViewer viewer = new FxViewer(g, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        FxViewPanel viewPanel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());

        InputContainer.getChildren().add(viewPanel);
        this.taskNames = taskNames;
    }

    @FXML
    void startSchedule(ActionEvent event) {
        timeline.play();
        startScheduleButton.setDisable(true);
        timer = System.nanoTime();
        // Starts the algorithm service, which finds the optimal schedule.
        GuiMain.startAlgorithm();
    }

    public void updateView(GuiData data) {
        updateScheduleView(data.getCurrentBestSchedule().rebuildPath());
        updateScheduleLength(data.getHeuristic());
    }

    public void stopGui(GuiData data) {

        timeline.stop();
        updateScheduleStatus("COMPLETED");
        updateOptimalLength(data.getHeuristic());
    }

    /**
     * Updates the schedule display given a schedule
     *
     * @param newSchedule A schedule
     */

    public void updateScheduleView(List<int[]> newSchedule) {
        schedule.getData().clear();

        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        XYChart.Series<String, Number> idleSeries = new XYChart.Series<>();
        seriesList.add(idleSeries);

        //Separates tasks into lists of processors
        List<List<Integer>> processorTasks = new ArrayList<>();
        for (int i = 0; i < this.numProcessors; i++) {
            processorTasks.add(new ArrayList<>());
        }

        for (int[] task : newSchedule) {
            processorTasks.get(task[1]).add(task[0]);
        }

        List<Legend.LegendItem> legendItems = new ArrayList<>();

        //For each processor, iterate through each processor, then for each processor, determine if it needs idle time before it
        //Then create idle time bar
        for (List<Integer> processor : processorTasks) {
            for (int i = 0; i < processor.size(); i++) {

                //Gets current Task from schedule
                int taskID = processor.get(i);
                int currentTaskIndex = findTaskIndex(newSchedule, taskID);
                int[] currentTask = newSchedule.get(currentTaskIndex);

                //Current Task's attributes
                int currentProcessor = currentTask[1];
                int startTime = currentTask[2];
                int weight = currentTask[3];

                //Add idle block if there is any before current task
                try {
                    //Get details of previous task
                    int prevTaskID = processor.get(i - 1);
                    int[] prevTask = newSchedule.get(findTaskIndex(newSchedule, prevTaskID));
                    int prevTaskCompletionTime = prevTask[2] + prevTask[3];

                    //If previous completion time is not the same as start time, then need to add idle time
                    if (prevTaskCompletionTime != startTime) {
                        //Add transparent idle block
                        XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), prevTaskCompletionTime - startTime);
                        block.nodeProperty().addListener((ov, oldNode, node) -> node.setStyle("-fx-bar-fill: transparent"));

                        idleSeries.getData().add(block);
                    }

                    //If previous task doesn't exist then it is the first task on the processor
                } catch (IndexOutOfBoundsException e) {
                    XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), startTime);
                    block.nodeProperty().addListener((ov, oldNode, node) -> node.setStyle("-fx-bar-fill: transparent"));
                    idleSeries.getData().add(block);
                }

                //Add block to schedule
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
                XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), weight);

                //Assigns random colour to bar.
                Color colour = generateRandomRGBColor();
                block.nodeProperty().addListener((ov, oldNode, node) -> node.setStyle("-fx-bar-fill: " + colour.toString().replace("0x", "#") + ""));
                newSeries.getData().add(block);
                seriesList.add(newSeries);

                //Generates Legend entry for series
                legendItems.add(new Legend.LegendItem(taskNames.get(taskID).getId(), new Rectangle(10, 10, colour)));
            }

        }
        schedule.getData().addAll(seriesList);

        //Creates legend
        Legend legend = (Legend) schedule.lookup(".chart-legend");
        legend.getItems().removeAll();
        legend.getItems().setAll(legendItems);

    }

    /**
     * Increments the timer label each second incrementing by one second
     */
    public void updateTimer() {
//        timer = timer.plusSeconds(1);
        TimerText.setText(getTimeElapsed());
    }

    /**
     * Helper method used to find index of task in schedule list given a taskID
     *
     * @param schedule A list of tasks
     * @param taskID   ID to search for
     * @return Returns the index of task with given taskID, returns -1 if doesn't exist
     */
    private int findTaskIndex(List<int[]> schedule, int taskID) {
        for (int i = 0; i < schedule.size(); i++) {
            if (schedule.get(i)[0] == taskID) {
                return i;
            }
        }

        return -1;
    }

    public void updateNumProcessors(int numProcessors) {
        numProcessorsLabel.setText(Integer.toString(numProcessors));
    }

    public void updateNumCores(int numCores) {
        numCoresLabel.setText(Integer.toString(numCores));
    }

    public void updateScheduleStatus(String scheduleStatus) {
        scheduleStatusLabel.setText(scheduleStatus);
        scheduleStatusLabel.setStyle("-fx-text-fill: green");
    }

    public void updateOptimalLength(int optimalLength) {
        optimalLengthLabel.setText(Integer.toString(optimalLength));
    }

    public void updateNumTasks(int numTasks) {
        numTasksLabel.setText(Integer.toString(numTasks));
    }

    public void updateScheduleLength(int scheduleLength) {
        scheduleLengthLabel.setText(Integer.toString(scheduleLength));
    }

    private String getTimeElapsed() {
        long end = System.nanoTime();
        long duration = (end - timer) / 1000000;

        int minutes = (int) (duration / 60000);
        int seconds = (int) ((duration - minutes * 60000) / 1000);
        int millis = (int) (duration - (minutes * 60000) - (seconds * 1000));
        return String.format("%02d:%02d:%03d", minutes, seconds, millis);
    }

    /**
     * Generates a random RGB colour
     *
     * @return Returns the colour object
     */
    private Color generateRandomRGBColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }
}
