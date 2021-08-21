package softeng.project1.gui;

import com.sun.javafx.charts.Legend;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.graphstream.graph.Graph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.Viewer;
import javafx.scene.paint.Color;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;

public class GuiController {

    @FXML
    private Button startScheduleButton;

    @FXML
    private CategoryAxis processors;

    @FXML
    private NumberAxis startTime;

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

    private LocalTime timer = LocalTime.parse("00:00");
    private Timeline timeline;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
    private AlgorithmDataCache dataCache;

    private int numProcessors;
    private Map<Short, String> taskNames;

    /**
     * Initializes the timer
     */
    public void initialize(){
        TimerText.setText(timer.format(formatter));

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), ae -> updateTimer()));
    }

    /**
     * Setups required fields for controller
     * @param numProcessors number of processors to divide tasks on
     */
    public void setup(int numProcessors,int numCores, Graph g, Map<Short, String> taskNames, AlgorithmDataCache dataCache) {
        //Setup fields
        this.numProcessors = numProcessors;
        this.dataCache = dataCache;

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
        GuiMain.startAlgorithm();
    }

    public void updateView(GuiData data){
        updateScheduleView(data.getCurrentBestSchedule().rebuildPath());
        updateScheduleStatus("COMPLETED");
    }

    public void stopGui(){
        timeline.stop();
    }

    /**
     * Updates the schedule display given a schedule
     * @param newSchedule A schedule
     */

    public void updateScheduleView(List<int[]> newSchedule){
        schedule.getData().clear();

        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        XYChart.Series<String, Number> idleSeries = new XYChart.Series<>();
        seriesList.add(idleSeries);

        //Separates tasks into lists of processors
        List<List<Integer>> processorTasks = new ArrayList<>();
        for(int i = 0; i < this.numProcessors; i++){
            processorTasks.add(new ArrayList<>());
        }

        for(int[] task : newSchedule){
            processorTasks.get(task[1]).add(task[0]);
        }

        List<Legend.LegendItem> legendItems = new ArrayList<>();

        //For each processor, iterate through each processor, then for each processor, determine if it needs idle time before it
        //Then create idle time bar
        for(List<Integer> processor: processorTasks){
            for(int i = 0; i < processor.size(); i++){

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
                    if(prevTaskCompletionTime != startTime){
                            //Add transparent idle block
                            XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1),  prevTaskCompletionTime - startTime);
                            block.nodeProperty().addListener((ov, oldNode, node) ->{
                                        node.setStyle("-fx-bar-fill: transparent");
                            });

                            idleSeries.getData().add(block);
                    }

                //If previous task doesn't exist then it is the first task on the processor
                } catch (IndexOutOfBoundsException e){
                    XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), startTime);
                    block.nodeProperty().addListener((ov, oldNode, node) -> {
                        node.setStyle("-fx-bar-fill: transparent");
                    });
                    idleSeries.getData().add(block);
                }

                //Add block to schedule
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
                XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), weight);

                //Assigns random colour to bar.
                Color colour = generateRandomRGBColor();
                block.nodeProperty().addListener((ov, oldNode, node) -> {
                    node.setStyle("-fx-bar-fill: " + colour.toString().replace("0x", "#") + "");
                });
                newSeries.getData().add(block);
                seriesList.add(newSeries);

                //Generates Legend entry for series
                legendItems.add(new Legend.LegendItem(taskNames.get((short)taskID), new Rectangle(10, 10, colour)));
            }

        }
        schedule.getData().addAll(seriesList);

        //Creates legend
        Legend legend = (Legend)schedule.lookup(".chart-legend");
        legend.getItems().removeAll();
        legend.getItems().setAll(legendItems);

    }

    /**
     * Increments the timer label each second incrementing by one second
     */
    public void updateTimer() {
        timer = timer.plusSeconds(1);
        TimerText.setText(timer.format(formatter));
    }

    /**
     * Helper method used to find index of task in schedule list given a taskID
     * @param schedule A list of tasks
     * @param taskID ID to search for
     * @return Returns the index of task with given taskID, returns -1 if doesn't exist
     */
    private int findTaskIndex(List<int[]> schedule, int taskID){
        for(int i = 0; i < schedule.size(); i++){
            if(schedule.get(i)[0] == taskID){
                return i;
            }
        }

        return -1;
    }

    public void updateNumProcessors(String numProcessors){
        numProcessorsLabel.setText(numProcessors);
    }

    public void updateNumCores(String numCores){
        numCoresLabel.setText(numCores);
    }

    public void updateScheduleStatus(String scheduleStatus){
        scheduleStatusLabel.setText(scheduleStatus);
        scheduleStatusLabel.setStyle("-fx-text-fill: green");
    }

    public void updateOptimalLength(String optimalLength){
        optimalLengthLabel.setText(optimalLength);

    }

    public void updateNumTasks(String numTasks){
        numTasksLabel.setText(numTasks);
    }

    public void updateScheduleLength(String scheduleLength){
        scheduleLengthLabel.setText(scheduleLength);
    }

    /**
     * Generates a random RGB colour
     * @return Returns the colour object
     */
    private Color generateRandomRGBColor(){
        Color color = Color.color(Math.random(), Math.random(), Math.random());
        return color;
    };
}
