# project-1-p1t16-outsourced-to-pakistan
project-1-p1t16-outsourced-to-pakistan created by GitHub Classroom

# SE306 Project 1 - Team 1 : Outsourced to Pakistan

## Team Members
* Adam Sinclair
* Henry Man
* Osama Kashif
* Remus Courtenay
* Syed Ahmad Kazmi

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
