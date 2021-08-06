package softeng.project1.graph.processors.processor;

import softeng.project1.graph.tasks.TaskNode;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 * 
 * An ordered collection of task locations that represents the state of a 
 * specific processor within a partial schedule. 
 * Processor implementations must be immutable as each Processor is expected
 * to be reference by multiple schedules.
 * Note that Processor objects are NOT UNIQUE, they can be reused but no 
 * checks are currently implemented to enforce this. 
 * 
 * The Processor interface expects the IDs returned to be, zero-indexed 
 * and no larger than the total number of processors. Note that IDs are 
 * not unique, multiple Processor objects with the same ID represent 
 * multiple states of a specific processor. Equality checking should be
 * handled by the deepEquals method instead.
 * 
 * NumSpaces refers to the number of spaces between tasks in the processor.
 * spaces are described by their start location, length, and the ID of the task
 * directly AFTER them. Every Processor is expected to contain a final space
 * that indicates the end of the list. This space is differentatied by 
 * having a next task ID of -1.
 * 
 * Instantiating new Processor objects should be handled with care due to the 
 * immutability requirement described above. Once one is created, more can
 * be generated with the copyAndInsert method. Directly accessing the storage
 * medium wrapped by the Processor is not recommended.
 * 
 * The Processor interface provides a method for using its data to generate
 * Hash Keys. This method should be used only for this purpose and not for
 * equality checking or new Processor generation.
 */
public interface Processor {

    /**
     * Returns the ID of the processor that this object represents a state of.
     * @return : Processor ID.
     */
    int getID();


    /**
     * The number of spaces between tasks in the current set of tasks queued 
     * on the processor. Spaces can be 0 length, and one space always exists 
     * at the end of the queue/schedule. Thus the number of tasks will always
     * be one less than the number of spaces.
     * 
     * @return : Number or spaces between scheduled tasks. Minimum of 1 and 
     *           maximum of n + 1 where n is the total number of tasks.
     */
    int getNumSpaces();

    /**
     * Generates a Processor object representing the state of the specific 
     * processor (represented by the called Processor) after a new Task 
     * has been greedily scheduled into the earliest possible space.
     * 
     * @param taskNode : A free task to insert into the processor schedule.
     * @return : A new Processor object representing the processor after the
     *           insert.
     */
    Processor copyAndInsert(TaskNode taskNode);

    /**
     * Returns whether the values stored within a specified Processor are the
     * exact same as the values stored within the called Processor.
     *  - Processor objects with the same ID are NOT confirmed equal.
     *  - Processor objects with the same byte array are NOT confirmed equal.
     *  - Processor objects will be reused but detecting if reuse is possible
     *    is handled lazily so duplicates will occur.
     *  - CURRENTLY CALLING deepEquals ON DIFFERING IMPLEMENTATIONS OF PROCESSOR
     *    IS EXPECTED TO ALWAYS RETURN FALSE.
     *  
     * @param otherProcessor : The Processor state to check for equality.
     * @return : Whether or not the given Processor stores the same data as this one.
     */
    boolean deepEquals(Processor otherProcessor);

    /**
     * Returns the stored state data as a byte array for use in the generation of 
     * hash keys.
     * Casting ints > 255 to bytes may cause differing Processors to produce
     * similar byte arrays. 
     * 
     * @param index : The location in the given array to start inserting the byte data.
     * @param arrayToFill : The array in which the data for generating the hash key 
     *                      is being stored.
     * @return : The given array after it has had the Processor's data added to it.
     */
    byte[] asByteArray(int index, byte[] arrayToFill);
}
