package softeng.project1.graph.processors;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.OriginalProcessorState;
import softeng.project1.graph.processors.processor.Processor;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * Implementation of ProcessorsState that specifically represents the state of the Processors before any tasks have
 * been scheduled.
 * Due to it's nature, OriginalProcessorsState is effectively a singleton class, however it hasn't been made static
 * so that it can still fit into the general Processors class structure.
 */
public class OriginalProcessorsState extends ProcessorsState {

    private static final int ORIGINAL_PROCESSORS_IDLE_TIME = 0;

    /**
     * Public constructor for setting off the generation of new Processors. This constructor should be the only public
     * constructor for all Processor implementations. Other Processor objects should only be created via the
     * copyAndAddProcessor method.
     * Creating an OriginalProcessorsState only requires the input of a number of available processors. Generation of
     * the OriginalProcessorState objects is handled within the class.
     *
     * @param numProcessors : Number of processors available for tasks to be scheduled in.
     */
    public OriginalProcessorsState(int numProcessors) {
        super(generateOriginalProcessors(numProcessors), ORIGINAL_PROCESSORS_IDLE_TIME);
    }

    /**
     * Static helper method for generating OriginalProcessorState objects.
     *
     * @param numProcessors : Number of processors available for tasks to be scheduled in.
     * @return : A set of Processor objects representing the original state of each processor.
     */
    private static Processor[] generateOriginalProcessors(int numProcessors) {
        Processor[] originalProcessors = new Processor[numProcessors];
        for (short i = 0; i<numProcessors; i++) {
            // Using index as processor ID
            originalProcessors[i] = new OriginalProcessorState(i);
        }
        return originalProcessors;
    }

    /**
     * Generates a valid hash key int for use in a HashTable or HashMap.
     * Shorted method for speed using knowledge of OriginalProcessorNode implementation.
     * Overrides the base hashCode() Object method.
     *
     * @return : A valid hash key based off the Processor data.
     */
    @Override
    public int hashCode() {

        // We know that every Processor in originalProcessors is actually an OriginalProcessor,
        // so only 3 bytes needed each
        byte[] byteArrayForHash = new byte[3*this.processors.length];

        for (int i = 0; i < this.processors.length; i++) {
            // assuming that i passes its value not its reference
            this.processors[i].asByteArray(i*3, byteArrayForHash);
        }
        // https://github.com/sangupta/murmur
        // TODO... Find better method than casting to int
        return (int) Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0);
    }

    /**
     * Deep equals method which ensures that equality is based off of
     * Processor data/location instead of Object IDs.
     * Overrides the base hashCode() Object method.
     * Shorted method using the knowledge that OriginalProcessorState is effectively a singleton class.
     *
     * @param otherObject : The other Processor set being equated to this one.
     * @return : Whether or not each Processors object store the same values in the same order.
     */
    @Override
    public boolean equals(Object otherObject) {
        // OriginalProcessorsState is an effectively singleton class and thus only equal to itself
        return this == otherObject;
    }

    /**
     * Shorted implementation of the helper method from ProcessorsState.
     * All OriginalProcessors are length zero so the max length will always be the length of the new processor.
     *
     * @param newProcessor : The changed Processor.
     * @return : The length of the longest Processor.
     */
    @Override
    protected int calculateMaxProcessorLength(Processor newProcessor) {
        // No calculation needed
        return newProcessor.getLength();
    }

    /**
     * Shorted implementation of the helper method from ProcessorsState.
     * NewProcessor will always have a changeInIdleTime of 0 as tasks are only ever inserted right at the start.
     * This means the only change in idle time will be the increase in max processor length affecting the idle time
     * of all other processors.
     * @param newProcessor : The changed Processor.
     * @return : The sum total idle time of the new ProcessorsState object.
     */
    @Override
    protected int calculateIdleTime(Processor newProcessor) {
        return newProcessor.getLength()*(this.processors.length - 1);
    }
}
