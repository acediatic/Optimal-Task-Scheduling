package softeng.project1.algorithms.astar;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import softeng.project1.algorithms.SchedulingAlgorithm;

import java.util.List;

public class AlgorithmService extends Service<List<int[]>> {
    private final SimpleObjectProperty<SchedulingAlgorithm> algorithm = new SimpleObjectProperty<>();

    public final SchedulingAlgorithm getAlgorithm() {
        return algorithm.get();
    }

    public final void setAlgorithm(SchedulingAlgorithm value) {
        algorithm.set(value);
    }

    protected Task<List<int[]>> createTask() {
        final SchedulingAlgorithm algorithm = getAlgorithm();
        return new Task<List<int[]>>() {
            protected List<int[]> call() {
                return algorithm.generateSchedule();
            }
        };
    }
}


