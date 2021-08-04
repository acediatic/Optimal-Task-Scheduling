package softeng.project1.graph.processors.processor;

import java.util.Arrays;

import softeng.project1.graph.TaskNode;
import static softeng.project1.graph.processors.processor.ProcessorHelper.fillProcessorSpace;

/**
 *
 */
public class ProcessorState implements Processor {

    private final int processorID;
    private final int[][] processorSpaces;

    /**
     *
     * @param processorID
     * @param processorSpaces
     */
    protected ProcessorState(int processorID, int[][] processorSpaces) {
        this.processorID = processorID;
        this.processorSpaces = processorSpaces;
    }

    @Override
    public int getID() {
        return this.processorID;
    }

    @Override
    public int getNumSpaces() {
        return processorSpaces.length;
    }

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

    private int[][] copyAndInsertSpace(int[][] newProcessorSpaces, int taskID, int taskLength, int prerequisite) {

        int diffNodeTaskLength;
        int diffPrereqStartPosition;
        int diffDiffNTPS;

        // skips final space in array as final node is handled below
        for (int i = 0; i < this.processorSpaces.length - 1; i++) {

            diffPrereqStartPosition = prerequisite - this.processorSpaces[i][0];
            diffNodeTaskLength = this.processorSpaces[i][1] - taskLength;
            diffDiffNTPS = diffNodeTaskLength - diffPrereqStartPosition;

            // has the prereq been passed?
            if (diffPrereqStartPosition <= 0 && diffNodeTaskLength >= 0) {
                
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
                return copyRestOfSpaces(++i, newProcessorSpaces);

                // is the node long enough to compensate for the prerequisite position? 
            } else if (diffDiffNTPS >= 0) {
                
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
                return copyRestOfSpaces(++i, newProcessorSpaces);


                // node doesn't fit in here :( so we just copy old array and move onto next index
            } else {
                // weird stuff might be happening with pointers here because not using Arrays.copyOf, should be fine...
                newProcessorSpaces[i] = this.processorSpaces[i];
            }
        }

        // We've reached the end of the array without returning, so we're just going bang the space right on the end.

        // Getting where to put it
        // Don't have to add length because last node always length 0
        
        int lastSpaceIndex = this.processorSpaces.length;
        int insertPoint = Math.max(prerequisite, this.processorSpaces[this.processorSpaces.length][0]);

        // updating old final node to now stretch to insertPoint
        fillProcessorSpace(newProcessorSpaces, lastSpaceIndex,
            processorSpaces[lastSpaceIndex][0],                 // start
            insertPoint - processorSpaces[lastSpaceIndex][0],   // length
            taskID                                              // nextTaskID
        );
        
        // adding new final node
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

    private int[][] copyRestOfSpaces(int index, int[][] newProcessorSpaces) {

        for (int i = index; i < this.processorSpaces.length; i++) {
            // new array is one step ahead due to insertion
            // weird stuff might be happening with pointers here because not using Arrays.copyOf, should be fine...
            newProcessorSpaces[i+1] = this.processorSpaces[i]; 
        }
        return newProcessorSpaces;

    }

    @Override
    public boolean deepEquals(Processor otherProcessor) {
        
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

    @Override
    public byte[] asByteArray(int index, byte[] arrayToFill) {
        
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
