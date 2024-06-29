package com.diman.space.SpaceMissionsTrackerTry2;

import com.diman.space.SpaceMissionsTrackerTry2.controller.MissionController;
import com.diman.space.SpaceMissionsTrackerTry2.model.Mission;
import com.diman.space.SpaceMissionsTrackerTry2.service.MissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MissionController.class)
class MissionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MissionService service;

    @Test
    public void testGetAllMissions() throws Exception {
        Mission mission1 = new Mission(1L, "Apollo 11", LocalDate.of(1969, 7, 16), "Completed", "First manned Moon landing.");
        Mission mission2 = new Mission(2L, "Mars Rover", LocalDate.of(2020, 7, 30), "Ongoing", "Mars exploration rover.");

        List<Mission> missions = Arrays.asList(mission1, mission2);

        when(service.getAllMissions())
                .thenReturn(missions);

        mockMvc.perform(get("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(mission1.getName())))
                .andExpect(jsonPath("$[1].name", is(mission2.getName())));
    }

    @Test
    public void testGetMissionsByDate() throws Exception {
        Mission mission1 = new Mission(1L, "Apollo 11", LocalDate.of(1969, 7, 16), "Completed", "First manned Moon landing.");

        List<Mission> missions = List.of(mission1);

        when(service.searchMissionsByDate(any(LocalDate.class)))
                .thenReturn(missions);

        mockMvc.perform(get("/api/missions/search?date=1969-07-16")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(mission1.getName())));

        verify(service, times(0)).getAllMissions();
    }

    @Test
    public void testGetMissionByIdNotFound() throws Exception {
        when(service.getMissionById(1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/missions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }



    @Test
    public void testCreateMission() throws Exception {
        Mission mission = new Mission(1L, "Voyager 1", LocalDate.of(1977, 9, 5), "Completed", "Interstellar space probe.");

        when(service.saveMission(any(Mission.class)))
                .thenReturn(mission);

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Voyager 1\", \"launchDate\": \"1977-09-05\", \"status\":\"Completed\", \"description\":\"Interstellar space probe.\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/missions/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(mission.getName())))
                .andExpect(jsonPath("$.launchDate", is("1977-09-05")))
                .andExpect(jsonPath("$.status", is(mission.getStatus())))
                .andExpect(jsonPath("$.description", is(mission.getDescription())));
    }

}
