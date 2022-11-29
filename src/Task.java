public class Task {
    private String taskName;
    private String content;
    private Integer id;
    private String status;

    public Task(String taskName, String content, String status) {
        this.taskName = taskName;
        this.content = content;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", content='" + content + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
