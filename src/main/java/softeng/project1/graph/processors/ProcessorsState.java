package softeng.project1.graph.processors;

import java.util.Arrays;

import com.sangupta.murmur.Murmur3;

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
    public ProcessorsState copyAndAddProcessor(Processor newProcessor) {
        // No need to recreate processor objects which haven't changed because they're immutable.
        Processor[] newProcessors = Arrays.copyOf(processors, processors.length);
        newProcessors[newProcessor.getID()] = newProcessor;
        return new ProcessorsState(newProcessors);
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
