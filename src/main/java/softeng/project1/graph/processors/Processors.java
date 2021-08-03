package softeng.project1.graph.processors;

import softeng.project1.graph.processors.processor.Processor;

/**
 *
 */
public interface Processors {

    Processor getProcessor(int processorID);

    Processors copyAndAddProcessor(Processor processor);

    long murmurHash();

    boolean deepEquals(Processors otherProcessors);

}
