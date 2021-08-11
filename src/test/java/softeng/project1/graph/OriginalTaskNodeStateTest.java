package softeng.project1.graph;

import org.junit.jupiter.api.Test;
import softeng.project1.graph.tasks.ChangedTaskNodeState;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNodeState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

}
