package softeng.project1.graph;

import softeng.project1.algorithms.heuristics.HeuristicManager;
import softeng.project1.graph.processors.Processors;

import java.util.List;

/**
 * @author Remus Courtenay
 * @version 1.0.1
 * @since 1.8
 *
 * The Schedule Interface describes a partial schedule generated by the A* scheduling algorithm during computation of
 * the multi-processor task scheduling problem with communication costs.
 * Schedules store data regarding the state of the processors within the partial schedule as well as data regarding the
 * state of each task.
 *
 * As Schedule is made for use in A*, all implementations of Schedule must provide the functionality for quick insertion
 * and retrieval from a HashMap. It must also provide all necessary information for the calculation of the heuristic
 * value generated by the HeuristicManager.
 * @see HeuristicManager
 *
 * As a partial schedule, Schedule also provides the functionality to create further schedules via the expand method.
 * As Schedule objects should be considered immutable, care should be taken to only generate new Schedules via the
 * expand method rather than directly instantiating them.
 */
public interface Schedule {

    /**
     * It's a pain that this directly exposes Processors. Using a HashTable over a HashMap would be preferable so this
     * wasn't required.
     * @return : The Processors object storing data about the current state of the processors for use as a key in the
     *           hashmap required by A*.
     */
    Processors getHashKey();

    /**
     * Specialised equals method that checks the stored data within the schedule is equal rather than just checking the
     * object references.
     *
     * @param otherSchedule : The other Schedule object being equated to this one.
     * @return : Whether or not the partial schedule described by the given Schedule is equivalent to this one.
     */
    boolean deepEquals(Schedule otherSchedule);

    /**
     * Generates a set of new fringe Schedules by greedily scheduling each free task onto every processor.
     * The number of created schedules is bounded by O(nm) where n is the number of tasks and m is the number of
     * processors.
     * Pruning techniques are utilised to reduce this number by attempting to only generating unique schedules.
     *
     * Once expand has been called on a Schedule it should no longer be considered as a fringe schedule and thus be
     * removed from the Priority Queue. All generated schedules are by definition fringe and, unless they have already
     * been created elsewhere, should be inserted into the Priority Queue.
     *
     * @return : A list of new fringe schedules for insertion into the Priority Queue, assuming they don't already
     * exist.
     */
    List<Schedule> expand();

    List<int[]> rebuildPath();

    /**
     * @return : The sum total amount of time on each processor spent idling before all tasks (on all processors) are
     * completed.
     */
    int getIdleTime();

    /**
     * @return : The longest critical path extending from any scheduled path.
     */
    int getMaxBottomLevel();

    /**
     * @return : The earliest moment that all data sent by currently scheduled tasks is first available on all stored
     * processors.
     */
    int getMaxDataReadyTime();

}
