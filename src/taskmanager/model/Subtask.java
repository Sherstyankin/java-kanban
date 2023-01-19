package taskmanager.model;

import taskmanager.enums.TaskType;

public class Subtask extends Task {
    private final Integer epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(String taskName, String content, Integer epicId) {
        super(taskName, content);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }
    @Override
    public TaskType getType() {
        return type;
    }
    @Override
    public String toStringForFile() {
        return getId() + "," + getType() + "," + getTaskName() + "," + getStatus() + "," + getContent() + "," +
                getEpicId() + "\n";
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "taskName='" + getTaskName() + '\'' +
                ", content='" + getContent() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
