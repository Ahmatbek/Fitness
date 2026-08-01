package kg.biamino.projects.auth;

import kg.biamino.projects.exception.UserNotFoundException;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.repository.TrainerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

@Component
public class AuthHandler {

    private final TrainerRepository trainerRepository;

    public AuthHandler(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public boolean isOwner(String trainerUsername){
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername).orElseThrow(()-> new UserNotFoundException(trainerUsername));
        return trainer.getUser().getUsername().equals(a.getName());
    }

}