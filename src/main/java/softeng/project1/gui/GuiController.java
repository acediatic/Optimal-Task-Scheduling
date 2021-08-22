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
import java.util.Collections;
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

    @FXML
    private Label inputFileName;

    @FXML
    private Label numSchedulesCheckedLabel;

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
     * Sets up required fields and input graph display
     *
     * @param numProcessors The number of processors the task is scheduled on
     * @param numCores      The number of cores/threads the algorithm is run on
     * @param g             The input graph
     * @param taskNames     List of names for each node in input graph
     */
    public void setup(int numProcessors, int numCores, Graph g, List<Node> taskNames, String inputFileName) {
        //Setup fields
        this.numProcessors = numProcessors;
        this.taskNames = taskNames;

        //Sets description labels
        updateNumProcessors(numProcessors);
        updateNumCores(numCores);
        updateNumTasks(taskNames.size());
        updateInputFileName("Input: " + inputFileName);

        //Setup requirements for schedule display
        List<String> processorNums = new ArrayList<>();
        for (int i = 0; i < numProcessors; i++) {
            processorNums.add(Integer.toString(i));
        }
        processors.setCategories(FXCollections.observableArrayList(processorNums));
        schedule.setAnimated(false);

        //Setup input graph display
        FxViewer viewer = new FxViewer(g, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        FxViewPanel viewPanel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());
        InputContainer.getChildren().add(viewPanel);
    }

    /**
     * Event handler for when start button is pressed.
     * Starts the timer as well as the scheduling algorithm.
     * Also disables start button
     *
     * @param event Button event
     */
    @FXML
    void startSchedule(ActionEvent event) {
        timeline.play();
        startScheduleButton.setDisable(true);
        timer = System.nanoTime();
        // Starts the algorithm service, which finds the optimal schedule.
        GuiMain.startAlgorithm();
        updateScheduleStatus("SCHEDULING", "red");
    }

    /**
     * Method for updating the gui view given data from the algorithm
     *
     * @param data GuiData object which provides data fetched from algorithm
     */
    public void updateView(GuiData data) {
        if (data.getCurrentBestScheduleArray() != null) {
            updateScheduleView(data.getCurrentBestScheduleArray());
        }
        updateScheduleLength(data.getHeuristic());
        updateSchedulesChecked(data.getNumSchedulesChecked());
    }

    /**
     * Stops the timer and updates the schedule status
     * Also updates
     *
     * @param data GuiData object which provides data fetched from algorithm
     */
    public void stopGui(GuiData data) {
        timeline.stop();
        updateScheduleStatus("COMPLETED", "green");
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
        List<List<Integer>> processorStartTimes = new ArrayList<>();
        List<List<int[]>> processorTasks = new ArrayList<>();

        for (int i = 0; i < this.numProcessors; i++) {
            processorTasks.add(new ArrayList<>());
            processorStartTimes.add(new ArrayList<>());
        }

        //Add start times to processor
        for (int[] task : newSchedule) {
            processorTasks.get(task[1]).add(task);
            processorStartTimes.get(task[1]).add(task[2]);
        }

        List<Legend.LegendItem> legendItems = new ArrayList<>();

        //Iterate through each processor, then schedule tasks or add idle time as needed
        for (int i = 0; i < numProcessors; i++) {
            List<Integer> currentProcessorStartTimes = processorStartTimes.get(i); //Regular Indexed

            //Sorts processor's tasks by start time
            List<Integer> sortedStartTimes = new ArrayList<>(currentProcessorStartTimes);
            Collections.sort(sortedStartTimes);

            //Processes tasks in order of startTime
            for (int j = 0; j < sortedStartTimes.size(); j++) {
                //Gets current Task from processor
                int taskStartTime = sortedStartTimes.get(j);
                int currentTaskIndex = currentProcessorStartTimes.indexOf(taskStartTime);
                int[] currentTask = processorTasks.get(i).get(currentTaskIndex);

                //Current Task's attributes
                int taskID = currentTask[0];
                int currentProcessor = currentTask[1];
                int startTime = currentTask[2];
                int weight = currentTask[3];

                //Add idle block if there is any before current task
                try {
                    //Get details of previous task
                    int prevTaskStartTime = sortedStartTimes.get(j - 1);
                    int prevTaskIndex = currentProcessorStartTimes.indexOf(prevTaskStartTime);
                    int[] prevTask = processorTasks.get(i).get(prevTaskIndex);
                    int prevTaskCompletionTime = prevTask[2] + prevTask[3];

                    //If previous completion time is not the same as start time, then need to add idle time
                    if (prevTaskCompletionTime != startTime) {
                        //Add transparent idle block
                        XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor), startTime - prevTaskCompletionTime);
                        block.nodeProperty().addListener((ov, oldNode, node) -> node.setStyle("-fx-bar-fill: transparent"));
                        idleSeries.getData().add(block);
                    }

                    //If previous task doesn't exist then it is the first task on the processor
                } catch (IndexOutOfBoundsException e) {
                    XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor), startTime);
                    block.nodeProperty().addListener((ov, oldNode, node) -> node.setStyle("-fx-bar-fill: transparent"));
                    idleSeries.getData().add(block);
                }

                //Add task block to schedule
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
                XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor), weight);

                //Assigns random colour to block
                Color colour = generateRandomRGBColor();
                block.nodeProperty().addListener((ov, oldNode, node) -> node.setStyle("-fx-bar-fill: " + colour.toString().replace("0x", "#") + ""));
                newSeries.getData().add(block);
                seriesList.add(newSeries);

                //Generates Legend entry for task
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
     * Increments Timer label
     */
    public void updateTimer() {
        TimerText.setText(getTimeElapsed());
    }

    /**
     * Updates Label displaying number of processors tasks are scheduled on
     *
     * @param numProcessors number of processors tasks are scheduled on
     */
    public void updateNumProcessors(int numProcessors) {
        numProcessorsLabel.setText(Integer.toString(numProcessors));
    }

    /**
     * Updates Label displaying number of cores/threads the algorithm is running on
     *
     * @param numCores number of cores/threads the algorithm is running on
     */
    public void updateNumCores(int numCores) {
        numCoresLabel.setText(Integer.toString(numCores));
    }

    /**
     * Sets Label displaying current status of algorithm to complete
     *
     * @param scheduleStatus Schedule status
     */
    public void updateScheduleStatus(String scheduleStatus, String colourString) {
        scheduleStatusLabel.setText(scheduleStatus);
        scheduleStatusLabel.setStyle("-fx-text-fill: " + colourString);
    }

    /**
     * Updates label displaying optimal length of the schedule
     *
     * @param optimalLength optimal length of schedule
     */
    public void updateOptimalLength(int optimalLength) {
        optimalLengthLabel.setText(Integer.toString(optimalLength));
    }

    /**
     * Updates label displaying number of tasks to be scheduled
     *
     * @param numTasks Number of tasks to be scheduled
     */
    public void updateNumTasks(int numTasks) {
        numTasksLabel.setText(Integer.toString(numTasks));
    }

    /**
     * Updates Label displaying current estimate for best schedule length
     *
     * @param scheduleLength estimate for schedule length
     */
    public void updateScheduleLength(int scheduleLength) {
        scheduleLengthLabel.setText(Integer.toString(scheduleLength));
    }

    /**
     * Updates Label displaying number of schedules checked
     *
     * @param numStatesChecked number of schedules checked
     */
    public void updateSchedulesChecked(int numStatesChecked) {
        numSchedulesCheckedLabel.setText(Integer.toString(numStatesChecked));
    }

    public void updateInputFileName(String fileName) {
        inputFileName.setText(fileName);
    }

    /**
     * Helper method to calculate the time elapsed since start button pressed
     *
     * @return time elapsed
     */
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
