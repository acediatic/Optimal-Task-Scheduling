package softeng.project1.graph.processors.processor;

import softeng.project1.graph.tasks.TaskNode;

import java.util.Map;
import java.util.HashMap;

/**
 * Bare-bones implementation of the Processor interface to represent each Processor
 * before any tasks have been assigned to them.
 * Returns hard coded values for space data.
 * Most importantly, OriginalProcessorState offers a kickoff point for the creation
 * of new ProcessorState objects. In order to stay immutable, ProcessorState objects
 * should only ever be created from the copyAndInsert() method in Processor.
 * OriginalProcessorState objects may be instantiated normally.
 */
public class OriginalProcessorState implements Processor {

    // Static space data shared by all Original Processor States
    private static final int ORIGINAL_NUM_SPACES = 1;
    private static final byte[] ORIGINAL_BYTE_DATA = new byte[]{
        (byte) 0, // space start position
        (byte) 0, // space length 
        (byte) -1 // ID of next task
    };

    // Immutable Processor ID. ID's should be unique within the 
    // OriginalProcessorState set.
    private final short processorID;

    /**
     * Main constructor / entry point for the entire Processor set.
     * This is the only public Processor constructor, all other Processor
     * implementations must be generated through the use of the 
     * copyAndInsert() method.
     * @param processorID : ID of the processor this object represents the original state of.
     */
    public OriginalProcessorState(short processorID) {
        this.processorID = processorID;
    }

    /**
     * Fast implementation of Processor's method.
     * No calculation is needed to insert the first task in the schedule,
     * so many of the resulting space values are hard coded. 
     */
    @Override
    public Processor copyAndInsert(TaskNode taskNode) {
        int[][] newSpaceArray = new int[2][3];

        // Always insert as soon as data is available.
        int insertLocation = taskNode.getProcessorPrerequisite(this.processorID);
        
        // First space exists before the inserted task
        ProcessorHelper.fillProcessorSpace(newSpaceArray, 0,
            0,
                insertLocation,
                taskNode.getTaskID()
        );

        // Putting empty space on end to signal processor completion
        ProcessorHelper.addFinalSpace(newSpaceArray,
            taskNode.getProcessorPrerequisite(this.processorID) + taskNode.getTaskCost()
        );

        return new ProcessorState(this.processorID, newSpaceArray, insertLocation);
    }

    /**
     * Shorted version of the full deep equals which utilises known information about the
     * objects to speed up equality checking.
     * We can use == to check whether the input is the exact same object as this one.
     * (== equates the pointers)
     * This is safe because:
     * - Each OriginalProcessorState is unique (see constructor)
     * - Only OriginalProcessorState objects have specifically one space as
     *   all other implementations can only be instantiated via copyAndInsert().
     *   Thus they'll never be equal.
     * 
     * @param otherProcessor : The Processor object that's being equated to this one.
     * @return : Whether or not the input stores the same values as this one.
     */
    @Override
    public boolean deepEquals(Processor otherProcessor) {
        return this == otherProcessor; 
    }


    /**
     * Implementation of asByteArray from Processor.
     * Iterates over every stored integer in the 2D array and inserts them into the byte array.
     * Hashing collisions may occur if the start locations of some spaces grow larger then 255.
     *
     * @param arrayToFill : The array to store the values in.
     * @return : The given array with all data from the ProcessorState added.
     */
    @Override
    public byte[] addToByteArray(byte[] arrayToFill) {

        // Not currently checking if will fit
        for (int i = 0; i < ORIGINAL_NUM_SPACES; i++) {
            for (int value: ORIGINAL_BYTE_DATA) {
                // casting to byte will have unintended consequences if value > 255, should be fine...
                arrayToFill[i] = (byte)(arrayToFill[i] + value);
            }
        }
        return arrayToFill;
    }

    // --------- Getter Methods ---------

    /**
     * ID is NOT unique among Processor objects but IS unique among OriginalProcessorState
     * objects.
     * @return : Returns the ID of the processor that this object represents the initial state of.
     */
    @Override
    public int getID() {
        return this.processorID;
    }

    /**
     * Fast implementation of the method from Processor.
     * The number of spaces in the original states is static, so no logic is needed.
     * @return : The number of spaces between tasks currently in the processor plus one on the end
     */
    @Override
    public int getNumSpaces() {
        return ORIGINAL_NUM_SPACES; // Always one space which represents the empty space at the end of the processor
    }

    /**
     * Always returns -1 as no tasks have been inserted in the original state.
     * @return : The location of the start of the latest task to be scheduled in the processor.
     */
    @Override
    public int getLastInsertLocation() {
        return -1; // There is no first task ID for this, and hence gives the error value of -1.
    }

    /**
     * @return : The location in the processor's schedule when all tasks have been fully completed.
     */
    @Override
    public int getLength() {
        return 0; // Always 0 for original
    }

    /**
     * Always returns -1 as no tasks have been inserted in the original state.
     * @return : The location where the final scheduled task finishes.
     */
    @Override
    public int getChangeInIdleTime() {
        return -1; // There is no first task ID for this, and hence gives the error value of -1.
    }

    /**
     * @return : The unique ID of the task scheduled first in the processor, or -1 if no tasks have been scheduled.
     */
    @Override
    public int getFirstTaskID() {
        return -1; // There is no first task ID for this, and hence gives the error value of -1.
    }


}