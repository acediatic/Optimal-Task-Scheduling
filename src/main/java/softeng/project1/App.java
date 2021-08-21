package softeng.project1;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.AlgorithmService;
import softeng.project1.algorithms.astar.heuristics.AStarHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.algorithms.astar.parallel.ParallelAStarSchedulingAlgorithm;
import softeng.project1.algorithms.astar.sequential.SequentialAStarSchedulingAlgorithm;
import softeng.project1.graph.Schedule;
import softeng.project1.gui.AlgorithmDataCache;
import softeng.project1.gui.GuiMain;
import softeng.project1.io.AStarIOHandler;
import softeng.project1.io.CommandLineProcessor;
import softeng.project1.io.IOHandler;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;


public final class App {

    /**
     * Runs the scheduling program
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "javafx");
        CommandLineProcessor clp = new CommandLineProcessor(args);

        String runInformation = "***** Outsourced to Pakistan - Scheduling Algorithm *****\n" +
                String.format("- Creating schedule for input graph from file: %s\n", clp.getInputFileName()) +
                String.format("- Number of Processors Available for Tasks: %o\n", clp.getNumProcessors()) +
                String.format("- Storing output in file: %s\n\n", clp.getOutputFileName()) +
                String.format("-- Cores used in determining schedule: %o\n", clp.getNumThreads()) +
                "-- visualisation " + (clp.isVisual() ? "on" : "off") +
                "\n";

        System.out.println(runInformation);

        // use clp here to make choices about what parts to execute.
        IOHandler ioHandler = new AStarIOHandler(
                clp.getInputFileName(),
                clp.getOutputFileName(),
                clp.getGraphName(),
                clp.getNumProcessors()
        );

        Schedule originalSchedule = ioHandler.readFile();
        HeuristicManager heuristicManager = new AStarHeuristicManager(ioHandler.getSumWeights(), clp.getNumProcessors());
        SchedulingAlgorithm algorithm;

        //Sequential or Parallel
        if (clp.getNumThreads() > 1) {
            algorithm = new ParallelAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    clp.getNumThreads()
            );
        } else {
            algorithm = new SequentialAStarSchedulingAlgorithm(
                    originalSchedule,
                    heuristicManager,
                    new HashMap<>(),
                    new PriorityQueue<>()
            );
        }

        Function<List<int[]>, Void> success =
                optimalSchedule -> {
                    String result = ioHandler.writeFile(optimalSchedule);
                    System.out.println(result);
                    System.out.println("Successfully created " + clp.getOutputFileName() + '\n');
                    return null;
                };

        Function<Void, Void> failure = o -> {
            System.err.println("Worker error");
            return null;
        };

        if (clp.isVisual()) {
            AlgorithmService algorithmRunner = new AlgorithmService();
            algorithmRunner.setAlgorithm(algorithm);
            algorithmRunner.setOnSucceeded(t -> {
                if (algorithmRunner.getValue() != null) {
                    success.apply(algorithmRunner.getValue());
                } else {
                    failure.apply(null);
                }
            });
            String graphStyle = "graph { fill-color: #86aff0; }" +
                    "node { size: 15px; text-color: white; text-size: 12px; }" +
                    "node:clicked {fill-color: red;}" +
                    "edge { text-mode: hidden; text-size: 12px; text-alignment: along; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: black; text-padding: 2px, 1px; text-offset: 0px, 2px; }";

            Graph inputGraph = ((AStarIOHandler) ioHandler).getGraph();
            inputGraph.setAttribute("ui.stylesheet", graphStyle);
            for (Node n : inputGraph) {
                n.setAttribute("ui.label", n.getId());
            }

            inputGraph.edges().forEach(e -> e.setAttribute("ui.label", e.getAttribute("Weight")));

            AlgorithmDataCache dataCache = new AlgorithmDataCache(algorithm);

            GuiMain.setupGui(clp.getNumProcessors(), clp.getNumProcessors(), inputGraph,
                    dataCache, ((AStarIOHandler) ioHandler).getTaskName(), algorithmRunner);

            GuiMain.main(args);
        } else {
            List<int[]> optimalSchedule = algorithm.generateSchedule();
            if (optimalSchedule != null) {
                success.apply(optimalSchedule);
                System.exit(0);
            } else {
                failure.apply(null);
                System.exit(1);
            }
        }
    }
}
