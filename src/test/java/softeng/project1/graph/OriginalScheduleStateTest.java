package softeng.project1.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the OriginalScheduleStateTest class for testing the OriginalScheduleState class.
 * It uses JUnit Testing and thoroughly tests the different methods used in OriginalScheduleState.
 * This includes inherited methods as well.
 * @author Osama Kashif
 */
public class OriginalScheduleStateTest {

    private static final int[][] childLinks = new int[2][2];
    private static final OriginalTaskNodeState original1 = new OriginalTaskNodeState((short)150,20, 3, childLinks, 0, 0);
    private static final OriginalTaskNodeState original2 = new OriginalTaskNodeState((short)150,20, 3, childLinks, 0, 0);
    private static final Map<Short, TaskNode>  tNode = new HashMap<>();
    private static final Map<Short, TaskNode>  fNode = new HashMap<>();
    private static final Map<Short, TaskNode>  otherFNode = new HashMap<>();
    private static final Map<Short, TaskNode> copyCheckMap = new HashMap<>((short)1,100);
    private static OriginalScheduleState o1;
    private static OriginalScheduleState o2;
    private static OriginalScheduleState o3;
    private static OriginalScheduleState o4;
    private static OriginalScheduleState o5;

    @BeforeAll
    private static void setup() {
        tNode.put((short)0, original1);
        tNode.put((short)1, original2);
        fNode.put((short)0, original1);
        fNode.put((short)1, original2);
        o1 = new OriginalScheduleState(tNode, fNode, 5, (short)1);
        o2 = new OriginalScheduleState(tNode, fNode, 5, (short)1);
        o3 = new OriginalScheduleState(tNode, fNode, 0, (short)0);
        o4 = new OriginalScheduleState(tNode, otherFNode, 5, (short)0);
        o5 = new OriginalScheduleState(tNode, otherFNode, 5, (short)1);
    }

    @Test
    public void testDeepEquals() {
        assertTrue(o1.deepEquals(o1));
        assertFalse(o1.deepEquals(o2));
        assertFalse(o1.deepEquals(o3));
        assertFalse(o1.deepEquals(o4));
        assertFalse(o1.deepEquals(o5));
    }

    @Test
    public void testGenerateStateChange() {
        assertArrayEquals(new ScheduleStateChange(null,0,0,0).rebuildSolutionPath().toArray(), o1.generateStateChange(0,0,0).rebuildSolutionPath().toArray());
        assertNotEquals(new ScheduleStateChange(null,0,0,0), o1.generateStateChange(1,0,0));
        assertNotEquals(new ScheduleStateChange(null,1,0,0), o1.generateStateChange(0,0,0));
    }

    @Test
    public void testGetOriginalSchedule() {
        assertEquals(o1, o1.getOriginalSchedule());
        assertNotEquals(o2, o1.getOriginalSchedule());
    }

    @Test
    public void testGetTaskNode() {
        assertEquals(original1,o1.getTaskNode((short)0));
        assertNotEquals(original2,o1.getTaskNode((short)0));
        assertEquals(o1.getTaskNode((short)0),o2.getTaskNode((short)0));
    }

    @Test
    public void testCopyFreeNodesHook() {
        assertEquals(copyCheckMap,o1.copyFreeNodesHook());
        assertEquals(copyCheckMap,o2.copyFreeNodesHook());
//        assertNotEquals(copyCheckMap,o3.copyFreeNodesHook());
    }

    @Test
    public void testCopyTaskNodesHook() {
        assertEquals(copyCheckMap,o1.copyTaskNodesHook());
        assertEquals(copyCheckMap,o2.copyTaskNodesHook());
//        assertNotEquals(copyCheckMap,o3.copyTaskNodesHook());
    }

    @Test
    public void testGetHashKey() {}

    @Test
    public void testExpand() {}

    @Test
    public void testFillProcessorPrerequisites() {}

    @Test
    public void testGetMaxBottomLevel() {}

    @Test
    public void testGetMaxDataReadyTime() {}

    @Test
    public void testGetIdleTime() {}

    @Test
    public void testToString() {}

}
