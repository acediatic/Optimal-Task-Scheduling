# A* Scheduling Algorithms

The A* scheduling algorithm is a version of a branch-and-bound depth-first search that explores the best option first.
It achieves this through the use of a **priority queue**, where each potential schedule holds some **heuristic value**.
The heuristic must _underestimate_, in order for A* to be valid.

Our implementation does the following:

    Create a priority queue of schedules and insert initial (empty) partial schedule.
        while true:
            Pop top of priority queue
            if schedule has no free nodes:
                break and return schedule
            otherwise:
                Create all permutations of schedulings (see pruning) and add to queue.

The <code>expand</code> method of the scheduling algorithms is used to create all the different permutations of each of
the free nodes, scheduled on each processor.

## Sequential

The sequential A* algorithm is the default configuration. It uses only one thread in the creation of the schedule, and
hence is usually slower than the parallel implementation.

### Implementation

It uses a non-blocking priority queue to hold its partial schedules, and only returns when the optimal schedule is
found.

## Parallel

The parallel A* algorithm is available when the <code>-p N</code> flag and value are specified, where the <code>
-p</code>
flag indicates using multiple cores, and <code>N</code> is the number of cores to use.

### Implementation

The parallel algorithm uses a thread pool along with a blocking priority queue to hand its partial schedules. The number
of threads specified in the command line input are created in a pool. Each thread in the pool will repeatedly remove the
top task (in a synchronised manner due to the blocking queue), and explore it, adding all subsequent schedules back into
the priority queue.

The algorithm will return when an optimal schedule has been found, **after the currently executing threads finish**.
This ensures that if the optimal schedule is expanded slower, there is not a race condition if another non-optimal
schedule is found at the same time.

As mentioned in Heuristic, in order to prune all schedules with a heuristic **greater than or equal to** the list
scheduling, the algorithm must return when the queue is empty, and return the list scheduling at this point. To achieve
this, an atomic integer is used to count the number of schedules currently in the queue. When this integer gets to zero,
the algorithm returns from execution, and returns the list scheduling. 

