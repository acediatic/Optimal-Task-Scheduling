package softeng.project1.graph.processors;

import com.sangupta.murmur.Murmur3;

import softeng.project1.graph.processors.processor.Processor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        
        int numBytesNeeded = 0;

        Processor[] orderedProcessors = orderProcessorsByFirstTaskNum();

        for (Processor processor: orderedProcessors) {
            // we multiply by 3 because each space has 3 values
            numBytesNeeded = numBytesNeeded + processor.getNumSpaces()*3;
        }
        // Kind of dirty to do this with two loops
        byte[] byteArrayForHash = new byte[numBytesNeeded];
        int index = 0;
        for (Processor processor: orderedProcessors) {
            processor.asByteArray(index, byteArrayForHash);
            index = index + processor.getNumSpaces()*3;
        }
        // https://github.com/sangupta/murmur
        // TODO... find nicer way to do this than int casting
        return (int) Murmur3.hash_x86_32(byteArrayForHash, byteArrayForHash.length, 0);

    }

    // TODO... do this when making new objects not in hashcode
    private Processor[] orderProcessorsByFirstTaskNum() {
        Processor[] orderedProcessors = this.processors.clone();
        Processor temp;
        while (true) {
            int i;
            for (i = 1; i < orderedProcessors.length; i++) {

                if (orderedProcessors[i].getFirstTaskId() < orderedProcessors[i-1].getFirstTaskId()) {

                    temp = orderedProcessors[i];
                    orderedProcessors[i] = orderedProcessors[i-1];
                    orderedProcessors[i-1] = temp;

                    break;
                }
            }
            if (i == orderedProcessors.length - 1 ) {
                return orderedProcessors;
            }
        }
    }

    /**
     * Deep equals method which ensures that equality is based off of
     * Processor data/location instead of Object IDs.
     * Overrides the base hashCode() Object method.
     *
     * @param otherObject : The other Processor set being equated to this one.
     * @return : Whether or not each Processors object store the same values in the same order.
     */
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object otherObject) {
        Processors otherProcessors;
        try {
            otherProcessors = (Processors) otherObject;
        } catch (ClassCastException e) {
            return false;
        }

        Processor[] orderedProcessors = orderProcessorsByFirstTaskNum();

        for (int i = 0; i < orderedProcessors.length; i++) {
            if (!otherProcessors.getProcessor(i).deepEquals(orderedProcessors[i])) {
                return false;
            }
        }
        return true;
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
