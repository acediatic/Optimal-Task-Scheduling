package softeng.project1.graph.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * TODO...
 * @author Osama Kashif
 */
public class OriginalTaskNodeStateTest {

    @Test
    void testTaskIDTrue() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        assertEquals(150, tNode.getTaskID());
    }

    @Test
    void testTaskIDFalse() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState(420,0, 0, childLinks, 0, 0);
        assertNotEquals(69, tNode.getTaskID());
    }

    @Test
    void testIsFreeTrue() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        assertEquals(true, tNode.isFree());
    }

    @Test
    void testIsFreeFalse() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState(150,0, 3, childLinks, 0, 5);
        assertEquals(false, tNode.isFree());
    }

    @Test
    void testCopyAndSetPrerequisite() {
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState tNode = new OriginalTaskNodeState(150,0, 1, childLinks, 0, 1);
        int[] prereq = new int[1];
        TaskNode returned = tNode.copyAndSetPrerequisite(prereq);
        assertEquals(true, returned.isFree());
    }

}
