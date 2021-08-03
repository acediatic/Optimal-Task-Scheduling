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
    ProcessorsState(Processor[] initialProcessors) {
        processors = initialProcessors;
    }

    @Override
    public Processor getProcessor(int processorID) {
        return processors[processorID];
    }

    @Override
    public long murmurHash() {
        return 0; // default dummy code
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
