package softeng.project1.graph.processors.processor;

import softeng.project1.graph.TaskNode;
import static softeng.project1.graph.processors.processor.ProcessorHelper.fillProcessorSpace;

public class OriginalProcessorState implements Processor {

    private final int processorID;

    public OriginalProcessorState(int processorID) {
        this.processorID = processorID;
    }

    @Override
    public int getID() {
        return this.processorID;
    }

    @Override
    public int getNumSpaces() {
        return 1; // Always one space which represents the empty space at the end of the processor
    }

    @Override
    public Processor copyAndInsert(TaskNode taskNode) {
        
        int[][] newSpaceArray = new int[2][3];
        // First space exists before the inserted task
        fillProcessorSpace(newSpaceArray, 0,
            0,
            taskNode.getProcessorPrerequisite(this.processorID),
            taskNode.getTaskID()    
        );

        // Putting empty space on end to signal processor completion
        fillProcessorSpace(newSpaceArray, 1,
            taskNode.getProcessorPrerequisite(this.processorID) + taskNode.getTaskCost(),
            0,
            -1
        );
        return new ProcessorState(this.processorID, newSpaceArray);
    }

    @Override
    public boolean deepEquals(Processor otherProcessor) {
        try {
            // Can directly access field due to being the same object type
            return this.processorID == ((OriginalProcessorState) otherProcessor).processorID;
        } catch (ClassCastException e) {
            return false; // Processor State objects will never equal original processor states
        }
    } 

    @Override
    public byte[] asByteArray(int index, byte[] arrayToFill) {
        arrayToFill[index++] = 0; // start
        arrayToFill[index++] = 0;// length
        arrayToFill[index++] = -1; // next task ID
        return arrayToFill;
    }


}