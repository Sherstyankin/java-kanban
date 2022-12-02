package taskmanager.model;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String taskName, String content, Integer epicId) {
        super(taskName, content);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
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
