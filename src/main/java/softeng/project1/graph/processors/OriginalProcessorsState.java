package softeng.project1.graph.processors;

import softeng.project1.graph.processors.processor.OriginalProcessorState;
import softeng.project1.graph.processors.processor.Processor;

public class OriginalProcessorsState implements Processors {

    private final Processor[] originalProcessors;

    public OriginalProcessorsState(int numProcessors) {
        this.originalProcessors = new Processor[numProcessors];

        for (int i = 0; i < numProcessors; i++) {
            this.originalProcessors[i] = new OriginalProcessorState(i);
        }

    }

    @Override
    public Processor getProcessor(int processorID) {
        return originalProcessors[processorID];
    }

    @Override
    public long murmurHash() {
        return 0; // TODO...
    }

    @Override
    public boolean deepEquals(Processors otherProcessors) {

        try {
            // Directly accessing field as we're checking an object of the same type
            return this.originalProcessors.length == ((OriginalProcessorState) otherProcessors).originalProcessors.length;
        } catch (ClassCastException e) {
            return false; // OriginalProcessorsState will never equal ProcessorsState 
        }

    }


    
}
