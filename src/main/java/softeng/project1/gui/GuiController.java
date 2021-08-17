package softeng.project1.gui;

import com.sun.org.apache.xml.internal.utils.ListingErrorHandler;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class GuiController {

    @FXML
    private CategoryAxis processors;

    @FXML
    private NumberAxis startTime;

    @FXML
    private StackedBarChart<String, Number> schedule;

    @FXML
    private Label TimerText;

    private int numProcessors;
    private int numTasks;

    /**
     * Setups required fields for controller
     * @param numProcessors number of processors to divide tasks on
     */
    public void setup(int numProcessors, int numTasks){
        this.numProcessors = numProcessors;
        this.numTasks = numTasks;

        List<String> processorNums = new ArrayList<>();
        for (int i = 0; i < numProcessors; i++) {
            processorNums.add(Integer.toString(i + 1));
        }

        processors.setCategories(FXCollections.observableArrayList(processorNums));
        schedule.setAnimated(false);
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

                } catch (ArrayIndexOutOfBoundsException e){ //If previous task doesn't exist then it is the first task on the processor
                    XYChart.Data<String, Number> block = new XYChart.Data<>(Integer.toString(currentProcessor + 1), startTime);
                    block.nodeProperty().addListener((ov, oldNode, node) -> {
                        node.setStyle("-fx-bar-fill: transparent");
                    });
                    idleSeries.getData().add(block);
                }

                //Add block to schedule
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
//                newSeries.setName("Task " + taskID);
                newSeries.getData().add(new XYChart.Data<>(Integer.toString(currentProcessor + 1), weight));
                schedule.getData().addAll(newSeries);

            }
            schedule.getData().addAll(idleSeries);

        }
    }

    /**
     * Helper method used to find index of task in schedule list given a taskID
     * @param schedule A list of tasks
     * @param taskID ID to search for
     * @return Returns the index of task with given taskID, returns -1 if doesn't exist
     */
    private int findTaskIndex(List<int[]> schedule, int taskID){
        for(int[] task: schedule){
            if(task[0] == taskID){
                return task[0];
            }
        }

        return -1;
    }

}
