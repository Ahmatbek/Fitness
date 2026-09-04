import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.controller.TrainerController;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.service.TrainerSummaryService;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class TrainerControllerTest {

    @Mock
    private TrainerSummaryService trainerSummaryService;
    private TrainerController trainerController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        trainerController = new TrainerController(trainerSummaryService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(trainerController)
                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getMonthlySummaryByTrainerUsername_returns200() throws Exception{
        mockMvc.perform(get("/trainers/{username}/summary", "Aidana.Toktosunova")
                .contentType("application/json")
        ).andExpect(status().isOk());

        verify(trainerSummaryService).getMonthlySummaryByTrainerUsername("Aidana.Toktosunova");
    }
}
