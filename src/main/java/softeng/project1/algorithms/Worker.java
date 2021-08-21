package softeng.project1.algorithms;

import softeng.project1.gui.GuiMain;

public class Worker implements Runnable{

    private Thread helper;
    private final SchedulingAlgorithm algorithm;
    private volatile boolean exit = false;

    public Worker(SchedulingAlgorithm algorithm){
        this.algorithm = algorithm;
    }

    public void run(){
        this.helper = new Thread();

        while(!exit){
            algorithm.generateSchedule();
        }
    }

    public void setExit(){
        exit = true;
    }


}
