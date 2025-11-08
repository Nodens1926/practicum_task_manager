package manager;

import task.AbstractTask;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Queue;

public interface TaskManager {
    //получение списка задач
    List<SubTask> getSubTasks();
    List<Task> getTasks();
    List<Epic> getEpics();

    //удаление всех задач в списке
    public void deleteAllSubTasks();
    public void deleteAllTasks();
    public void deleteAllEpics();

    //получение по идентификатору
    public SubTask getIdSubtask(int id);
    public Task getIdTask(int id);
    public Epic getIdEpic(int id);

    //создание задачи --- СДЕЛАНО проверка на null
    public boolean createSubTask(SubTask subTask);
    public boolean createTask(Task task);
    public boolean createEpic(Epic epic);


    //обновление задачи --- СДЕЛАНО проверка на null, в новой сабТаске сделана проверка, что эпик совпадает и существует в базе данных
    public boolean updateSubTask(SubTask subTask);
    public boolean updateTask(Task task);
    public boolean updateEpic(Epic epic);

    //удаление по идентификатору --- СДЕЛАНО удаление сабтаски из мапы "subTasks" в epic, удаление всех сабтасок после удаления эпика
    public SubTask deleteSubTaskById(int id);
    public Task deleteTaskById(int id);
    public Epic deleteEpicById(int id);

    //все подзадачи определённого эпика --- СДЕЛАНО возвращает подзадачи эпика
    public List<SubTask> returnSubTasksInEpic (Epic epic);

    public List<AbstractTask> getHistory ();
}
