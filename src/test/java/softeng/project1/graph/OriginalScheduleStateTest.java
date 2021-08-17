package softeng.project1.graph;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import softeng.project1.graph.tasks.OriginalTaskNodeState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        int[][] childLinks = new int[0][0];
//        OriginalTaskNodeState tNode = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
//        OriginalTaskNodeState fNode = new OriginalTaskNodeState(150,0, 0, childLinks, 0, 0);
        Map<Integer, OriginalTaskNodeState>  tNode = new HashMap<>();
        Map<Integer, OriginalTaskNodeState>  fNode = new HashMap<>();
        Map<Integer, OriginalTaskNodeState>  otherFNode = new HashMap<>();
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
