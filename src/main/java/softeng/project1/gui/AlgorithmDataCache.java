package softeng.project1.gui;

import softeng.project1.algorithms.SchedulingAlgorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 * Middle man service that allows for asynchronous data accessing from the GUI to the scheduling algorithm.
 * New data is requested from the algorithm every 100 milliseconds, although retrieval of that data can take varied
 * amounts of time depending on the algorithm implementation.
 *
 * The freshest available data is stored in the cache and can be accessed by the GUI as needed.
 */
public class AlgorithmDataCache {

    private static final int INITIAL_DATA_ACCESS_DELAY = 0;
    private static final int DATA_ACCESS_REQUEST_PERIOD = 100;

    private GuiData cachedData; // the cached data to use.

    public AlgorithmDataCache(SchedulingAlgorithm algorithm) {
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                cachedData = algorithm.getGuiData();
            }
        }, INITIAL_DATA_ACCESS_DELAY, DATA_ACCESS_REQUEST_PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * @return the data needed for the GUI to display the information
     */
    public GuiData readData() {
        return this.cachedData;
    }


}