package softeng.project1.algorithms;

import softeng.project1.gui.GuiData;
import java.util.List;

public interface SchedulingAlgorithm {

    List<int[]> generateSchedule();

    GuiData getGuiData();
}
