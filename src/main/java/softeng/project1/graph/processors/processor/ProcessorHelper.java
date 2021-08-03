package softeng.project1.graph.processors.processor;

class ProcessorHelper {

    static void fillProcessorSpace(int[][] newProcessorSpaces, int index, int start, int length, int taskID) {
        newProcessorSpaces[index][0] = start;
        newProcessorSpaces[index][1] = length;
        newProcessorSpaces[index][2] = taskID;
    }


}