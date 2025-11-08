package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Epic extends AbstractTask{
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Map<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void setTask(SubTask subTask) {
        if(subTask == null) {
            throw new NullPointerException("SubTask == null!!!");
        }
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public LocalDateTime getEndTime() {
        for(SubTask sT : subTasks.values()) {
            LocalDateTime localDateTime = sT.getEndTime();
            if (localDateTime.isAfter(endTime)) {
                endTime = localDateTime;
            }
        }
        return endTime;
    }

    @Override
    public Duration getDuration() {
        for(SubTask sT : subTasks.values()) {
            duration.plus(sT.getDuration());
        }
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        for(SubTask sT : subTasks.values()) {
            LocalDateTime localDateTime = sT.getStartTime();
            if (localDateTime.isBefore(startTime)) {
                startTime = localDateTime;
            }
        }
        return startTime;
    }

}
