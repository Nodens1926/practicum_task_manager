package task;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    //пустой список подзадач
    @Test
    void createEpicShouldCreateEmptyEpic() {
        Epic epic = new Epic("Test epic", "Test description", Status.NEW);
        taskManager.createEpic(epic);
        assertTrue(taskManager.getIdEpic(epic.getId()).getSubTasks().isEmpty());
    }

    //все подзадачи со статусом NEW
    @Test
    void createEpicShouldCreateAllNEWEpic() {
        Epic epic = new Epic("Test epic", "Test description", Status.NEW);
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask(epic, "Test", "Desc", Status.NEW);
        SubTask subTask2 = new SubTask(epic, "Test", "Desc", Status.NEW);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(Status.NEW, taskManager.getIdEpic(epic.getId()).getStatus());
    }

    //все подзадачи со статусом DONE
    @Test
    void createEpicShouldCreateAllDONEEpic() {
        Epic epic = new Epic("Test epic", "Test description", Status.NEW);
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask(epic, "Test", "Desc", Status.DONE);
        SubTask subTask2 = new SubTask(epic, "Test", "Desc", Status.DONE);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(Status.DONE, taskManager.getIdEpic(epic.getId()).getStatus());
    }

    //подзадачи со статусом NEW и DONE
    @Test
    void createEpicShouldCreateNEWDONEEpic() {
        Epic epic = new Epic("Test epic", "Test description", Status.NEW);
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask(epic, "Test", "Desc", Status.NEW);
        SubTask subTask2 = new SubTask(epic, "Test", "Desc", Status.DONE);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(Status.IN_Progress, taskManager.getIdEpic(epic.getId()).getStatus());
    }

    //подзадачи со статусом IN_PROGRESS
    @Test
    void createEpicShouldCreateIN_PROGRESSEpic() {
        Epic epic = new Epic("Test epic", "Test description", Status.NEW);
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask(epic, "Test", "Desc", Status.IN_Progress);
        SubTask subTask2 = new SubTask(epic, "Test", "Desc", Status.IN_Progress);

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(Status.IN_Progress, taskManager.getIdEpic(epic.getId()).getStatus());
    }
}