package softeng.project1.graph.processors.processor;

import java.util.Arrays;

import softeng.project1.graph.TaskNode;
import static softeng.project1.graph.processors.processor.ProcessorHelper.fillProcessorSpace;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 * 
 * Implementation of Processor that stores Space data in a two dimensional array.
 * As expected of a Processor object ProcessorState should be immutable so care should
 * be taken not to expose the wrapped array.
 * 
 * ProcessorState objects should never be instantiated directly, only via calls to the
 * Processor method copyAndInsert(). 
 * 
 * As ProcessorState methods sit in the heart of the A* algorithm, they should attempt
 * to be as fast as possible. 
 */
public class ProcessorState implements Processor {

    // Immutable fields describing the state of the specific processor
    private final int processorID;
    private final int[][] processorSpaces;

    /**
     * Protected constructor for the generation of new ProcessorState objects.
     * Due to Java's implemention of 'protected', ProcessorState should be placed in 
     * its own package with only the other Processor implementations.
     * 
     * All logic regarding the insertion of new spaces into the array is handled before
     * this constructor which simply takes the finished product.
     * 
     * @param processorID : The processor that the object represents a state of.
     * @param processorSpaces : The current set of spaces between tasks assigned to the processor.
     */
    protected ProcessorState(int processorID, int[][] processorSpaces) {
        this.processorID = processorID;
        this.processorSpaces = processorSpaces;
    }

    /**
     * Returns the ID of the processor that this object represents a state of.
     * @return : Processor ID.
     */
    @Override
    public int getID() {
        return this.processorID;
    }

        /**
     * The number of spaces between tasks in the current set of tasks queued 
     * on the processor. Spaces can be 0 length, and one space always exists 
     * at the end of the queue/schedule. Thus the number of tasks will always
     * be one less than the number of spaces.
     * 
     * @return : Number or spaces between scheduled tasks. Minimum of 1 and 
     *           maximum of n + 1 where n is the total number of tasks.
     */
    @Override
    public int getNumSpaces() {
        return processorSpaces.length;
    }

    /**
     * Generates a Processor object representing the state of the specific 
     * processor (represented by the called Processor) after a new Task 
     * has been greedily scheduled into the earliest possible space.
     * 
     * @param taskNode : A free task to insert into the processor schedule.
     * @return : A new Processor object representing the processor after the
     *           insert.
     */
    @Override
    public Processor copyAndInsert(TaskNode newTaskNode) {

        // One space is always added so we increase initial array size by 1
        int[][] newProcessorSpaces = new int[this.processorSpaces.length + 1][3];

        // Returning new ProcessorState with inserted spaces
        return new ProcessorState(
                this.processorID,
                copyAndInsertSpace(
                    newProcessorSpaces,
                    newTaskNode.getTaskID(),
                    newTaskNode.getTaskCost(),
                    newTaskNode.getProcessorPrerequisite(this.processorID)
                )
        );

    }

