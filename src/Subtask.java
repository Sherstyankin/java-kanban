public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String taskName, String content, String status, Integer epicId) {
        super(taskName, content, status);
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
