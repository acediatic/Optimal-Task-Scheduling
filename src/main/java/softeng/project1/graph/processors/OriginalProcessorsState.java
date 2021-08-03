package softeng.project1.graph.processors;

import org.apache.commons.codec.digest.MurmurHash3;

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

        // We know that every Processor in originalProcessors is actually an OriginalProcessor,
        // so only 3 bytes needed each
        byte[] byteArrayForHash = new byte[3*originalProcessors.length];

        for (int i = 0; i < originalProcessors.length; i++) {
            // assuming that i passes its value not its reference
            originalProcessors[i].asByteArray(i*3, byteArrayForHash);
        }
        // could replace with hash32 as hash64 is deprecated
        return MurmurHash3.hash64(byteArrayForHash); 

    }

    @Override
    public boolean deepEquals(Processors otherProcessors) {

        try {
            // Directly accessing field as we're checking an object of the same type
            return this.originalProcessors.length == ((OriginalProcessorsState) otherProcessors).originalProcessors.length;
        } catch (ClassCastException e) {
            return false; // OriginalProcessorsState will never equal ProcessorsState 
        }

    }


    
}
