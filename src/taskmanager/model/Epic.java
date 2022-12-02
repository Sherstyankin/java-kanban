package taskmanager.model;

import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasksList;

    public Epic(String taskName, String content, List<Subtask> subtasksList) {
        super(taskName, content);
        this.subtasksList = subtasksList;
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }
    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", content='" + getContent() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}

