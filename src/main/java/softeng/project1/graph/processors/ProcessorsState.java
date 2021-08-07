package softeng.project1.graph.processors;

import java.util.Arrays;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.Processor;
import softeng.project1.graph.tasks.TaskNode;

/**
 * ProcessorsState implements the Processors interface and is used to keep track of all the
 * different Processor objects.
 */
public class ProcessorsState implements Processors {

    private final Processor[] processors;
    private final int maxProcessorLength;
    private final int idleTime;

    /**
     * ProcessorsState contructor which gets the Processor objects passed in as an array.
     * @param initialProcessors
     */
    protected ProcessorsState(Processor[] initialProcessors, int maxProcessorLength, int idleTime) {
        processors = initialProcessors;
        this.maxProcessorLength = maxProcessorLength;
        this.idleTime = idleTime;
    }

    @Override
    public Processor getProcessor(int processorID) {
        return processors[processorID];
    }

    @Override
    public int getNumProcessors() {
        return processors.length;
    }

    @Override
    public int getIdleTime() {
        return idleTime;
    }

    @Override
    public ProcessorsState copyAndAddProcessor(TaskNode newNode, int processorID) {

        Processor[] newProcessors = Arrays.copyOf(processors, processors.length);
        Processor newProcessor = this.processors[processorID].copyAndInsert(newNode);
        newProcessors[processorID] = newProcessor;
        int newIdleTime;
        int newMaxProcessorLength;

        int lengthDiff = newProcessor.getLength() - this.maxProcessorLength;
        if (lengthDiff > 0) {
            newIdleTime =
                    this.idleTime + // Old idle time
                    newProcessor.getChangeInIdleTime() + // Idle time increase from changed processor
                    ((this.processors.length - 1) * lengthDiff); // Idle time increase from all other processors
            newMaxProcessorLength = newProcessor.getLength();
        } else {
            newIdleTime =
                    this.idleTime + // Old idle time
                    newProcessor.getChangeInIdleTime(); // Decrease in idle time from inserted task
            newMaxProcessorLength = this.maxProcessorLength;
        }
        return new ProcessorsState(newProcessors, newMaxProcessorLength, newIdleTime);
    }


    @Override
    public long murmurHash() {
        
        int numBytesNeeded = 0;

        for (Processor processor: processors) {
            // we multiply by 3 because each space has 3 values
            numBytesNeeded = numBytesNeeded + processor.getNumSpaces()*3;
        }
        // Kind of dirty to do this with two loops
        byte[] byteArrayForHash = new byte[numBytesNeeded];
        int index = 0;
        for (Processor processor: processors) {
            processor.asByteArray(index, byteArrayForHash);
            index = index + processor.getNumSpaces()*3;
        }
        // https://github.com/sangupta/murmur
        return Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0);

    }

    @Override
    public boolean deepEquals(Processors otherProcessors) {
        for (int i = 0; i < processors.length; i++) {
            if (!otherProcessors.getProcessor(i).deepEquals(processors[i])) {
                return false;
            }
        }
        return true;
    }
}
