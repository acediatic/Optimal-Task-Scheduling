package softeng.project1.graph.tasks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * This is the TaskNodeTest class for testing the ChangedTaskNodeState class.
 * It uses JUnit Testing and thoroughly tests the different methods used in ChangedTaskNodeState.
 * This includes inherited methods as well.
 * @author Osama Kashif
 */
class TaskNodeTest {

    @Test
    void testIsFreeTrue() {
        int[] preReqs = new int[0];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertTrue(tNode.isFree());
    }

    @Test
    void testIsFreeFalse() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 3, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,3, preReqs);
        assertFalse(tNode.isFree());
    }

    @Test
    void testTaskIDTrue() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,3, preReqs);
        assertEquals(150, tNode.getTaskID());
    }

    @Test
    void testTaskIDFalse() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(420,0, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,3, preReqs);
        assertNotEquals(69, tNode.getTaskID());
    }

    @Test
    void testCopyAndSetPrerequisite() {
        int[] preReqs = new int[2];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 1, childLinks, 0, 1);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,2, preReqs);
        int[] prerequisite = new int[2];
        assertFalse(tNode.isFree());
        TaskNode returned = tNode.copyAndSetPrerequisite(prerequisite);
        assertFalse(returned.isFree());
        returned = returned.copyAndSetPrerequisite(prerequisite);
        assertTrue(returned.isFree());
    }

    @Test
    void testGetTaskCost() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,10, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertNotEquals(0, tNode.getTaskCost());
        assertEquals(10, tNode.getTaskCost());
    }

    @Test
    void testGetChildLinks() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[2][2];
        int[][] falseLinks = {{1}};
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        int[][] gottenLinks = tNode.getChildLinks();
        assertFalse(Arrays.deepEquals(falseLinks, gottenLinks));
        assertArrayEquals(childLinks, gottenLinks);
    }

    @Test
    void testGetBottomLevel() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 500, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertNotEquals(300, tNode.getBottomLevel());
        assertEquals(500, tNode.getBottomLevel());
    }

    @Test
    void testGetMaxCommunicationCost() {
        int[] preReqs = new int[5];
        int[][] childLinks = {{1,5}, {2,8}, {3,3}};
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertNotEquals(5, tNode.getMaxCommunicationCost());
        assertEquals(8, tNode.getMaxCommunicationCost());
    }

    @Test
    void testGetProcessorPrerequisite() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 3);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertNotEquals(1, tNode.getProcessorPrerequisite(2));
        assertEquals(0, tNode.getProcessorPrerequisite(2));
    }

    @Test
    void testToString() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[2][2];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,20, 2, childLinks, 10, 2);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertEquals("TaskNodeState:\n" +
                "numLinks: 0\n" +
                "Processor Prerequisites:\n" +
                "0 - 01 - 02 - 03 - 04 - 0",tNode.toString());
    }

}
