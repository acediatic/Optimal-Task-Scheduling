package softeng.project1.graph.processors;

import softeng.project1.graph.processors.processor.Processor;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 * 
 * The Processors interface keeps track of the 'state' of each 
 * schedule by keeping track of each of its processors.
 * By wrapping the Processor objects like this it is easier to reuse
 * unchanged Processor objects between states and to ensure the 
 * immutability of the objects isn't compromised.
 * Implementations must provide the functionality required to create
 * new Processors objects given a changed Processor, and to act as a
 * key for a HashTable/HashMap.
 * @see Processor
 */
public interface Processors {

    /**
     * Accesses the implementation's storage to retrieve a specific 
     * stored Processor. Implementations are expected to utilise
     * the Processor ID as a key in some way to speed up accesses.
     * 
     * @param processorID : The unique integer ID of the processor.
     * @return : The Processor object with the specific ID that 
     *           represents the current state of the processor in
     *           it's context.
     */
    Processor getProcessor(int processorID);

    /**
     * TODO...
     */
    int getNumProcessors();

    /**
     * Creates a new Processors object which is functionally a 
     * copy of the current except for one Processor which is
     * overwritten by the given Processor. Processor objects
     * overwrite the earlier version with the same ID. 
     * 
     * @param processor : The new Processor to place into the copied
     *                   Processors object.
     * @return : Copy of current Processors with one Processor replaced.
     */
    Processors copyAndAddProcessor(Processor processor);

    /**
     * Generates a valid hash key long for use in a HashTable or HashMap
     * based off of the state of each stored Processor. 
     * Implementations are expected to use the MurmurHash3 algorithm.
     * 
     * @return : A valid hash key based off the Processor data.
     */
    long murmurHash();

    /**
     * Deep equals method which ensures that equality is based off of 
     * Processor data/location instead of Object IDs.
     * 
     * @param otherProcessors : The other Processor set being equated to
     *                          the current one.
     * @return : Whether or not each Processors object store the same values
     *           in the same order.
     */
    boolean deepEquals(Processors otherProcessors);

}
