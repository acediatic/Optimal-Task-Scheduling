package softeng.project1.graph.processors;
//
///**
// *
// */
//public class SingleLinkedProcessorNode implements ProcessorNode {
//
//    private final ProcessorNode nextNode;
//    private final int nextTaskID;
//    private final int startTime;
//    private final int length;
//
//    /**
//     *
//     * @param nextNode
//     * @param nextTaskID
//     * @param startTime
//     * @param length
//     */
//    public SingleLinkedProcessorNode(ProcessorNode nextNode, int nextTaskID, int startTime, int length) {
//
//        this.nextNode = nextNode;
//        this.nextTaskID = nextTaskID;
//        this.startTime = startTime;
//        this.length = length;
//
//    }
//
//    @Override
//    public int getNextTaskID() {
//        return this.nextTaskID;
//    }
//
//    @Override
//    public int getStartTime() {
//        return this.startTime;
//    }
//
//    @Override
//    public int getLength() {
//        return this.length;
//    }
//
//    @Override
//    public ProcessorNode getNextNode() {
//        return this.nextNode;
//    }
//
//    @Override
//    public boolean listEquals(ProcessorNode otherNode) {
//
//        // Corresponding node is null and we are not, thus list is not equal
//        if (otherNode == null) {
//            return false;
//
//            // Corresponding node is equal, need to check rest of list
//        } else if (otherNode.getNextTaskID() == this.nextTaskID
//                && otherNode.getStartTime() == this.startTime
//                && otherNode.getLength() == this.length) {
//
//            // Are we the end of our list?
//            if (this.nextNode == null) {
//                // We are, so check if corresponding node is also the end of their list
//                return otherNode.getNextNode() == null;
//            } else {
//                // We're not, so recursively check the rest of the list
//                return nextNode.listEquals(otherNode.getNextNode());
//            }
//
//            // Corresponding node is not equal, thus list is not equal
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public byte[] listAsByteArray(int index, byte[] arrayToFill) {
//
//        // Casting int to byte cuts off the left-most 24 bits so numbers over 255
//        // will start returning values equal to lower numbers
//        arrayToFill[index++] = (byte) this.nextTaskID; // increment after makes it return the original value
//        arrayToFill[index++] = (byte) this.startTime;
//        arrayToFill[index++] = (byte) this.length;
//
//        if (this.nextNode == null) {
//            return arrayToFill;
//        } else {
//            return nextNode.listAsByteArray(index, arrayToFill);
//        }
//
//    }
//
//}
