package softeng.project1.graph.processors;

/**
 *
 */
public interface ProcessorNode {

    int getNextTaskID();

    void setNextTaskID(int taskID);

    int getStartTime();

    int getLength();

    void setLength(int length);

    ProcessorNode getNextNode();

    void setNextNode(ProcessorNode nextNode);

    ProcessorNode copyNode();

    boolean listEquals(ProcessorNode otherNode);

    byte[] listAsByteArray(int index, byte[] arrayToFill);

}
