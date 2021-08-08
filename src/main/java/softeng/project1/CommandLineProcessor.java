package softeng.project1;

import org.apache.commons.cli.*;

import java.io.File;

public class CommandLineProcessor {
    private CommandLine cmd;
    private int numProcessors;
    private int numThreads;
    private final String inputFileName;
    private final String outputFileName;
    private final boolean isVisual;

    public CommandLineProcessor(String[] args) {
        // file name is first positional argument
        inputFileName = args[0];

        // check file can be found
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            System.err.println("Error: cannot find input file");
            System.exit(1);
        }

        // number of processors is second positional argument
        try {
            numProcessors = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number of processors supplied");
            // exit with error.
            System.exit(1);
        }

        // attempt to parse arguments
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(getCLOptions(), args);
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments.");
            System.err.println("Please ensure correct command line formatting");
            System.exit(1);
        }

        // Determine output filename
        String defaultOutputName = inputFileName.substring(0, inputFileName.length() - 4) + "-output" + inputFileName.substring(inputFileName.length() - 4);
        outputFileName = cmd.getOptionValue('o', defaultOutputName);

        // Determine visual mode
        isVisual = cmd.hasOption('v');

        // Determine number of threads to use (default is 1).
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
