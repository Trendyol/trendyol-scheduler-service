package com.trendyol.scheduler.controller;

import com.trendyol.scheduler.controller.sample.SampleJobController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SampleJobController.class)
public class SampleJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void it_should_invoke_sample_job_endpoint() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(get("/job/invoke"));

        //Then
        resultActions.andExpect(status().isOk());
    }
}