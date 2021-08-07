package softeng.project1.graph.processors;

import java.util.Arrays;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.OriginalProcessorState;
import softeng.project1.graph.processors.processor.Processor;
import softeng.project1.graph.tasks.TaskNode;

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
    public int getNumProcessors() {
        return originalProcessors.length;
    }

    @Override
    public int getIdleTime() {
        return 0; // Always 0 for original
    }


    @Override
    public Processors copyAndAddProcessor(TaskNode newNode, int processorID) {
        // No need to recreate unchanged processor objects
        Processor[] newProcessors = Arrays.copyOf(originalProcessors, originalProcessors.length);
        Processor newProcessor = this.originalProcessors[processorID].copyAndInsert(newNode);
        newProcessors[processorID] = newProcessor;

        // We don't bother checking that getLength() and getChangeInIdleTime() are larger than stored because stored
        // is always 0
        return new ProcessorsState(newProcessors, newProcessor.getLength(), newProcessor.getChangeInIdleTime());
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
