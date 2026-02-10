//package com.andresen.macrocalculatorbackend;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@AutoConfigureMock
//class MacroCalculatorBackEndApplicationTests {
//
//    @Test
//    void contextLoads() {
//    }
//
//}
package com.andresen.macrocalculatorbackend;

import com.andresen.macrocalculatorbackend.macrogoal.MacroGoal;
import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalRepository;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MacroCalculatorBackEndApplicationTests {

    private static final String BASE_URL = "/user-profile";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper; // tools.jackson
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private MacroGoalRepository macroGoalRepository;

    @Test
    void onboarding_createsUser_andActiveMacroGoal() throws Exception {
        String response = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userProfile.id").isNumber())
                .andExpect(jsonPath("$.activeMacroGoal.id").isNumber())
                .andExpect(jsonPath("$.activeMacroGoal.isActive").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode body = objectMapper.readTree(response);

        long userId = body.at("/userProfile/id").asLong();
        long activeGoalId = body.at("/activeMacroGoal/id").asLong();

        UserProfile user = userProfileRepository.findById(userId).orElseThrow();
        assertThat(user.getId()).isEqualTo(userId);

        MacroGoal activeGoal =
                macroGoalRepository.findByUserProfile_IdAndIsActiveTrue(userId).orElseThrow();

        assertThat(activeGoal.getId()).isEqualTo(activeGoalId);
        assertThat(activeGoal.isActive()).isTrue();
    }

    @Test
    void put_recalculatesMacros_and_deactivatesOldGoal() throws Exception {
        Created created = createUser();

        mockMvc.perform(put(BASE_URL + "/{id}", created.userId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequestJsonWithWeight(95.0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeMacroGoal.isActive").value(true));

        MacroGoal newActive =
                macroGoalRepository.findByUserProfile_IdAndIsActiveTrue(created.userId()).orElseThrow();

        assertThat(newActive.getId()).isNotEqualTo(created.activeGoalId());

        MacroGoal old =
                macroGoalRepository.findById(created.activeGoalId()).orElseThrow();

        assertThat(old.isActive()).isFalse();
    }

    @Test
    void patch_updatesOnlyProvidedFields_and_recalculatesMacros() throws Exception {
        Created created = createUser();

        UserProfile before =
                userProfileRepository.findById(created.userId()).orElseThrow();

        mockMvc.perform(patch(BASE_URL + "/{id}", created.userId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 { "weightKg": 87.5 }
                                 """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeMacroGoal.isActive").value(true));

        UserProfile after =
                userProfileRepository.findById(created.userId()).orElseThrow();

        assertThat(after.getWeightKg()).isEqualTo(87.5);
        assertThat(after.getHeightCm()).isEqualTo(before.getHeightCm());
        assertThat(after.getName()).isEqualTo(before.getName());

        MacroGoal newActive =
                macroGoalRepository.findByUserProfile_IdAndIsActiveTrue(created.userId()).orElseThrow();

        assertThat(newActive.getId()).isNotEqualTo(created.activeGoalId());
    }

    @Test
    void validationError_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCreateRequestJson()))
                .andExpect(status().isBadRequest());
    }

    // helpers

    private Created createUser() throws Exception {
        String response = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequestJson()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode body = objectMapper.readTree(response);

        return new Created(
                body.at("/userProfile/id").asLong(),
                body.at("/activeMacroGoal/id").asLong()
        );
    }

    private String validCreateRequestJson() {
        return """
               {
                 "name": "Integration Test User",
                 "dateOfBirth": "1995-01-01",
                 "sex": "FEMALE",
                 "weightKg": 80.0,
                 "heightCm": 170.0,
                 "activityLevel": "MODERATE",
                 "goal": "MAINTAIN",
                 "intensity": "RECOMMENDED",
                 "bodyFatPercentage": 0.25
               }
               """;
    }

    private String validCreateRequestJsonWithWeight(double weight) {
        return """
               {
                 "name": "Integration Test User",
                 "dateOfBirth": "1995-01-01",
                 "sex": "FEMALE",
                 "weightKg": %s,
                 "heightCm": 170.0,
                 "activityLevel": "MODERATE",
                 "goal": "MAINTAIN",
                 "intensity": "RECOMMENDED",
                 "bodyFatPercentage": 0.25
               }
               """.formatted(weight);
    }

    private String invalidCreateRequestJson() {
        String future = LocalDate.now().plusDays(1).toString();
        return """
               {
                 "name": "Invalid",
                 "dateOfBirth": "%s",
                 "sex": "FEMALE"
               }
               """.formatted(future);
    }

    private record Created(long userId, long activeGoalId) {}
}
