package softeng.project1.graph.processors;

/**
 *
 */
public interface Processors {

    Processor getProcessor(int processorID);

    long murmurHash();

    boolean deepEquals(Processors otherProcessors);

}
