package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.TrainerWorkloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeadLetterQueue {


    @JmsListener(destination = "DLQ.training-queue")
    public void failedMessages(TrainerWorkloadRequest trainerWorkloadRequest){
        log.error("Message failed: {}", trainerWorkloadRequest);
    }
}
