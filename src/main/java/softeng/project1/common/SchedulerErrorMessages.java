package softeng.project1.common;

public enum SchedulerErrorMessages {
    CLINoArgs("Error: No arguments detected, please provide the file path for the input graph and an integer > 0 representing the number of available processors"),
    CLINoProcessorArg("Error: Missing Input, please provide the file path for the input graph and an integer > 0 representing the number of available processors"),
    CLITooManyArgs("Error: Too many arguments provided, please only provide the file path for the input graph and an integer > 0 that represents the number of available processors"),
    CLINotEnoughProcessors("Error: Number of processors must be greater than 0");


    private final String message;

    SchedulerErrorMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
