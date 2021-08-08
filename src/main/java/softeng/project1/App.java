package softeng.project1;

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

        String runInformation = "Outsourced to Pakistan - Scheduling Algorithm\n" +
                String.format("Creating schedule for input graph from file: %s\n", clp.getInputFileName()) +
                String.format("Storing output in file: %s\n\n", clp.getOutputFileName()) +
                "Using: \n" +
                String.format("Processors: %o", clp.getNumProcessors()) +
                String.format("Cores: %o", clp.getNumThreads()) +
                "visualisation " + (clp.isVisual() ? "on" : "off");

        System.out.println(runInformation);

        // use clp here to make choices about what parts to execute.
    }
}
