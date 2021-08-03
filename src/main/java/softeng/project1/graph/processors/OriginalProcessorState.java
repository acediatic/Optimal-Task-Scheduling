package softeng.project1.graph.processors;

public class originalProcessorState implements Processor{

    private final int processorID;

    public originalProcessorState(int processorID) {
        this.processorID = processorID;
    }

    @Override
    public int getNumSpaces() {
        return 1; // Always one space which represents the empty space at the end of the processor
    }

    @Overried
    public Processor copyAndInsert(TaskNode TaskNode) {
        return new ProcessorState() // TODO...
    }

    @Override
    public boolean deepEquals(Processor otherProcessor) {
        try {
            return this.processorID = ((OriginalProcessorState) otherProcessor).processorID;
        } catch (ClassCastException e) {
            return false; // Processor State objects will never equal original processor states
        }
    } 

    @Override
    public byte[] asByteArray(int index, byte[] arrayToFill) {
        arrayToFill[index++] = 0 // start
        arrayToFill[index++] = 0 // length
        arrayToFill[index++] = -1 // next task ID
    }


}