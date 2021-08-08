package softeng.project1.graph.processors;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.OriginalProcessorState;
import softeng.project1.graph.processors.processor.Processor;

public class OriginalProcessorsState extends ProcessorsState {

    private static final int ORIGINAL_PROCESSORS_IDLE_TIME = 0;

    public OriginalProcessorsState(int numProcessors) {
        super(generateOriginalProcessors(numProcessors), ORIGINAL_PROCESSORS_IDLE_TIME);
    }

    private static Processor[] generateOriginalProcessors(int numProcessors) {
        Processor[] originalProcessors = new Processor[numProcessors];
        for (int i = 0; i<numProcessors; i++) {
            originalProcessors[i] = new OriginalProcessorState(i);
        }
        return originalProcessors;
    }


    @Override
    public long murmurHash() {

        // We know that every Processor in originalProcessors is actually an OriginalProcessor,
        // so only 3 bytes needed each
        byte[] byteArrayForHash = new byte[3*this.processors.length];

        for (int i = 0; i < this.processors.length; i++) {
            // assuming that i passes its value not its reference
            this.processors[i].asByteArray(i*3, byteArrayForHash);
        }
        // https://github.com/sangupta/murmur 
        return Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0); 
    }

    @Override
    public boolean deepEquals(Processors otherProcessors) {
        // OriginalProcessorsState is a singleton class and thus only equal to itself
        return this == otherProcessors;
    }


    @Override
    protected int calculateMaxProcessorLength(Processor newProcessor) {
        // No calculation needed
        return newProcessor.getLength();
    }

    @Override
    protected int calculateIdleTime(Processor newProcessor) {
        // No calculation needed
        return newProcessor.getChangeInIdleTime();
    }
}
