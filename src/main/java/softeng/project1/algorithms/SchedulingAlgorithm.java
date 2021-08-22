package softeng.project1.algorithms;

import softeng.project1.gui.GuiData;

import java.util.List;

/**
 * An interface defining the minimal methods of a scheduling algorithm
 * Namely, generating an optimal schedule, and creating a GUI data
 * object that can be given to the GUI.
 */
public interface SchedulingAlgorithm {

    /**
     * Generate schedule is the main method for the scheduling algorithm.
     * It is used to turn the provided original schedule into the optimal schedule.
     * This is done by repeatedly expanding schedules, and placing them in a priority
     * queue till the optimal is found, at which case this method returns
     *
     * @return List<int [ ]> generatedSchedule, the optimal scheduling for the given original schedule state.
     */
    List<int[]> generateSchedule();


    /**
     * A helper method to transfer data from the running algorithm to the GUI.
     *
     * @return GuiData, a data object storing information needed by the GUI.
     */
    GuiData getGuiData();
}
