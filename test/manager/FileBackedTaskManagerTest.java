package manager;

import exceptions.ManagerIOException;
import histories.HistoryManager;
import histories.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import task.AbstractTask;
import task.Epic;
import task.Status;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    //TempDir - временно создает и удаляет директорию для каждого теста
    @TempDir
    Path tempDir;

    //Тест преобразования строки в список ID истории
    @Test
    void historyFromStringShouldCheckIDInHistory() {
        String historyString = "1,3,5,2";
        List<Integer> historyId = FileBackedTaskManager.historyFromString(historyString);

        assertEquals(List.of(1, 3, 5, 2), historyId);
        assertEquals(4, historyId.size());
        assertEquals(1, historyId.get(0));
        assertEquals(2, historyId.get(3));
    }

    //Тест с пустой строкой
    @Test
    void historyFromStringShouldCheckEmptyHistory() {
        String historyString = "";
        List<Integer> historyIds = FileBackedTaskManager.historyFromString(historyString);

        assertTrue(historyIds.isEmpty());
    }

    //Тест с одним ID
    @Test
    void historyFromStringShouldCheckSingleId() {
        String historyString = "7";
        List<Integer> historyIds = FileBackedTaskManager.historyFromString(historyString);

        assertEquals(List.of(7), historyIds);
        assertEquals(1, historyIds.size());
    }

    //Тест преобразования истории в строку
    @Test
    void historyToStringShouldConvertHistoryToString() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("task1", "desc1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "desc2", Status.IN_Progress);
        task2.setId(2);
        Epic epic1 = new Epic("epic1", "desc3", Status.NEW);
        epic1.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);

        String historyString = FileBackedTaskManager.historyToString(historyManager);

        assertEquals("1,2,3", historyString);
    }

    //Тест с пустой историей
    @Test
    void historyToStringShouldCheckEmptyHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        String historyString = FileBackedTaskManager.historyToString(historyManager);

        assertEquals("", historyString);
    }

    //Тест с одной задачей в истории
    @Test
    void historyToStringShouldCheckSingleTask() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("task", "desc", Status.DONE);
        task.setId(5);
        historyManager.add(task);
        String historyString = FileBackedTaskManager.historyToString(historyManager);

        assertEquals("5", historyString);
    }

    //Тест загрузки менеджера из файла с задачами и историей
    @Test
    void loadFromFileShouldLoadFromFile() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();

        String csvContent = """
            id,type,name,status,description,epic
            1,TASK,task1,NEW,Description 1,
            2,EPIC,epic1,NEW,Epic description,
            3,SUBTASK,subTask1,NEW,Sub description,2
            
            1,3,2""";

        Files.writeString(file.toPath(), csvContent);

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(manager.getTasks());
        assertEquals(1, manager.getTasks().size());
        assertEquals("task1", manager.getTasks().get(0).getName());

        assertNotNull(manager.getEpics());
        assertEquals(1, manager.getEpics().size());
        assertEquals("epic1", manager.getEpics().get(0).getName());

        assertNotNull(manager.getSubTasks());
        assertEquals(1, manager.getSubTasks().size());
        assertEquals("subTask1", manager.getSubTasks().get(0).getName());

        List<AbstractTask> history = manager.getHistory();
        assertEquals(3, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
        assertEquals(2, history.get(2).getId());
    }

    //Тест загрузки подзадачи с корректной связью с эпиком
    @Test
    void loadFromFileShouldCheckEpicSubTaskConnection() throws IOException {
        File file = tempDir.resolve("test_relation.csv").toFile();

        String csvContent = """
            id,type,name,status,description,epic
            1,EPIC,Epic 1,NEW,Epic description,
            2,SUBTASK,SubTask 1,DONE,Sub description,1
            
            1,2""";

        Files.writeString(file.toPath(), csvContent);

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getSubTasks().size());

        Epic epic = manager.getEpics().get(0);
        assertEquals(Status.DONE, epic.getStatus());
    }

    //Тест попытки загрузки из несуществующего файла
    @Test
    void loadFromFileShouldLoadUnexistedFile() {
        File file = new File("non_existent_file.csv");

        assertThrows(ManagerIOException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        });
    }

    //Тест загрузки файла с некорректной историей (нечисловые значения)
    @Test
    void loadFromFileShouldLoadInvalidHistory() throws IOException {
        File file = tempDir.resolve("invalid_history.csv").toFile();
        String csvContent = """
            id,type,name,status,description,epic
            1,TASK,Task 1,NEW,Description 1,
            
            abc,def,xyz""";
        Files.writeString(file.toPath(), csvContent);

        assertThrows(NumberFormatException.class, () -> {
            FileBackedTaskManager.loadFromFile(file);
        });
    }

    //Интеграционный тест: сохраняем историю и загружаем обратно
    @Test
    void loadFromFileShouldSaveAndLoadHistory() throws IOException {
        File file = tempDir.resolve("integration_test.csv").toFile();

        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        Task task1 = new Task("task1", "desc1", Status.NEW);
        Task task2 = new Task("task2", "desc2", Status.IN_Progress);
        Epic epic1 = new Epic("epic1", "desc3", Status.NEW);

        manager1.createTask(task1);
        manager1.createTask(task2);
        manager1.createEpic(epic1);

        manager1.getIdTask(task1.getId());
        manager1.getIdEpic(epic1.getId());
        manager1.getIdTask(task2.getId());

        System.out.println("История после добавления тасков: " + manager1.getHistory());

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        List<AbstractTask> history = manager2.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
        assertEquals(epic1.getId(), history.get(1).getId());
        assertEquals(task2.getId(), history.get(2).getId());
    }
}