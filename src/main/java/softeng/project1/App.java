package softeng.project1;

import org.graphstream.graph.Graph;
import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AStarHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.algorithms.astar.parallel.ParallelAStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.sequential.SequentialAStarSchedulingAlgorithm;
import softeng.project1.graph.Schedule;
import softeng.project1.gui.AlgorithmDataCache;
import softeng.project1.gui.GuiMain;
import softeng.project1.io.AStarIOHandler;
import softeng.project1.io.CommandLineProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


public final class App {

    /**
     * Runs the scheduling program
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        System.setProperty("org.graphstream.ui", "javafx");
        CommandLineProcessor clp = new CommandLineProcessor(args);

        String runInformation = "***** Outsourced to Pakistan - Scheduling Algorithm *****\n" +
                String.format("- Creating schedule for input graph from file: %s\n", clp.getInputFileName()) +
                String.format("- Number of Processors Available for Tasks: %o\n", clp.getNumProcessors()) +
                String.format("- Storing output in file: %s\n\n", clp.getOutputFileName()) +
                String.format("-- Cores used in determining schedule: %d\n", clp.getNumThreads()) +
                "-- visualisation " + (clp.isVisual() ? "on" : "off") +
                "\n";

        System.out.println(runInformation);

        // use clp here to make choices about what parts to execute.
        AStarIOHandler ioHandler = new AStarIOHandler(
                clp.getInputFileName(),
                clp.getOutputFileName(),
                clp.getGraphName(),
                clp.getNumProcessors()
        );

        Schedule originalSchedule = ioHandler.readFile();
        HeuristicManager heuristicManager = new AStarHeuristicManager(ioHandler.getSumWeights(), clp.getNumProcessors(), ioHandler.getListSchedulingAlgoStep());
        SchedulingAlgorithm algorithm;

        if (clp.getNumThreads() > 1) {
            // PARALLEL
            algorithm = new ParallelAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    clp.getNumThreads(),
                    ioHandler.getListSchedulingAlgoStep()
            );
        } else {
            // SEQUENTIAL
            algorithm = new SequentialAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    new HashMap<>(),
                    new PriorityQueue<>(),
                    ioHandler.getListSchedulingAlgoStep()
            );
        }

        Graph inputGraph = ioHandler.getGraph();
        List<int[]> optimalSchedule = algorithm.generateSchedule();

        if (clp.isVisual()) {
        AlgorithmDataCache dataCache = new AlgorithmDataCache(algorithm);
            GuiMain.setupGui(clp.getNumProcessors(), optimalSchedule, inputGraph);
            GuiMain.main(args);
            System.out.println(optimalSchedule);
        }


        String result = ioHandler.writeFile(optimalSchedule);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;  //divide by  to get milliseconds.

        System.out.printf("Took: %d ms to complete.%n", duration);
        System.out.println(result);

        System.out.println("Successfully created " + clp.getOutputFileName() + '\n');

    }
}
