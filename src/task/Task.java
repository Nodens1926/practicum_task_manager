package task;

public class Task extends AbstractTask{
    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public Task(String name, String description, Status status) {
        super(name, description, status);
    }

}
