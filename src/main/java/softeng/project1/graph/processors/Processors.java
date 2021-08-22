package softeng.project1.graph.processors;

import softeng.project1.graph.processors.processor.Processor;
import softeng.project1.graph.tasks.TaskNode;

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
     * @return : The number of processors available for tasks to be scheduled on.
     */
    int getNumProcessors();

    /**
     * Note that this implementation should consider all processors as finishing at the same point, not at each
     * individual latest task per processor.
     * @return : The sum total amount of time spent idling on each processor before the latest scheduled task (on any
     *           processor) is finished.
     */
    int getIdleTime();

    /**
     * Creates a new Processors object which is functionally a 
     * copy of the current except for one Processor which is
     * overwritten by the given Processor. Processor objects
     * overwrite the earlier version with the same ID. 
     *
     * @param newNode : The new task to be greedily inserted into a processor creating a new processor state.
     * @param processorID : The ID of the processor that the task is to be inserted into.
     * @return : Copy of current Processors with one Processor replaced by a freshly generated processor state created
     *           by inserting a given task.
     */
    Processors copyAndAddProcessor(TaskNode newNode, int processorID);

    /**
     * Generates a valid hash key int for use in a HashTable or HashMap
     * based off of the state of each stored Processor. 
     * Implementations are expected to use the MurmurHash3 algorithm.
     * Overrides the base hashCode() Object method.
     * 
     * @return : A valid hash key based off the Processor data.
     */
    @Override
    int hashCode();

    /**
     * Deep equals method which ensures that equality is based off of 
     * Processor data/location instead of Object IDs.
     * Overrides the base equals() Object method.
     * 
     * @param otherObject : The other Processor set being equated to this one.
     * @return : Whether or not each Processors object store the same values in the same order.
     */
    @Override
    boolean equals(Object otherObject);

}
