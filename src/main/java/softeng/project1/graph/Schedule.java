package softeng.project1.graph;

import java.util.List;

public interface Schedule {
    long getHashKey();

    boolean deepEquals(Schedule otherSchedule);

    int getMaxDataReadyTime();

    List<Schedule> expand();

    int getIdleTime();

    int getMaxBottomLevel();

}
