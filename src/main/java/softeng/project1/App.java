package softeng.project1;

import softeng.project1.algorithms.ListSchedulingAlgorithm;
import softeng.project1.converters.IOHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Runs the scheduling program
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        CommandLineProcessor clp = new CommandLineProcessor(args);

        String runInformation = "***** Outsourced to Pakistan - Scheduling Algorithm*****\n" +
                String.format("- Creating schedule for input graph from file: %s\n", clp.getInputFileName()) +
                String.format("- Number of Processors Available for Tasks: %o\n", clp.getNumProcessors()) +
                String.format("- Storing output in file: %s\n\n", clp.getOutputFileName()) +
                String.format("-- Cores used in determining schedule: %o\n", clp.getNumThreads()) +
                "-- visualisation " + (clp.isVisual() ? "on" : "off");

        System.out.println(runInformation);

        // use clp here to make choices about what parts to execute.

        ListSchedulingAlgorithm listScheduler = new ListSchedulingAlgorithm(IOHandler.readFile(clp.getInputFileName()));

        List<int[]> schedule = listScheduler.generateSchedule();

        IOHandler.writeFile(listScheduler.scheduleToGraph(schedule), clp.getOutputFileName());

    }
}
