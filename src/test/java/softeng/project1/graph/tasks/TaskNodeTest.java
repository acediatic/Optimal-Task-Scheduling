package softeng.project1.graph.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO...
 * @author Osama Kashif
 */
class TaskNodeTest {

    @Test
    void testIsFreeTrue() {
        int[] preReqs = new int[0];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,0, preReqs);
        assertEquals(true, tNode.isFree());
    }

    @Test
    void testIsFreeFalse() {
        int[] preReqs = new int[5];
        int[][] childLinks = new int[0][0];
        OriginalTaskNodeState original = new OriginalTaskNodeState(150,0, 3, childLinks, 0, 0);
        ChangedTaskNodeState tNode = new ChangedTaskNodeState(original,3, preReqs);
        assertEquals(false, tNode.isFree());
    }

}
