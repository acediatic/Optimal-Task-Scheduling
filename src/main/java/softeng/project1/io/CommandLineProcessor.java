package softeng.project1.io;

import org.apache.commons.cli.*;
import softeng.project1.common.SchedulerErrorMessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class CommandLineProcessor {
    private final String[] args;
    private CommandLine cmd;
    private short numProcessors;
    private int numThreads;
    private String inputFileName;
    private String outputFileName;
    private boolean isVisual;
    private String graphName;

    public CommandLineProcessor(String[] args) {
        this.args = args;

        // attempt to parse arguments
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(getCLOptions(), args);
        } catch (ParseException e) {
            System.err.println(SchedulerErrorMessages.CLIFailedOptionParsing);
            System.err.println(SchedulerErrorMessages.CLIProperFormat);
            System.exit(1);
        }

        switch (cmd.getArgList().size()) {
            case 0:
                System.err.println(SchedulerErrorMessages.CLINoArgs);
                System.err.println(SchedulerErrorMessages.CLIProperFormat);
                System.exit(2);
            case 1:
                System.err.println(SchedulerErrorMessages.CLINoProcessorArg);
                System.err.println(SchedulerErrorMessages.CLIProperFormat);
                System.exit(2);
            case 2:
            case 3:
                break; // Do nothing, correct number of arguments passed
            default:
                System.err.println(SchedulerErrorMessages.CLITooManyArgs);
                System.err.println(SchedulerErrorMessages.CLIProperFormat);
                System.exit(2);
        }

        initInputFilename();

        initNumProcessors();

        initOutputFilename();

        initGraphName();

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
                System.exit(3);
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
            File input = new File(inputFileName);
            String cleanInputName = input.getName();
            String outputNamePrefix = cleanInputName.substring(0, cleanInputName.length() - 4);

            outputFileName = outputNamePrefix + "-output.dot";
        }

        try {
            Paths.get(outputFileName);

        } catch (InvalidPathException ex) {
            System.err.println("Output file name is invalid");
            System.exit(4);
        }
    }

    private void initGraphName() {
        File input = new File(inputFileName);

        try {
            BufferedReader fr = new BufferedReader(new FileReader(input));
            String rawGraphLine = fr.readLine();
            graphName = rawGraphLine.substring(rawGraphLine.indexOf('"') + 1, rawGraphLine.lastIndexOf('"'));

        } catch (IOException e) { // TODO... print error message
            e.printStackTrace();
        }
    }


    /**
     * Determines number of processors available for tasks to be scheduled on.
     */
    private void initNumProcessors() {
        try {
            // number of processors is second positional argument
            numProcessors = Short.parseShort(args[cmd.hasOption("Xmx4G") ? 2 : 1]);

            if (numProcessors <= 0) {
                System.err.println(SchedulerErrorMessages.CLINotEnoughProcessors);
                System.exit(5);
            }
        } catch (NumberFormatException e) {
            System.err.println(SchedulerErrorMessages.CLIInvalidProcessorNum.getMessageWithArg(args[1]));
            // exit with error.
            System.exit(5);
        }
    }

    /**
     * Determines the input file name, and checks the file exists.
     */
    private String initInputFilename() {
        // file name is first positional argument, if there's no heap size specified.
        inputFileName = args[cmd.hasOption("Xmx4G") ? 1 : 0];

        // check file can be found
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            System.err.println(SchedulerErrorMessages.CLICannotFindInputFile.getMessageWithArg(inputFileName));
            System.exit(6);
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
        options.addOption(Option.builder("Xmx4G")
                .hasArg(false)
                .required(false)
                .build());

        return options;
    }

    public short getNumProcessors() {
        return numProcessors;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public String getGraphName() {
        return graphName;
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
