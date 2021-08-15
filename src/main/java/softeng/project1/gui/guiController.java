package softeng.project1.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class guiController {

    @FXML
    private StackedBarChart<String, Number> schedule;

    @FXML
    private CategoryAxis processors;

    @FXML
    private Label TimerText;

    private int numProcessors;
    private int numTasks;


    public void setup(int processors, int tasks){
        numProcessors = processors;
        numTasks = tasks;
    }

    @FXML
    public void initialize(){



        //Sets up x-axis with desired number of processors
        List<String> processorNums = new ArrayList<>();
        for (int i = 0; i < numProcessors; i++) {
            processorNums.add(Integer.toString(i + 1));
        }

        processors.setCategories(FXCollections.observableArrayList(processorNums));
    }


    /**
     * Updates the schedule view with current best schedule
     */
    public void updateScheduleView(){

    }

}
