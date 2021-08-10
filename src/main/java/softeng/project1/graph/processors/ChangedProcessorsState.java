package softeng.project1.graph.processors;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.Processor;

/**
 * ProcessorsState implements the Processors interface and is used to keep track of all the
 * different Processor objects.
 */
public class ChangedProcessorsState extends ProcessorsState {

    private final int maxProcessorLength;

    /**
     * ProcessorsState contructor which gets the Processor objects passed in as an array.
     * @param processors
     */
    protected ChangedProcessorsState(Processor[] processors, int maxProcessorLength, int idleTime) {
        super(processors, idleTime);
        this.maxProcessorLength = maxProcessorLength;
    }

    @Override
    public int hashCode() {
        
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
        // TODO... find nicer way to do this than int casting
        return (int) Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0);

    }

    @Override
    public boolean equals(Object otherObject) {
        Processors otherProcessors;
        try {
            otherProcessors = (Processors) otherObject;
        } catch (ClassCastException e) {
            return false;
        }
        for (int i = 0; i < processors.length; i++) {
            if (!otherProcessors.getProcessor(i).deepEquals(processors[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected int calculateMaxProcessorLength(Processor newProcessor) {
        return Math.max(this.maxProcessorLength, newProcessor.getLength());
    }

    @Override
    protected int calculateIdleTime(Processor newProcessor) {
        int lengthDiff = this.maxProcessorLength - newProcessor.getLength();
        // All processors idle time has to extend by any increase in max processor length.
        if (lengthDiff > 0) {
            return this.idleTime +
                    newProcessor.getChangeInIdleTime() +
                    ((this.processors.length - 1) * lengthDiff);
        } else {
            return this.idleTime + newProcessor.getChangeInIdleTime();
        }
    }
}
