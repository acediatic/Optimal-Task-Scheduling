package softeng.project1.algorithms;

import java.util.List;

public interface SchedulingAlgorithm {

    List<int[]> generateSchedule();

    int addReporterTask();
}
