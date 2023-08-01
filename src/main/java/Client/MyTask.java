package Client;

import java.util.concurrent.Callable;

public class MyTask implements Callable<Integer> {
    private int taskId;

    public MyTask(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public Integer call() throws Exception {
        // Replace this with the actual task you want to perform in each thread
        return taskId * 2;
    }
}