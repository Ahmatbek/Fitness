package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.service.impl.WorkloadServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "trainer", fallback = WorkloadServiceClientFallback.class)
public interface WorkloadServiceClient {
    @PostMapping("/trainers/workload")
    ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request);

    @GetMapping("/trainers/{username}/summary")
    ResponseEntity<TrainerSummaryResponse> getMonthlyDuration(@PathVariable(name = "username") String username);
}
