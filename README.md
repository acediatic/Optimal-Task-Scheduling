# SE306 Project 1 - Team 1 : Outsourced to Pakistan

## Team Members
* Adam Sinclair
* Henry Man
* Osama Kashif
* Remus Courtenay
* Syed Ahmad Kazmi

## Project Outline
For this project, we have been tasked with developing a program used to schedule a certain number of tasks over a certain number of processors whilst
keeping to a list of constraints. As part of the project there are two milestones.

# Milestone 1
Milestone 1 requires that we develop a scheduler which is able to return a valid schedule. A valid schedule refers to a schedule for the tasks which
keeps to the constraints, but is not neccessarily the most optimal solution. The runnable jar for our Milestone 1 release can be found [here](https://github.com/SoftEng306-2021/project-1-p1t16-outsourced-to-pakistan/releases/tag/v1.0)

## Running the program
Before running the program, check that the runnable jar along with the input DOT file are located in the same folder. The runnable 
jar files can be found on the [Releases](https://github.com/SoftEng306-2021/project-1-p1t16-outsourced-to-pakistan/releases) page.

To run the jar files, run the follow command in the terminal: 
```
java -jar scheduler.jar INPUT.dot P [OPTION]
```
COMPULSORY ARGUMENTS:
* ```INPUT.dot``` : a directed acyclic task graph with integer weights using the DOT file format.
* ```P```: the number of processors available to schedule tasks on

OPTIONAL ARGUMENTS:
* ```-o OUTPUT```: name of output file (default name is INPUT-output.dot)

Once the program has completed execution, the output file will be generated in the same folder
