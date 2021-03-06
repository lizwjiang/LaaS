package org.g6.laas.server.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueScenario {
    private Future future;
    private boolean report;
    private String reportTemplate;

    public boolean isDone(){
        return future.isDone();
    }

    public boolean isCancelled(){
        return future.isCancelled();
    }

    public Object getRunningResult() throws ExecutionException, InterruptedException {
        return future.get();
    }
}
