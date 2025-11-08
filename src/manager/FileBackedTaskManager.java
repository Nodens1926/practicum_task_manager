package manager;

import exceptions.ManagerIOException;
import exceptions.TaskNotExistsException;
import histories.HistoryManager;
import task.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (SubTask subTask : getSubTasks()) {
                writer.write(toString(subTask));
                writer.newLine();
            }

            writer.newLine();
            writer.write(historyToString(history));
        } catch (IOException e) {
            throw new ManagerIOException("Ошибка сохранения в файл", e);
        }
    }

    // загружает состояние менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<String> lines;

        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new ManagerIOException("Ошибка чтения файла", e);
        }

        // обработка всех тасков
        for (int i = 1; i < lines.size() && !lines.get(i).isEmpty(); i++) {
            AbstractTask task = manager.fromString(lines.get(i));

            if (task instanceof Task) {
                manager.tasks.put(task.getId(), (Task) task);
            } else if (task instanceof Epic) {
                manager.epics.put(task.getId(), (Epic) task);
            } else if (task instanceof SubTask subTask) {
                manager.subTasks.put(subTask.getId(), subTask);

                Epic epic = subTask.getEpic();
                if (epic != null && manager.epics.containsKey(epic.getId())) {
                    epic.getSubTasks().put(subTask.getId(), subTask);
                    manager.changeStatusEpic(epic);
                }
            }
        }

        // история
        if (lines.size() > 1 && !lines.get(lines.size()-1).isEmpty()) {
            for (int id : historyFromString(lines.get(lines.size()-1))) {
                if (manager.tasks.containsKey(id)) {
                    manager.history.add(manager.tasks.get(id));
                } else if (manager.subTasks.containsKey(id)) {
                    manager.history.add(manager.subTasks.get(id));
                } else if (manager.epics.containsKey(id)) {
                    manager.history.add(manager.epics.get(id));
                }
            }
        }

        return manager;
    }

    // превращение задачи в строку
    private String toString(AbstractTask task) {
        String epicInfo = "";
        Type type;
        Class<? extends AbstractTask> clazz = task.getClass();

        if (clazz == SubTask.class) {
            SubTask subTask = (SubTask) task;
            epicInfo += subTask.getEpic().getId();
            type = Type.SUBTASK;
        } else if (clazz == Task.class) {
            type = Type.TASK;
        } else {
            type = Type.EPIC;
        }
        return new StringBuilder()
                .append(task.getId()).append(",")
                .append(type).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",")
                .append(epicInfo).toString();
    }

    // создание задачи из строки
    private AbstractTask fromString(String value) {
        String[] values = value.split(",");//6 элементов
        Status status = Status.valueOf(values[3]);
        int id = Integer.parseInt(values[0]);
        String name = values[2];
        String description = values[4];


        switch(values[1]) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "SUBTASK":
                Epic subTaskEpic = null;
                if(!values[5].isEmpty()) {
                    subTaskEpic = epics.get(Integer.parseInt(values[5]));//возможно тут ошибка
                }
                SubTask subTask = new SubTask(subTaskEpic, values[2], values[4], id, status);
                subTask.setId(Integer.parseInt(values[0]));
                return subTask;
            case "EPIC":
                Epic epic = new Epic(values[2], values[4], status);
                epic.setId(Integer.parseInt(values[0]));
                return epic;
            default:
                throw new TaskNotExistsException("такой задачи нет!!!");
        }
    }

    // превращение истории в строку
    static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(AbstractTask::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    //возвращает историю из строки
    static List<Integer> historyFromString(String value) {
            if(value.isEmpty()) {
                return new ArrayList<>();
            }
            return Arrays.stream(value.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return super.getSubTasks();
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override//ЕСТЬ SAVE
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override//ЕСТЬ SAVE
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override//ЕСТЬ SAVE
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public SubTask getIdSubtask(int id) {
        return super.getIdSubtask(id);
    }

    @Override
    public Task getIdTask(int id) {
        return super.getIdTask(id);
    }

    @Override
    public Epic getIdEpic(int id) {
        return super.getIdEpic(id);
    }

    @Override//ЕСТЬ SAVE
    public boolean createSubTask(SubTask subTask) {
        boolean result = super.createSubTask(subTask);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public boolean createTask(Task task) {
        boolean result = super.createTask(task);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public boolean createEpic(Epic epic) {
        boolean result = super.createEpic(epic);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public boolean updateSubTask(SubTask subTask) {
        boolean result = super.updateSubTask(subTask);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public SubTask deleteSubTaskById(int id) {
        SubTask result = super.deleteSubTaskById(id);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public Task deleteTaskById(int id) {
        Task result = super.deleteTaskById(id);
        save();
        return result;
    }

    @Override//ЕСТЬ SAVE
    public Epic deleteEpicById(int id) {
        Epic result = super.deleteEpicById(id);
        save();
        return result;
    }

    @Override
    public List<SubTask> returnSubTasksInEpic(Epic epic) {
        return super.returnSubTasksInEpic(epic);
    }

    @Override
    public List<AbstractTask> getHistory() {
        save();
        return super.getHistory();
    }
}
