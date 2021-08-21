package softeng.project1.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import softeng.project1.algorithms.SchedulingAlgorithm;
import softeng.project1.algorithms.astar.heuristics.AStarHeuristicManager;
import softeng.project1.algorithms.astar.heuristics.HeuristicManager;
import softeng.project1.algorithms.astar.sequential.SequentialAStarSchedulingAlgorithm;
import softeng.project1.graph.OriginalScheduleState;
import softeng.project1.graph.Schedule;
import softeng.project1.graph.tasks.OriginalTaskNodeState;
import softeng.project1.graph.tasks.TaskNode;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;
/**
 * This is the test class for the AStarIOHandler Class
 * It utilizes JUNIT 5 to test the different methods found within the class
 * @author Syed Kazmi
 */
class AStarIOHandlerTest {


    @Test
    void readFile() {
        //code for first read
        IOHandler B1 = new AStarIOHandler(
                "src/test/resources/testGraph.dot",
                "src/test/resources/testGraphOut.dot",
                "Comp1",
                Short.valueOf((short) 2)
        );
        //code from for second read
        IOHandler B2 = new AStarIOHandler(
                "src/test/resources/testGraph2.dot",
                "src/test/resources/testGraph2Out.dot",
                "Comp2",
                Short.valueOf((short) 2)
        );
        assertNotEquals(B1.readFile(),B2.readFile());

    }

    @Test
    void writeFile() {
        //Test to write files and inspect the processes of the applciation and output into txt files
        IOHandler Write1 = new AStarIOHandler(
                "src/test/resources/testGraph.dot",
                "src/test/resources/testGraphOut.dot",
                "Comp1",
                Short.valueOf((short) 2)
        );
        Schedule originalSchedule = Write1.readFile();
        //write schedule to string and then to text file
       //create list of ints generated from algorithm, can possibly work around
        HeuristicManager heuristicManager = new AStarHeuristicManager(Write1.getSumWeights(), (short)1, Write1.getListSchedulingAlgoStep());
        SchedulingAlgorithm algorithm;
        algorithm = new SequentialAStarSchedulingAlgorithm(
                originalSchedule,
                heuristicManager,
                new HashMap<>(),
                new PriorityQueue<>(),
                Write1.getListSchedulingAlgoStep()
        );
        List<int[]> PostAlgSchedule = algorithm.generateSchedule();
        //task needs to be schduled otherwise write doesnt work
        //Could replace PostAlgSchedule with a List<int[]> but so far unable to create one accepted by code
        String result = Write1.writeFile(PostAlgSchedule);
        //Read File A, then write this to file B, then readfile() on the writeFile and compare to pre calc'ed length
        assertEquals("Max processor length: 45",result);
    }

    @Test
    void getSumWeights() {
        IOHandler SumWeights = new AStarIOHandler(
                "src/test/resources/testGraph.dot",
                "src/test/resources/testGraphOut.dot",
                "Comp1",
                Short.valueOf((short) 2)
        );
        assertEquals(0,SumWeights.getSumWeights());
    }
}