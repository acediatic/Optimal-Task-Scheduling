package softeng.project1.graph.processors;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.Processor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Remus Courtenay
 * @version 1.0
 * @since 1.8
 *
 * Implementation of ProcessorsState that specifically represents the state of a set of Processor objects where at least
 * one has been changed.
 */
public class ChangedProcessorsState extends ProcessorsState {

    private final int maxProcessorLength;

    /**
     * Protected constructor for use only by the copyAndAddProcessor method from Processors.
     * Direct instantiations should be used on OriginalProcessorsState objects only.
     * @see OriginalProcessorsState
     *
     * @param processors : The set of Processor objects representing the current state of each processor.
     * @param maxProcessorLength : The length of the longest stored Processor.
     * @param idleTime : The sum total idle time of all stored Processor objects.
     *
     */
    protected ChangedProcessorsState(Processor[] processors, int maxProcessorLength, int idleTime) {
        super(processors, idleTime);
        this.maxProcessorLength = maxProcessorLength;
    }

    /**
     * Generates a valid hash key int for use in a HashTable or HashMap
     * based off of the state of each stored Processor.
     * Overrides the base hashCode() Object method.
     *
     * @return : A valid hash key based off the Processor data.
     */
    @Override
    public int hashCode() {

        int maxNumSpaces = 0;

        for (Processor processor: this.processors) {
            if (processor.getNumSpaces() > maxNumSpaces) {
                maxNumSpaces = processor.getNumSpaces();
            }
        }

        // Multiply by three because each space has three
        byte[] byteArrayForHash = new byte[maxNumSpaces*3];
        for (Processor processor: processors) {
            processor.addToByteArray(byteArrayForHash);
        }
        // https://github.com/sangupta/murmur
        // TODO... find nicer way to do this than int casting
        return (int) Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0);

    }

    /**
     * Deep equals method which ensures that equality is based off of
     * Processor data/location instead of Object IDs.
     * Overrides the base hashCode() Object method.
     *
     * @param otherObject : The other Processor set being equated to this one.
     * @return : Whether or not each Processors object store the same values in the same order.
     */
    @Override
    public boolean equals(Object otherObject) {
        ChangedProcessorsState otherProcessors;

        if (otherObject.getClass() != ChangedProcessorsState.class) {
            return false;
        } else {
            // Will never be equal to OriginalProcessorsState so can cast to ChangedProcessor instead of Processor
            otherProcessors = (ChangedProcessorsState) otherObject;
        }
        
        PriorityQueue<Processor> sortedProcessors = getSortedProcessors();
        PriorityQueue<Processor> otherSortedProcessors = otherProcessors.getSortedProcessors();

        for (int i = 0; i < processors.length; i++) {
            if (!sortedProcessors.poll().deepEquals(otherSortedProcessors.poll())) {
                return false;
            }
        }
        return true;
    }


     PriorityQueue<Processor> getSortedProcessors() {

        PriorityQueue<Processor> sortedProcessors = new PriorityQueue<>(this.processors.length, new Comparator<Processor>() {
            @Override
            public int compare(Processor o1, Processor o2) {
                return o1.getFirstTaskID() - o2.getFirstTaskID();
            }
        });

        sortedProcessors.addAll(Arrays.asList(this.processors));

        return sortedProcessors;


    }

    /**
     * Implementation of helper method from ProcessorsState.
     *
     * @param newProcessor : The changed Processor.
     * @return : The length of the longest Processor schedule.
     */
    @Override
    protected int calculateMaxProcessorLength(Processor newProcessor) {
        return Math.max(this.maxProcessorLength, newProcessor.getLength());
    }

    /**
     * Implementation of helper method from ProcessorsState.
     *
     * @param newProcessor : The changed Processor.
     * @return : The sum total idle time of all stored Processor objects.
     */
    @Override
    protected int calculateIdleTime(Processor newProcessor) {
        int lengthDiff = this.maxProcessorLength - newProcessor.getLength();
        // All processors idle time has to extend by any increase in max processor length.
        if (lengthDiff > 0) {
            return this.idleTime +
                    newProcessor.getChangeInIdleTime() +
                    ((this.processors.length - 1) * lengthDiff);
        } else {
            return this.idleTime + newProcessor.getChangeInIdleTime();
        }
    }
}
