package softeng.project1.common;

/**
 * A utility class containing the commonly used error messages in the command line processor.
 * This cleans the code up, and allows changing of multiple messages in one place.
 */
public enum SchedulerErrorMessages {
    CLINoArgs("Error: No arguments detected, please provide the file path for the input graph and an integer > 0 representing the number of available processors"),
    CLINoProcessorArg("Error: Missing Input, please provide the file path for the input graph and an integer > 0 representing the number of available processors"),
    CLITooManyArgs("Error: Too many arguments provided, please only provide the file path for the input graph and an integer > 0 that represents the number of available processors"),
    CLINotEnoughProcessors("Error: Number of processors must be greater than 0"),
    CLICannotFindInputFile("Error: Unable to find file"),
    CLIInvalidProcessorNum("Error: Unable to parse inputted number of processors as an integer"),
    CLIFailedOptionParsing("Error: Failed to successfully parse optional arguments, please ensure you're following correct command line formatting"),
    CLIProperFormat("Attempting to parse arguments in the form [-Xmx4G] inputFileName numProcessors [-o outputFileName] [-p numberCores]");


    private final String message;

    SchedulerErrorMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    public String getMessageWithArg(String arg) {
        return message + " - " + arg;
    }
}
