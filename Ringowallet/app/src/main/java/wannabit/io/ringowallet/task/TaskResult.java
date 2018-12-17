package wannabit.io.ringowallet.task;

public class TaskResult {
    public int          taskType;
    public boolean      isSuccess;
    public int          resultCode;
    public String       resultMsg;

    public int          resultData1;
    public int          resultData2;

    public String       resultData3;
    public String       resultData4;

    public TaskResult() {
        this.isSuccess = false;
        this.resultCode = -1;
        this.resultMsg = "Task Fetal Error";
    }
}
