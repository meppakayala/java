import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ParallelAsyncOrdered {

    public static CompletableFuture<String> processTask(int taskId) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate some work
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Result of Task " + taskId;
        });
    }

    public static void main(String[] args) {
        int numberOfTasks = 5;
        List<Integer> taskIds = new ArrayList<>();
        for (int i = 1; i <= numberOfTasks; i++) {
            taskIds.add(i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numberOfTasks);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // Start all tasks asynchronously
        for (int taskId : taskIds) {
            futures.add(processTask(taskId));
        }

        List<CompletableFuture<String>> orderedProcessingFutures = new ArrayList<>();
        CompletableFuture<?> lastFuture = CompletableFuture.completedFuture(null); // Start with a completed future

        // Chain the processing of results in the original order
        for (CompletableFuture<String> future : futures) {
            lastFuture = lastFuture.thenApply(ignored -> future.join()); // Wait for the current future and get its result
            orderedProcessingFutures.add(lastFuture.thenApply(result -> {
                System.out.println("Processing result: " + result + " (in order)");
                return result.toUpperCase(); // Example processing
            }));
        }

        // Wait for all processing to complete and collect the final results
        List<String> finalResults = orderedProcessingFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        executor.shutdown();

        System.out.println("\nFinal Ordered Results:");
        finalResults.forEach(System.out::println);
    }
}
