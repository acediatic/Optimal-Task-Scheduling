package softeng.project1.graph;

import java.util.List;

public interface Schedule {
    public long getHashKey();

    public boolean deepEquals(Schedule otherSchedule);

    public int getMaxDataReadyTime();

    public List<Schedule> expand();

}
