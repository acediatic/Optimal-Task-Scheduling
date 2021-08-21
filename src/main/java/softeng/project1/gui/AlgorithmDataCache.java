package softeng.project1.gui;

import softeng.project1.algorithms.SchedulingAlgorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AlgorithmDataCache {

    private final SchedulingAlgorithm algorithm;
    private final ScheduledExecutorService scheduledExecutor;
    private GuiData cachedData;

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

    public GuiData readData() {
        return this.cachedData;
    }


}