package task;

public class SubTask extends AbstractTask {
    protected Epic epic;

//    public SubTask(Epic epic, String name, String description, int id, Status status) {
//        super(name, description, id, status);
//        this.epic = epic;
//    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public SubTask(Epic epic, String name, String description, Status status) {
        super(name, description, status);
        this.epic = epic;
    }

    public SubTask(Epic epic, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

}
