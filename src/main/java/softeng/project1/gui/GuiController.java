package softeng.project1.gui;

import com.sun.javafx.charts.Legend;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
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
import javafx.scene.Node.*;


//import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GuiController {

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

    private LocalTime timer = LocalTime.parse("00:00");
    private Timeline timeline;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");

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
        timeline.play();
    }

    /**
     * Setups required fields for controller
     * @param numProcessors number of processors to divide tasks on
     */
    public void setup(int numProcessors, Graph g, Map<Short, String> taskNames){
        //Setup fields
        this.numProcessors = numProcessors;

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
        FxViewPanel viewPanel = (FxViewPanel)viewer.addDefaultView(false, new FxGraphRenderer());

        InputContainer.getChildren().add(viewPanel);

        this.taskNames = taskNames;
    }

    /**
     * Updates the schedule display given a schedule
     * @param newSchedule A schedule
     */

    public void updateScheduleView(List<int[]> newSchedule){
        XYChart.Series<String, Number> idleSeries = new XYChart.Series<>();

        //Separates tasks into lists of processors
        List<List<Integer>> processorTasks = new ArrayList<>();
        for(int i = 0; i < this.numProcessors; i++){
            processorTasks.add(new ArrayList<>());
        }

        for(int[] task : newSchedule){
            processorTasks.get(task[1]).add(task[0]);
        }


        List<Legend.LegendItem> legendItems = new ArrayList<>();
//        legend.getItems().seta

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
                } catch (ArrayIndexOutOfBoundsException e){
                    XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), startTime);
                    block.nodeProperty().addListener((ov, oldNode, node) -> {
                        node.setStyle("-fx-bar-fill: transparent");
                    });
                    idleSeries.getData().add(block);
                }

                //Add block to schedule
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
//                newSeries.setName("Task " + taskID);

                XYChart.Data<String, Number>block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), weight);
                Color colour = generateRandomRGBColor();

                System.out.println(colour.toString().replace("0x", "#"));

                String colorString = String.format("%f, %f, %f", colour.getRed(), colour.getBlue(), colour.getRed());
                System.out.println(colorString);
                block.nodeProperty().addListener((ov, oldNode, node) -> {
                    node.setStyle("-fx-bar-fill: " + colour.toString().replace("0x", "#") + "");
                });

                newSeries.getData().add(block);
                newSeries.setName(taskNames.get((short)taskID));

                schedule.getData().addAll(newSeries);

                legendItems.add(new Legend.LegendItem(taskNames.get((short)taskID), new Rectangle(10, 10, colour)));

            }
            Legend legend = (Legend)schedule.lookup(".chart-legend");
            legend.getItems().removeAll();
            legend.getItems().setAll(legendItems);
            schedule.getData().addAll(idleSeries);

        }
    }

    /**
     * Increments the timer label each second incrementing by one second
     */
    public void updateTimer(){
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

    private Color generateRandomRGBColor(){

        Color color = Color.color(Math.random(), Math.random(), Math.random());


        return color;
    };
}
