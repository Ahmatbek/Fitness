package kg.biamino.projects.service;

import kg.biamino.projects.TrainerSummaryResponse;
import kg.biamino.projects.TrainerWorkloadRequest;
import kg.biamino.projects.service.impl.WorkloadServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "trainer", fallback = WorkloadServiceClientFallback.class)
public interface WorkloadServiceClient {
    @PostMapping("/trainers/workload")
    ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request);

    @GetMapping("/trainers/{username}/summary")
    ResponseEntity<TrainerSummaryResponse> getMonthlyDuration(@PathVariable(name = "username") String username);
}
