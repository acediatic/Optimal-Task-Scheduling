package softeng.project1.graph.processors;

import softeng.project1.graph.TaskNode;

public class ProcessorState implements Processor {

    private final int processorID;
    private final ProcessorNode headNode;
    private final int numNodes;

    public ProcessorState(int processorID, ProcessorNode headNode, int numNodes) {
        this.processorID = processorID;
        this.headNode = headNode;
        this.numNodes = numNodes;
    }

    public ProcessorNode getHeadNode() {
        return this.headNode;
    }

    public int getNumNodes() {
        return this.numNodes;
    }

    @Override
    public Processor copyAndInsert(TaskNode newTaskNode) {

        return new ProcessorState(
                this.processorID,
                copyAndInsertListNodes(
                        newTaskNode.getTaskID(),
                        newTaskNode.getTaskCost(),
                        newTaskNode.getProcessorPrerequisite(this.processorID)),
                this.numNodes + 1 // Node inserted so num increases
        );

    }

    private ProcessorNode copyAndInsertListNodes(int taskID, int taskLength, int prerequisite) {

        // Holding onto reference to head node so we can return it once the list is completed
        ProcessorNode newHeadNode = new SingleLinkedProcessorNode(
                null,
                this.headNode.getNextTaskID(),
                this.headNode.getStartTime(),
                this.headNode.getLength()
        );

        // Initialising variables for use in the loop
        ProcessorNode currentNode = this.headNode;
        ProcessorNode nextNode;
        ProcessorNode copyCurrentNode = newHeadNode;
        ProcessorNode copyNextNode;

        int diffNodeTaskLength;
        int diffPrereqStartPosition;
        int diffDiffNTPS;

        while ((nextNode = currentNode.getNextNode()) != null) {

            diffPrereqStartPosition = prerequisite - currentNode.getStartTime();
            diffNodeTaskLength = currentNode.getLength() - taskLength;
            diffDiffNTPS = diffNodeTaskLength - diffPrereqStartPosition;

            // has the prereq been passed?
            if (diffPrereqStartPosition <= 0) {
                copyCurrentNode = copyAndInsertTaskIntoNode(taskID, taskLength, currentNode, nextNode, copyCurrentNode, 0, diffNodeTaskLength);
                copyRestOfNodes(copyCurrentNode, nextNode);
                return newHeadNode;

                // is the node long enough to compensate for the prerequisite position?
            } else if (diffDiffNTPS >= 0) {
                copyCurrentNode = copyAndInsertTaskIntoNode(taskID, taskLength, currentNode, nextNode, copyCurrentNode, diffPrereqStartPosition, diffDiffNTPS);
                copyRestOfNodes(copyCurrentNode, nextNode);
                return newHeadNode;

                // node doesn't fit in here :( so we just copy old list and move onto next node
            } else {
                copyCurrentNode = copyNextNode(copyCurrentNode, nextNode);
            }
            currentNode = nextNode;
        }

        // We've reached the end of the list without breaking, so we're just going bang the node right on the end.

        // Getting where to put it
        // Don't have to add length because last node always length 0
        int insertPoint = Math.max(prerequisite, currentNode.getStartTime());

        // Extending old final node to bridge gap between end of last task and start of new task
        copyCurrentNode.setLength(insertPoint - prerequisite);
        copyCurrentNode.setNextTaskID(taskID);

        // Adding tail node that exists after newly added last task
        copyNextNode = new SingleLinkedProcessorNode(
                null, // no more nodes after this
                0, // no more tasks after this
                insertPoint + taskLength,
                0); // no length as list ends after last node
        copyCurrentNode.setNextNode(copyNextNode);

        return newHeadNode;
    }

    private ProcessorNode copyNextNode(ProcessorNode copyOfCurrentNode, ProcessorNode nextNode) {
        ProcessorNode copyOfNextNode = nextNode.copyNode();
        copyOfCurrentNode.setNextNode(copyOfNextNode);
        return copyOfNextNode;
    }

    private void copyRestOfNodes(ProcessorNode copyOfCurrentNode, ProcessorNode currentNode) {

        ProcessorNode nextNode;
        ProcessorNode copyNextNode;

        while((nextNode = currentNode.getNextNode()) != null) {
            copyNextNode = nextNode.copyNode();
            copyOfCurrentNode.setNextNode(copyNextNode);
            copyOfCurrentNode = copyNextNode;
            currentNode = nextNode;
        }


    }

    private ProcessorNode copyAndInsertTaskIntoNode(int taskID, int taskLength, ProcessorNode currentNode, ProcessorNode nextNode, ProcessorNode copyOfCurrentNode, int spaceBeforeInsert, int spaceAfterInsert) {

        // Updating copy with changed values
        copyOfCurrentNode.setNextTaskID(taskID);
        copyOfCurrentNode.setLength(spaceBeforeInsert);

        // Creating new node representing the space after the inserted task
        ProcessorNode newNode = new SingleLinkedProcessorNode(
                null,
                currentNode.getNextTaskID(),
                currentNode.getStartTime() + spaceBeforeInsert + taskLength,
                spaceAfterInsert
        );

        // inserting into previous
        copyOfCurrentNode.setNextNode(newNode);

        // getting copy of next node
        ProcessorNode copyOfNextNode = nextNode.copyNode();
        // inserting into new
        newNode.setNextNode(copyOfNextNode);

        // returning next
        return copyOfNextNode;
    }

    @Override
    public boolean deepEquals(Processor otherProcessor) {
        // This assumes head nodes are never null
        return this.headNode.listEquals(otherProcessor.getHeadNode());
    }

    @Override
    public byte[] asByteArray(int index, byte[] arrayToFill) {
        // This assumes head nodes are never null
        return this.headNode.listAsByteArray(index, arrayToFill);
    }

}
