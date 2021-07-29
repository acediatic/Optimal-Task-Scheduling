package softeng.project1.graph.processors;

public interface ProcessorNode {

    int getNextTaskID();

    int getStartTime();

    int getLength();

    ProcessorNode getNextNode();

    boolean listEquals(ProcessorNode otherNode);

    byte[] listAsByteArray(int index, byte[] arrayToFill);

}
