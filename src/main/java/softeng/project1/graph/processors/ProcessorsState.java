package softeng.project1.graph.processors;

import softeng.project1.graph.processors.processor.Processor;

/**
 * ProcessorsState implements the Processors interface and is used to keep track of all the
 * different Processor objects.
 */
public class ProcessorsState implements Processors {

    private final Processor[] processors;

    /**
     * ProcessorsState contructor which gets the Processor objects passed in as an array.
     * @param initialProcessors
     */
    protected ProcessorsState(Processor[] initialProcessors) {
        processors = initialProcessors;
    }

    @Override
    public Processor getProcessor(int processorID) {
        return processors[processorID];
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
        // could replace this with hash32 as hash64 is deprecated
        return MurmurHash3.hash64(byteArrayForHash);

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
