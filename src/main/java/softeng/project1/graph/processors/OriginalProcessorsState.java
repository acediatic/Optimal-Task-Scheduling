package softeng.project1.graph.processors;

import java.util.Arrays;

import com.sangupta.murmur.Murmur3;

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
    public Processors copyAndAddProcessor(Processor newProcessor) {
        // No need to recreate unchanged processor objects
        Processor[] newProcessors = Arrays.copyOf(originalProcessors, originalProcessors.length);
        newProcessors[newProcessor.getID()] = newProcessor;

        // This is the only location where ProcessorsState objects should be instantiated 
        // outside of ProcessorsState itself.
        return new ProcessorsState(newProcessors);
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
        // https://github.com/sangupta/murmur 
        return Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0); 
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
