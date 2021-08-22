package softeng.project1.algorithms.astar;

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import softeng.project1.algorithms.SchedulingAlgorithm;

import java.util.List;

/**
 * This class creates a service that can be run by the JavaFX GUI thread. It allows
 * the GUI to begin the finding of the optimal solution, and display information
 * about the search while it is running.
 */
public class AlgorithmService extends Service<List<int[]>> {
    private final SimpleObjectProperty<SchedulingAlgorithm> algorithm = new SimpleObjectProperty<>();

    public final SchedulingAlgorithm getAlgorithm() {
        return algorithm.get();
    }

    public final void setAlgorithm(SchedulingAlgorithm value) {
        algorithm.set(value);
    }

    /**
     * This prunes the expanded schedules we've already explored, and adds those that
     * haven't been seen before to the map of now seen schedules. This uses a hash,
     * where collisions are resolved by using a deep equals check. The consistency
     * of our heuristic allows us to do nothing if we have already explored the schedule.
     *
     * @return the list of as yet unexplored schedules that should now be explored
     */
    protected Task<List<int[]>> createTask() {
        final SchedulingAlgorithm algorithm = getAlgorithm();
        return new Task<List<int[]>>() {
            protected List<int[]> call() {
                return algorithm.generateSchedule();
            }
        };
    }
}


