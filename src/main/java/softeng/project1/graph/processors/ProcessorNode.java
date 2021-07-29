package softeng.project1.graph.processors;

public interface ProcessorNode {

    int getStartTime();

    int getLength();

    int getNextTaskID();

    boolean listEquals(ProcessorNode otherNode);

    byte[] listAsByteArray(int index, byte[] arrayToFill);

}
