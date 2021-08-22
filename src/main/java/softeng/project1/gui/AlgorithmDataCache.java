package softeng.project1.gui;

import softeng.project1.algorithms.SchedulingAlgorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class to store cached data from the algorithm, so that the GUI does not have to interrupt
 * the scheduling algorithm as much. It polls for new data at a fixed rate (100ms).
 */
public class AlgorithmDataCache {

    private final SchedulingAlgorithm algorithm; // the algorithm performing the scheduling
    private final ScheduledExecutorService scheduledExecutor; // the executor service for the algorithm
    private GuiData cachedData; // the cached data to use.

    public AlgorithmDataCache(SchedulingAlgorithm algorithm) {

        this.algorithm = algorithm;
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                cachedData = algorithm.getGuiData();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * @return the data needed for the GUI to display the information
     */
    public GuiData readData() {
        return this.cachedData;
    }


}