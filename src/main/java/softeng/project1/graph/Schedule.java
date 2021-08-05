package softeng.project1.graph;

import java.util.List;

public interface Schedule {
    public long getHashKey();

    public boolean processorEquals();

    public int getMaxDataReadyTime();

    public List<Schedule> expand();

}
