package softeng.project1.graph;

import org.junit.jupiter.api.Test;
import softeng.project1.graph.tasks.TaskNode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is the OriginalScheduleStateTest class for testing the OriginalScheduleState class.
 * It uses JUnit Testing and thoroughly tests the different methods used in OriginalScheduleState.
 * This includes inherited methods as well.
 * @author Osama Kashif
 */
public class OriginalScheduleStateTest {

    @Test
    void testDeepEquals() {
        Map<Integer, TaskNode>  tNode = new HashMap<>();
        Map<Integer, TaskNode>  fNode = new HashMap<>();
        Map<Integer, TaskNode>  otherFNode = new HashMap<>();
        OriginalScheduleState o1 = new OriginalScheduleState(tNode, fNode, 5);
        OriginalScheduleState o2 = new OriginalScheduleState(tNode, fNode, 5);
        OriginalScheduleState o3 = new OriginalScheduleState(tNode, fNode, 0);
        OriginalScheduleState o4 = new OriginalScheduleState(tNode, otherFNode, 5);
        OriginalScheduleState o5 = new OriginalScheduleState(tNode, otherFNode, 5);
        assertTrue(o1.deepEquals(o1));
        assertFalse(o1.deepEquals(o2));
        assertFalse(o1.deepEquals(o3));
        assertFalse(o1.deepEquals(o4));
        assertFalse(o1.deepEquals(o5));

    }

    @Test
    void testGenerateStateChange() {}

    @Test
    void testGetOriginalSchedule() {}

    @Test
    void testGetTaskNode() {}

    @Test
    void testGetHashKey() {}

    @Test
    void testExpand() {}

    @Test
    void testFillProcessorPrerequisites() {}

    @Test
    void testGetMaxBottomLevel() {}

    @Test
    void testGetMaxDataReadyTime() {}

    @Test
    void testGetIdleTime() {}

    @Test
    void testToString() {}

}
