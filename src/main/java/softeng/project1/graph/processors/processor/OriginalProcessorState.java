package softeng.project1.graph.processors.processor;

import softeng.project1.graph.TaskNode;

import java.util.Map;
import java.util.HashMap;

/**
 * Barebones implementation of the Processor interface to represent each Processor
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

    private static Map<Integer, OriginalProcessorState> ORIGINAL_PROCESSORS = new HashMap<>(); 

    // Immutable Processor ID. ID's should be unique within the 
    // OriginalProcessorState set.
    private final int processorID;

    /**
     * Main constructor / entry point for the entire Processor set.
     * This is the only public Processor constructor, all other Processor
     * implementations must be generated through the use of the 
     * copyAndInsert() method.
     * Original Processor States only require an ID to create but only one
     * should be generated for each unique ID. 
     * The constructor will throw an exception if it finds that it's a duplicate.
     * @param processorID
     */
    public OriginalProcessorState(int processorID) {
        // Ensuring that we're not generating unnecessary original states
        if (ORIGINAL_PROCESSORS.get(processorID) != null) {
            throw new RuntimeException(
                "Trying to create a Original Processor that's already been generated?\n" +
                "Processor ID: " + processorID
            );
        } else {
            this.processorID = processorID;
            ORIGINAL_PROCESSORS.put(processorID, this); // Does this fail because 'this' is null?
        }
    }
    /**
     * Returns the ID of the processor that this object represents the initial state of.
     * ID is NOT unique among Processor objects but IS unique among OriginalProcessorState
     * objects.
     */
    @Override
    public int getID() {
        return this.processorID;
    }

    /**
     * Fast implementation of the method from Processor.
     * The number of spaces in the original states is static, so no logic is needed.
     */
    @Override
    public int getNumSpaces() {
        return ORIGINAL_NUM_SPACES; // Always one space which represents the empty space at the end of the processor
    }

    /**
     * Fast implementation of Processor's method.
     * No calculation is needed to insert the first task in the schedule,
     * so many of the resulting space values are hard coded. 
     */
    @Override
    public Processor copyAndInsert(TaskNode taskNode) {
        
        int[][] newSpaceArray = new int[2][3];
        // First space exists before the inserted task
        ProcessorHelper.fillProcessorSpace(newSpaceArray, 0,
            0,
            taskNode.getProcessorPrerequisite(this.processorID),
            taskNode.getTaskID()    
        );

        // Putting empty space on end to signal processor completion
        ProcessorHelper.addFinalSpace(newSpaceArray,
            taskNode.getProcessorPrerequisite(this.processorID) + taskNode.getTaskCost()
        );

        return new ProcessorState(this.processorID, newSpaceArray);
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
     * @param : The Processor object that's being equated to this one.
     * @return: Whether or not the input stores the same values as this one. 
     */
    @Override
    public boolean deepEquals(Processor otherProcessor) {
        return this == otherProcessor; 
    } 

    /**
     * Simple implementation of the method described in Processor.
     * Uses System.arraycopy to quickly copy the hard coded data into the 
     * given byte array.
     */
    @Override
    public byte[] asByteArray(int index, byte[] arrayToFill) {
        System.arraycopy(ORIGINAL_BYTE_DATA, 0, arrayToFill, index, ORIGINAL_BYTE_DATA.length);
        return arrayToFill;
    }


}