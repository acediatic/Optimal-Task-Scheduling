package softeng.project1.graph;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * This is the ScheduleStateChangeTest class for testing the ScheduleStateChange class.
 * It uses JUnit Testing and thoroughly tests the different methods used in ScheduleStateChange.
 * @author Osama Kashif
 */
public class ScheduleStateChangeTest {

    @Test
    void testRebuildSolutionPath() {
        ScheduleStateChange trial = new ScheduleStateChange(null, 0, 0, 0, 0);
        ScheduleStateChange trialChild = new ScheduleStateChange(trial, 1, 1, 2, 3);
        int [] path = new int [] {0, 0, 0, 0};
        List<int[]> check = new LinkedList<>(Arrays.asList(new int[][]{path}));
        assertArrayEquals(check.toArray(), trial.rebuildSolutionPath().toArray());
        check.add(new int[]{1,1,2,3});
        assertArrayEquals(check.toArray(), trialChild.rebuildSolutionPath().toArray());
    }

}