    /**
     * Helper method for implementing the logic behind the interface method copyAndInsert().
     * 
     * Iterates through the current array of spaces until either a space is found that fits the 
     * given task or the array ends.
     * Uses System.arraycopy() to quickly copy data across from the current space array to the new one.
     *
     * This method is technically O(n) due to the list iteration.
     * 
     * @param newProcessorSpaces : A correctly sized array that the copied/inserted spaces will be added to.
     * @param taskID : The ID of the task that we're recording the addition into the processor of.
     * @param taskLength : The length/cost of said task
     * @param prerequisite : The earliest position within this specific processor where all data needed by
     *                       the task is available.
     * @return : A filled two dimensional array recording the location of every task allocated to the 
     *           processor at this specific state. Data is stored as spaces which are described in the class doc.
     */
    private int[][] copyAndInsertSpace(int[][] newProcessorSpaces, int taskID, int taskLength, int prerequisite) {

        // temp variables for attempted inserts
        int diffNodeTaskLength; // The difference between the length of the space and the length of the task
        int diffPrereqStartPosition; // The difference between the task's prerequisite position and the start location of the space
        int diffDiffNTPS; // The amount of extra space available after you factor in the task length and any communication delay.

        // ----[TASK A]----[TASK B]--|-------------[TASK C]
        //                   Space ^^^^^^^^^^^^^^^^

        // ----[TASK A]----[TASK B]--|-------------[TASK C]
        //     prerequisite position ^

        // ----[TASK A]----[TASK B]--|[NEW TASK]---[TASK C]
        //  diffPrereqStartPosition^^

        // ----[TASK A]----[TASK B]--|[NEW TASK]---[TASK C]
        //                          diffDiffNTPS^^^

        // skips final space in array as final node is handled below
        for (int i = 0; i < this.processorSpaces.length - 1; i++) {

            // See above
            diffPrereqStartPosition = prerequisite - this.processorSpaces[i][0];
            diffNodeTaskLength = this.processorSpaces[i][1] - taskLength;
            diffDiffNTPS = diffNodeTaskLength - diffPrereqStartPosition;

            // has the prereq been passed, and does the task fit in the space?
            if (diffPrereqStartPosition <= 0 && diffNodeTaskLength >= 0) {
                
                // We've found the insert position so copy the first half of array across
                System.arraycopy(this.processorSpaces, 0, newProcessorSpaces, 0, i-1);
                
                // prereq was passed so new 0 length space starts at beginning of now-filled old space
                fillProcessorSpace(newProcessorSpaces, i,
                    this.processorSpaces[i][0], // start
                    0,                          // length
                    taskID                      // next task ID
                );

                // adding new space after inserted task
                fillProcessorSpace(newProcessorSpaces, i+1,
                    this.processorSpaces[i][0] + taskLength,    // start
                    diffNodeTaskLength,                         // length 
                    this.processorSpaces[i][2]                  // next task ID
                );

                // copying rest of array
                i++;
                System.arraycopy(
                    this.processorSpaces,           // source
                    i,                              // source start
                    newProcessorSpaces,             // destination
                    i+1,                            // destination start - one after source because of added spot in middle
                    this.processorSpaces.length - i // length of copied section
                );;
                return newProcessorSpaces;

                // is the node long enough to compensate for the prerequisite position being after the space start? 
            } else if (diffDiffNTPS >= 0) {

                // We've found the insert position so copy the first half of array across
                System.arraycopy(this.processorSpaces, 0, newProcessorSpaces, 0, i-1);
                
                // task fits even though prereq is after space start so we add new space before inserted task
                fillProcessorSpace(newProcessorSpaces, i,
                    this.processorSpaces[i][0], // start
                    diffPrereqStartPosition,    // length
                    taskID                      // next task ID
                );

                // adding new space after inserted task
                fillProcessorSpace(newProcessorSpaces, i,
                    this.processorSpaces[i][0] + diffPrereqStartPosition + taskLength,  // start
                    diffDiffNTPS,                                                       // length
                    this.processorSpaces[i][2]                                          // next task ID
                ); 
                // copying rest of array
                i++;
                System.arraycopy(
                    this.processorSpaces,           // source
                    i,                              // source start
                    newProcessorSpaces,             // destination
                    i+1,                            // destination start - one after source because of added spot in middle
                    this.processorSpaces.length - i // length of copied section
                );
                return newProcessorSpaces;
            } 
        }

        // We've reached the end of the array without returning, so we're just going bang the space right on the end.

        // Getting where to put it
        int lastSpaceIndex = this.processorSpaces.length;
        int insertPoint = Math.max(prerequisite, this.processorSpaces[lastSpaceIndex][0]);

        // updating old final space to now stretch to insertPoint
        fillProcessorSpace(newProcessorSpaces, lastSpaceIndex,
            processorSpaces[lastSpaceIndex][0],                 // start
            insertPoint - processorSpaces[lastSpaceIndex][0],   // length
            taskID                                              // nextTaskID
        );
        
        // adding new final space
        lastSpaceIndex++;
        fillProcessorSpace(newProcessorSpaces, lastSpaceIndex,
            insertPoint + taskLength,   // start
            0,                          // length 0 as at end of processor schedule
            -1                          // -1 task ID ensures that hash conflicts don't occur when 
                                        // starts of other processors mimic end of this processor
        );

        // returning completed array
        return newProcessorSpaces;
        
    }


    /**
    * Implementation of deepEquals from Processor, using Arrays.deepEquals.
    * Note that:
    * - deepEquals returns FALSE if it is given a Processor implementation that isn't ProcessorState, regardless of stored data.
    * - deepEquals returns FALSE if it is given a Processor with a different ID to itself, regardless of stored data.
    *
    * @param otherProcessor : The other processor being equated to this one.
    * @return : Whether or not the data stored in the given processor is the same as is stored in this one, with the caveats mentioned above.
    */
    @Override
    public boolean deepEquals(Processor otherProcessor) {
        // Not currently handling null values for speed reasons
        try {
            // DeepEquals should handle the fact that we're comparing two dimensional arrays
            ProcessorState otherProcessorState = (ProcessorState) otherProcessor;
            return 
                Arrays.deepEquals(otherProcessorState.processorSpaces,this.processorSpaces) 
                && 
                this.processorID == otherProcessorState.processorID;
        } catch (ClassCastException e) {
            return false; // This is allowed because ProcessorStates will never directly equal OriginalProcessors
        }
    }

    /**
     * Implementation of asByteArray from Processor.
     * Iterates over every stored integer in the 2D array and inserts them into the byte array.
     * Hashing collisions may occur if the start locations of some spaces grow larger then 255.
     * 
     * @param index : The location in the array where the values should be inserted.
     * @param arrayToFill : The array to store the values in.
     * @return : The given array with all data from the ProcessorState added.
     */
    @Override
    public byte[] asByteArray(int index, byte[] arrayToFill) {
        
        // Not currently checking if will fit 
        for (int[] spaceValue: this.processorSpaces) {
            for (int value: spaceValue) {
                // casting to byte will have unintended consequences if value > 255, should be fine...
                arrayToFill[index] = (byte) value;
                index++;
            }
        }
        return arrayToFill;
    }

}
