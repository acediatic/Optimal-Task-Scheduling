package softeng.project1;

import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class CommandLineProcessor {
    private final String[] args;
    private CommandLine cmd;
    private int numProcessors;
    private int numThreads;
    private String inputFileName;
    private String outputFileName;
    private boolean isVisual;

    public CommandLineProcessor(String[] args) {
        this.args = args;

        // attempt to parse arguments
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(getCLOptions(), args);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments.");
            System.err.println("Please ensure correct command line formatting");
            System.exit(1);
        }

        initInputFilename();

        initNumProcessors();

        initOutputFilename();

        initIsVisual();

        initNumThreads();
    }

    /**
     * Determines the number of threads to be used [default 1].
     */
    private void initNumThreads() {
        if (cmd.hasOption('p')) {
            try {
                numThreads = Integer.parseInt(cmd.getOptionValue('p'));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing optional value number of cores");
                System.err.println("Please ensure correct command line formatting");
                System.exit(1);
            }
        } else {
            numThreads = 1;
        }
    }

    /**
     * Determines whether visual mode is to be used.
     */
    private void initIsVisual() {
        isVisual = cmd.hasOption('v');
    }

    /**
     * Determines output filename. Default is [<inputname>-output.dot]
     */
    private void initOutputFilename() {
        if (cmd.hasOption('o')) {
            outputFileName = cmd.getOptionValue('o');
            if (!outputFileName.endsWith(".dot")) {
                outputFileName += ".dot";
            }
        } else {
            // use default.
            outputFileName = inputFileName.substring(0, inputFileName.length() - 4) + "-output" + inputFileName.substring(inputFileName.length() - 4);
        }

        try {
            Paths.get(outputFileName);

        } catch (InvalidPathException ex) {
            System.err.println("Output file name is invalid");
            System.exit(1);
        }
    }

    /**
     * Determines number of processors available for tasks to be scheduled on.
     */
    private void initNumProcessors() {
        try {
            // number of processors is second positional argument
            numProcessors = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number of processors supplied");
            // exit with error.
            System.exit(1);
        }
    }

    /**
     * Determines the input file name, and checks the file exists.
     */
    private String initInputFilename() {
        // file name is first positional argument
        inputFileName = args[0];

        // check file can be found
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            System.err.println("Error: cannot find input file");
            System.exit(1);
        }
        return inputFileName;
    }


    /**
     * @return Returns Options object containing optional command line arguments.
     */
    private Options getCLOptions() {
        Options options = new Options();
        options.addOption(Option.builder("p")
                .hasArg(true)
                .desc("use specified cores for execution in parallel (default is sequential)")
                .required(false)
                .build());
        options.addOption(Option.builder("v")
                .hasArg(false)
                .desc("visualise the search")
                .required(false)
                .build());
        options.addOption(Option.builder("o")
                .hasArg(true)
                .desc("uses specified name as the name of the output file (default is <inputName>-output.dot)")
                .build());

        return options;
    }

    public int getNumProcessors() {
        return numProcessors;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public boolean isVisual() {
        return isVisual;
    }

    /**
     * @return numThreads the number of threads/cores to be used.
     */
    public int getNumThreads() {
        return numThreads;
    }
}
