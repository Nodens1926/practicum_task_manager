package manager;

import exceptions.HistoryIsEmpty;
import exceptions.TaskNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Epic epic;
    protected Task task;
    protected SubTask subTask;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        epic = new Epic("TestEpic", "Description", Status.NEW);
        task = new Task("TestTask", "Description", Status.NEW);
        subTask = new SubTask(epic, "TestSubTask", "Description", 0, Status.NEW);

        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createSubTask(subTask);
    }

    //ПОЛУЧЕНИЕ ПО ID
    @Test
    void getIdTaskShouldCreateAndGetTask() {
        Task createdTask = taskManager.getIdTask(task.getId());
        assertEquals(task, createdTask);
    }
    @Test
    void getIdEpicShouldCreateAndGetEpic() {
        Epic createdEpic = taskManager.getIdEpic(epic.getId());
        assertEquals(epic, createdEpic);
    }
    @Test
    void getIdSubtaskShouldCreateAndGetSubTask() {
        SubTask createdSubTask = taskManager.getIdSubtask(subTask.getId());
        assertEquals(subTask, createdSubTask);
        assertEquals(epic.getId(), createdSubTask.getEpic().getId());
    }

    //НЕПРАВИЛЬНЫЙ ID
    @Test
    void getIdTaskShouldThrowExceptionWhenTaskNotFound() {
        assertThrows(TaskNotExistsException.class, () -> taskManager.getIdTask(999));
    }
    @Test
    void getIdEpicShouldThrowExceptionWhenEpicNotFound() {
        assertThrows(TaskNotExistsException.class, () -> taskManager.getIdEpic(999));
    }
    @Test
    void getIdSubtaskShouldThrowExceptionWhenSubTaskNotFound() {
        assertThrows(TaskNotExistsException.class, () -> taskManager.getIdSubtask(999));
    }

    //ВСЕ ТАСКИ
    @Test
    void getTasksShouldReturnAllTasks() {
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }
    @Test
    void getEpicsShouldReturnAllEpics() {
        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }
    @Test
    void getSubTasksShouldReturnAllSubTasks() {
        List<SubTask> subTasks = taskManager.getSubTasks();
        assertEquals(1, subTasks.size());
        assertEquals(subTask, subTasks.get(0));
    }

    //ОБНОВЛЕНИЕ ТАСКОВ
    @Test
    void updateTaskShouldUpdateTask() {
        Task updatedTask = new Task("UpdatedTask", "Description", Status.DONE);
        updatedTask.setId(task.getId());

        taskManager.updateTask(updatedTask);
        Task retrievedTask = taskManager.getIdTask(task.getId());

        assertEquals("UpdatedTask", retrievedTask.getName());
        assertEquals(Status.DONE, retrievedTask.getStatus());
    }
    @Test
    void updateEpicShouldUpdateEpic() {
        Epic updatedEpic = new Epic("UpdatedEpic", "Description", Status.DONE);
        updatedEpic.setId(epic.getId());

        taskManager.updateEpic(updatedEpic);
        Epic retrievedEpic = taskManager.getIdEpic(epic.getId());

        assertEquals("UpdatedEpic", retrievedEpic.getName());
    }
    @Test
    void shouldUpdateSubTask() {
        SubTask updatedSubTask = new SubTask(epic, "UpdatedSubTask", "Description", 0, Status.DONE);
        updatedSubTask.setId(subTask.getId());

        taskManager.updateSubTask(updatedSubTask);
        SubTask retrievedSubTask = taskManager.getIdSubtask(subTask.getId());

        assertEquals("UpdatedSubTask", retrievedSubTask.getName());
        assertEquals(Status.DONE, retrievedSubTask.getStatus());
    }

    //УДАЛЕНИЕ ПО ID
    @Test
    void deleteTaskByIdShouldDeleteTask() {
        taskManager.deleteTaskById(task.getId());
        assertThrows(TaskNotExistsException.class, () -> taskManager.getIdTask(task.getId()));
        assertTrue(taskManager.getTasks().isEmpty());
    }
    @Test
    void deleteEpicByIdShouldDeleteEpic() {
        taskManager.deleteEpicById(epic.getId());
        assertThrows(TaskNotExistsException.class, () -> taskManager.getIdEpic(epic.getId()));
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
    @Test
    void deleteSubTaskByIdShouldDeleteSubTask() {
        taskManager.deleteSubTaskById(subTask.getId());
        assertThrows(TaskNotExistsException.class, () -> taskManager.getIdSubtask(subTask.getId()));
        assertTrue(taskManager.getSubTasks().isEmpty());
    }

    //УДАЛЕНИЕ ВСЕХ ЗАДАЧ
    @Test
    void deleteAllTasksShouldDeleteAllTasks() {
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }
    @Test
    void deleteAllEpicsShouldDeleteAllEpics() {
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
    @Test
    void deleteAllSubTasksShouldDeleteAllSubTasks() {
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.getSubTasks().isEmpty());
        assertEquals(Status.NEW, epic.getStatus());
    }

    //ВОЗВРАЩЕНИЕ САБТАСКА ИЗ ЭПИКА
    @Test
    void returnSubTaskInEpicShouldReturnSubtasksInEpic() {
        List<SubTask> subtasksInEpic = taskManager.returnSubTasksInEpic(epic);
        assertEquals(1, subtasksInEpic.size());
        assertEquals(subTask, subtasksInEpic.get(0));
    }

    //ТЕСТЫ ИСТОРИИ
    @Test
    void getHistoryShouldReturnHistory() {
        taskManager.getIdTask(task.getId());
        taskManager.getIdEpic(epic.getId());

        List<AbstractTask> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task));
        assertTrue(history.contains(epic));
    }
    @Test
    void getHistoryShouldThrowExceptionWhenHistoryEmpty() {
        assertThrows(HistoryIsEmpty.class, () -> taskManager.getHistory());
    }
    @Test
    void getIdTaskShouldNotAddDuplicateToHistory() {
        taskManager.getIdTask(task.getId());
        taskManager.getIdTask(task.getId());

        List<AbstractTask> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }
}