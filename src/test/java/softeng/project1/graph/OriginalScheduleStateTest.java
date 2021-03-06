package softeng.project1.graph;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import softeng.project1.graph.processors.OriginalProcessorsState;
import softeng.project1.graph.processors.Processors;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.util.HashMap;
import java.util.List;
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
    private static final OriginalTaskNodeState original1 = new OriginalTaskNodeState((short)150,20, 3, childLinks, 0, 3);
    private static final OriginalTaskNodeState original2 = new OriginalTaskNodeState((short)150,20, 3, childLinks, 0, 3);
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
        o1 = new OriginalScheduleState(tNode, fNode, 3, (short)1);
        o2 = new OriginalScheduleState(tNode, fNode, 3, (short)1);
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
        assertArrayEquals(new ScheduleStateChange(null,0,0,0, 0).rebuildSolutionPath().toArray(), o1.generateStateChange(0,0,0, 0).rebuildSolutionPath().toArray());
        assertNotEquals(new ScheduleStateChange(null,0,0,0, 0), o1.generateStateChange(1,0,0, 0));
        assertNotEquals(new ScheduleStateChange(null,1,0,0, 0), o1.generateStateChange(0,0,0, 0));
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

    /**
     * Tested against String value so that in case there are any internal issues
     * in the classes used within this class, it doesn't affect this test - also
     * if a comparison object is made incorrectly the String avoids that.
     *
     * Test may be redundant due to how the method works.
     */
    @Test
    public void testCopyFreeNodesHook() {
        assertEquals("{0=TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                ", 1=TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "}",o1.copyFreeNodesHook().toString()); // Possibly redundant test.
    }

    @Test
    public void testCopyTaskNodesHook() {
        assertEquals(copyCheckMap,o1.copyTaskNodesHook()); // Possibly redundant test.
    }

    @Test
    public void testGetHashKey() {
        Processors p1 = new OriginalProcessorsState(3);
        Processors p2 = new OriginalProcessorsState(0);
        assertEquals(p1.toString(), o1.getHashKey().toString());
        assertEquals(p2.toString(), o3.getHashKey().toString());
    }

    /**
     * Tested against String value so that in case there are any internal issues
     * in the classes used within this class, it doesn't affect this test - also
     * if a comparison object is made incorrectly the String avoids that.
     */
    @Test
    public void testExpand() {
        assertNull(o4.expand());
        assertNotEquals(null, o1.expand());
        assertEquals("[-----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 20\n" +
                "1 - 20\n" +
                "2 - 20\n" +
                "\n" +
                "-----------------------------\n" +
                ", -----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 20\n" +
                "1 - 20\n" +
                "2 - 20\n" +
                "\n" +
                "-----------------------------\n" +
                ", -----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 20\n" +
                "1 - 20\n" +
                "2 - 20\n" +
                "\n" +
                "-----------------------------\n" +
                ", -----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 20\n" +
                "1 - 20\n" +
                "2 - 20\n" +
                "\n" +
                "-----------------------------\n" +
                ", -----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 20\n" +
                "1 - 20\n" +
                "2 - 20\n" +
                "\n" +
                "-----------------------------\n" +
                ", -----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 2\n" +
                "Processor Prerequisites:\n" +
                "0 - 20\n" +
                "1 - 20\n" +
                "2 - 20\n" +
                "\n" +
                "-----------------------------\n" +
                "]",o1.expand().toString());
    }

    @Test
    public void testRebuildPath() {
//        ScheduleStateChange trial = new ScheduleStateChange(null, 0, 0, 0, 0);
//        assertArrayEquals(trial.rebuildSolutionPath().toArray(), o1.rebuildPath().toArray()); // Won't work as o1 has null ScheduleStateStage so can't call .rebuildSolutionPath.
        Throwable exception = assertThrows(NullPointerException.class, () -> o1.rebuildPath());
        assertNull(exception.getMessage());
    }

    @Test
    public void testGetMaxBottomLevel() {
        assertEquals(0,o1.getMaxBottomLevel());
        assertNotEquals(5,o1.getMaxBottomLevel());
    }

    @Test
    public void testGetMaxDataReadyTime() {
        assertEquals(0,o1.getMaxDataReadyTime());
        assertNotEquals(5,o1.getMaxDataReadyTime());
    }

    @Test
    public void testGetIdleTime() {
        assertEquals(0,o1.getIdleTime());
        assertNotEquals(5,o1.getIdleTime());
    }

    @Test
    public void testToString() {
        assertEquals("-----------------------------\n" +
                "Schedule State:\n" +
                "Free Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "General Task Nodes: \n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "TaskNodeState:\n" +
                "numLinks: 3\n" +
                "Processor Prerequisites:\n" +
                "0 - 0\n" +
                "1 - 0\n" +
                "2 - 0\n" +
                "\n" +
                "-----------------------------\n",o1.toString());
    }

}
