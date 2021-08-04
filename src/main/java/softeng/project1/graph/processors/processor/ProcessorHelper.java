package softeng.project1.graph.processors.processor;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 * Helper class, exists to try reduce duplicate code.
 */
class ProcessorHelper {

    /** 
     * Static helper method that correctly inserts data into the 'Space'
     * array format. 
     * 
     * @param newProcessorSpaces : Array to insert data into.
     * @param index : Index in array to start inserting data into.
     * @param start : The value of the space's start.
     * @param length : The length of the space.
     * @param taskID : The ID of the task that comes after this space,
     *                 or -1 if this is the final space.
     */
    static void fillProcessorSpace(int[][] newProcessorSpaces, int index, int start, int length, int taskID) {
        newProcessorSpaces[index][0] = start;
        newProcessorSpaces[index][1] = length;
        newProcessorSpaces[index][2] = taskID;
    }


}