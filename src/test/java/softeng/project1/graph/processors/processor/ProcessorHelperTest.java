package softeng.project1.graph.processors.processor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * This is the ProcessorHelperTest class for testing the ProcessorHelper class.
 * It uses JUnit Testing and thoroughly tests the different methods used in ProcessorHelper.
 * @author Osama Kashif
 */
public class ProcessorHelperTest {

    @Test
    public void testFillProcessorSpace() {
        int [][] arr = new int[2][3];
        int [][] test = new int[2][3];
        assertArrayEquals(test, arr);
        test[0][0] = 1;
        test[0][1] = 2;
        test[0][2] = 3;
        ProcessorHelper.fillProcessorSpace(arr, 0, 1,2,3);
        assertArrayEquals(test, arr);
    }

    @Test
    public void testAddFinalSpace() {
        int [][] arr = new int[2][3];
        int [][] test = new int[2][3];
        assertArrayEquals(test, arr);
        test[1][0] = 1;
        test[1][1] = 0;
        test[1][2] = -1;
        ProcessorHelper.addFinalSpace(arr, 1);
        assertArrayEquals(test, arr);
    }
}
