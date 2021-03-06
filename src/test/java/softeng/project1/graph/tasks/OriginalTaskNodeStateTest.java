package softeng.project1.graph.tasks;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the OriginalTaskNodeStateTest class for testing the OriginalTaskNodeState class.
 * It uses JUnit Testing and thoroughly tests the different methods used in OriginalTaskNodeState.
 * This includes inherited methods as well.
 * @author Osama Kashif
 */
public class OriginalTaskNodeStateTest {

    @Test
    void testTaskIDTrue() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 0, childLinks, 0, 0);
        assertEquals(150, tNode.getTaskID());
    }

    @Test
    void testTaskIDFalse() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 420,0, 0, childLinks, 0, 0);
        assertNotEquals(69, tNode.getTaskID());
    }

    @Test
    void testIsFreeTrue() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 0, childLinks, 0, 0);
        assertTrue(tNode.isFree());
    }

    @Test
    void testIsFreeFalse() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 3, childLinks, 0, 5);
        assertFalse(tNode.isFree());
    }

    @Test
    void testCopyAndSetPrerequisite() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 1, childLinks, 0, 1);
        int[] prerequisite = new int[1];
        assertFalse(tNode.isFree());
        TaskNode returned = tNode.copyAndSetPrerequisite(prerequisite);
        assertTrue(returned.isFree());
    }

    @Test
    void testGetTaskCost() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,10, 0, childLinks, 0, 0);
        assertNotEquals(0, tNode.getTaskCost());
        assertEquals(10, tNode.getTaskCost());
    }

    @Test
    void testGetChildLinks() {
        int[][] childLinks = new int[2][2];
        int[][] falseLinks = {{1}};
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 0, childLinks, 0, 0);
        int[][] gottenLinks = tNode.getChildLinks();
        assertFalse(Arrays.deepEquals(falseLinks, gottenLinks));
        assertArrayEquals(childLinks, gottenLinks);
    }

    @Test
    void testGetBottomLevel() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 0, childLinks, 500, 0);
        assertNotEquals(300, tNode.getBottomLevel());
        assertEquals(500, tNode.getBottomLevel());
    }

    @Test
    void testGetMaxCommunicationCost() {
        int[][] childLinks = {{1,5}, {2,8}, {3,3}};
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 0, childLinks, 0, 0);
        assertNotEquals(5, tNode.getMaxCommunicationCost());
        assertEquals(8, tNode.getMaxCommunicationCost());
    }

    @Test
    void testGetProcessorPrerequisite() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,0, 0, childLinks, 0, 3);
        assertNotEquals(1, tNode.getProcessorPrerequisite(2));
        assertEquals(0, tNode.getProcessorPrerequisite(2));
    }

    @Test
    void testToString() {
        int[][] childLinks = new int[2][2];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState((short) 150,20, 2, childLinks, 10, 2);
        assertEquals("TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n",tNode.toString());
    }

}
