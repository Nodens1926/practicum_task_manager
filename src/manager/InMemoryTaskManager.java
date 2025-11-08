package manager;

import exceptions.HistoryIsEmpty;
import exceptions.TaskNotExistsException;
import exceptions.EpicNotFoundException;
import histories.InMemoryHistoryManager;
import task.*;
import task.Status.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected InMemoryHistoryManager history = new InMemoryHistoryManager();

    //private / protected
    private int idGen = 1;

    //получение списка задач
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //удаление всех задач в списке
    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.setStatus(Status.NEW);
        }
        subTasks.clear();
    }
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }
    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(history::remove);
        subTasks.keySet().forEach(history::remove);

        epics.clear();
        subTasks.clear();
    }

    //получение по идентификатору --- ИСПРАВИТЬ НАЗВАНИЕ: getTaskById
    @Override
    public SubTask getIdSubtask(int id) {
        if(!subTasks.containsKey(id)) {
            throw new TaskNotExistsException("Задачи с идентификатором " + id + " нет в списке задач");
        }
        SubTask st = subTasks.get(id);
        history.add(st);
        return st;
    }
    @Override
    public Task getIdTask(int id) {
        if(!tasks.containsKey(id)) {
            throw new TaskNotExistsException("Задачи с идентификатором " + id + " нет в списке задач");
        }
        Task t = tasks.get(id);
        history.add(t);
        return t;
    }
    @Override
    public Epic getIdEpic(int id) {
        if(!epics.containsKey(id)) {
            throw new TaskNotExistsException("Задачи с идентификатором " + id + " нет в списке задач");
        }
        Epic e = epics.get(id);
        history.add(e);
        return e;
    }

    //создание задачи return - не может не быть true
    @Override
    public boolean createSubTask(SubTask subTask) {
        Epic gE = subTask.getEpic();//проверку SubTask==null нет смысла делать, т.к. gE есть часть сабтаски

        if (gE == null) {
            throw new NullPointerException("Epic == null!");
        } else if(!epics.containsKey(gE.getId())) {
            throw new EpicNotFoundException("Эпика с id: " + gE.getId() + " не существует!");
        }
        int id = idGen++;

        subTask.setId(id);
        gE.getSubTasks().put(id, subTask);
        changeStatusEpic(gE);

        subTasks.put(id, subTask);
        return subTasks.containsKey(id);
    }

    //return бесполезен так как нет false
    @Override
    public boolean createTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Task == null!!");
        }
        int id = idGen++;
        task.setId(id);

        tasks.put(task.getId(), task);
        return true;
    }

    //return бесполезен так как нет false
    @Override
    public boolean createEpic(Epic epic) {
        if (epic == null) {
            throw new NullPointerException("Epic == null!!");
        }
        int id = idGen++;
        epic.setId(id);

        epics.put(epic.getId(), epic);
        changeStatusEpic(epic);
        return true;
    }


    //обновление задачи
    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (subTask == null) {
            throw new NullPointerException("subTask == null!!");
        }
        int id = subTask.getId();
        Epic epic = subTask.getEpic();

        //тут не нужна проверка на null для Сабтаски т.к. по умолчанию false
        if(!subTasks.containsKey(id)) {
            return false;
        } else if (epic == null) {
            throw new NullPointerException("Epic == null!!");
        } else if (!epics.containsKey(epic.getId())) {
            throw new EpicNotFoundException("Эпика новой подзадачи нет!!");
        }
        subTasks.put(id, subTask);
        epic.getSubTasks().put(id, subTask);//добавление в новый эпик
        changeStatusEpic(epic);
        return true;
    }
    @Override
    public boolean updateTask(Task task) {
        if (task == null) {
            throw new NullPointerException("task == null!!");
        }
        int id = task.getId();

        if(!tasks.containsKey(id)) {
            return false;
        }
        tasks.put(id, task);
        return true;
    }
    @Override
    public boolean updateEpic(Epic epic) {
        if (epic == null) {
            throw new NullPointerException("epic == null!!");
        }
        int id = epic.getId();

        if(!epics.containsKey(id)) {
            return false;
        }
        epics.put(id, epic);
        return true;
    }

    //удаление по идентификатору
    @Override
    public SubTask deleteSubTaskById(int id) {
        if(!subTasks.containsKey(id)) {
            throw new TaskNotExistsException("Подзадачи с id: " + id + " нет");
        }
        Epic epic = subTasks.get(id).getEpic();
        epic.getSubTasks().remove(id);
        changeStatusEpic(epic);
        history.remove(id);
        return subTasks.remove(id);
    }
    @Override
    public Task deleteTaskById(int id) {
        if(!tasks.containsKey(id)) {
            throw new TaskNotExistsException("Задачи с id: " + id + " нет");
        }
        history.remove(id);
        return tasks.remove(id);
    }
    @Override
    public Epic deleteEpicById(int id) {
        if(!epics.containsKey(id)) {
            throw new TaskNotExistsException("Эпика с id: " + id + " нет");
        }
        for (SubTask st : epics.get(id).getSubTasks().values()) {
            subTasks.remove(st.getId());
        }
        history.remove(id);
        return epics.remove(id);
    }

    //все подзадачи определённого эпика
    @Override
    public List<SubTask> returnSubTasksInEpic (Epic epic) {
        if (epic == null) {
            throw new NullPointerException("epic == null!!");
        } else if(!epics.containsKey(epic.getId())) {
            throw new TaskNotExistsException("Эпика с id: " + epic.getId() + " нет");
        }
        return new ArrayList<>(epic.getSubTasks().values());
    }

    //получение истории
    @Override
    public List<AbstractTask> getHistory() {
        List<AbstractTask> getH = history.getHistory();

        if (getH.isEmpty()) {
            throw new HistoryIsEmpty("История пуста!!");
        }
        return getH;
    }

    //расчёт status для эпика
    protected void changeStatusEpic(Epic epic) {
        boolean ifDONE = true;
        boolean ifNEW = true;

        for (SubTask subTask : epic.getSubTasks().values()) {
            Status status = subTask.getStatus();
            switch(status) {
                case Status.IN_Progress:
                    ifDONE = false;
                    ifNEW = false;
                    break;
                case Status.NEW:
                    ifDONE = false;
                    break;
                case Status.DONE:
                    ifNEW = false;
                    break;
            }
        }

        //инициализация status
        if (epic.getSubTasks().isEmpty() || ifNEW) {
            epic.setStatus(Status.NEW);
        } else if (ifDONE) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_Progress);
        }
    }
}
