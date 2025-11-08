package histories;

import task.AbstractTask;

import java.util.List;

public interface HistoryManager {
    void add(AbstractTask task);
    List<AbstractTask> getHistory();
    void remove(int id);
}
