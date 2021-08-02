package softeng.project1.graph.processors;

public class ProcessorsState implements Processors{

    private Processor[] processors;

    @Override
    public Processor getProcessor(int processorID) {
        return processors[processorID];
    }

    @Override
    public long murmurHash() {
        return 0; // default dummy code
    }

    @Override
    public boolean deepEquals(Processors otherProcessors) {
        return this.processors.equals(otherProcessors);
    }
}
