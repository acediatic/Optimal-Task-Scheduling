package softeng.project1.algorithms;

import softeng.project1.graph.processors.processor.Processor;

import java.util.List;

public interface SchedulingAlgorithm {


    List<int[]> generateSchedule();

    int addReporterTask();
}
