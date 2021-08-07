package softeng.project1.graph.processors;

import softeng.project1.graph.processors.processor.Processor;
import softeng.project1.graph.tasks.TaskNode;

import java.util.Arrays;

public abstract class ProcessorsState implements Processors {

    protected final Processor[] processors;
    protected final int idleTime;

    protected ProcessorsState(Processor[] processors, int idleTime) {
        this.processors = processors;
        this.idleTime = idleTime;
    }

    @Override
    public Processor getProcessor(int processorID) {
        return this.processors[processorID];
    }

    @Override
    public int getNumProcessors() {
        return this.processors.length;
    }

    @Override
    public int getIdleTime() {
        return this.idleTime;
    }

    @Override
    public Processors copyAndAddProcessor(TaskNode newNode, int processorID) {
        Processor[] newProcessors = Arrays.copyOf(this.processors, this.processors.length);
        Processor newProcessor = this.processors[processorID].copyAndInsert(newNode);
        newProcessors[processorID] = newProcessor;

        return new ChangedProcessorsState(
                newProcessors,
                calculateMaxProcessorLength(newProcessor),
                calculateIdleTime(newProcessor)
        );
    }

    protected abstract int calculateMaxProcessorLength(Processor newProcessor);

    protected abstract int calculateIdleTime(Processor newProcessor);



}
