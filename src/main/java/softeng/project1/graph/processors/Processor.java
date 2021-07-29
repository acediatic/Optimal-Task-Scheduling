package softeng.project1.graph.processors;

import softeng.project1.graph.TaskNode;

public interface Processor {

    ProcessorNode getHeadNode();

    int getNumNodes();

    Processor copyAndInsert(TaskNode taskNode);

    boolean deepEquals(Processor otherProcessor);

    byte[] asByteArray(int index, byte[] arrayToFill);
}
