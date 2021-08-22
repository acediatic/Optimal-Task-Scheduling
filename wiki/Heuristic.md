# A* Heuristic

For the A* algorithm to work effectively, a priority value is needed. In the scheduling case, this heuristic is an estimation
of the final schedule length. This has to be a **lower bound** in order for A* to guarantee optimality. The tighter the 
bound, the more effective A* is. 

## Calculation

Our heuristic is derived from Oliver Sinnen's [2014 paper](https://www.sciencedirect.com/science/article/abs/pii/S0305054813002542?via%3Dihub).
The heuristic is the maximum of the idle time, bottom level, and data ready functions described below.

_f(s) = max{f_idle-time(s), f_bl(s), f_DRT(s)}_

### f_idle_time

The idle time of a partial schedule, s, is here defined as the sum of the weights of all nodes on the input graph, plus 
the current idle time (time a processor is not working on a task) for all processors, divided by the number of processors. 

### f_bottom_level

The bottom level of a node is the sum of computation costs for the current node and all nodes on the critical path from
that node to the exit. 

The max bottom level of a partial schedule, s, is defined as the maximum bottom level of its nodes. 

### f_data_ready_time

The data ready time of a task is the earliest time a task could be scheduled on any of the processors, given the
communication costs of its parent tasks, as applicable. 

The max data ready time of a partial schedule, s, is defined as the maximum data ready time of its **free** nodes.