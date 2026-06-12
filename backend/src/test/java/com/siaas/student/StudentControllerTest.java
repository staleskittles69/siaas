package com.siaas.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void dashboard_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/student/dashboard"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void dashboard_withStudentAuth_returns200WithCgpa() throws Exception {
        String loginBody = """
            {"email":"student@siaas.dev","password":"Student@123"}
            """;
        MvcResult loginResult = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

        String cookie = loginResult.getResponse().getHeader("Set-Cookie");

        mvc.perform(get("/api/v1/student/dashboard")
                .header("Cookie", cookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.cgpa").isNumber())
            .andExpect(jsonPath("$.data.subjects").isArray())
            .andExpect(jsonPath("$.data.subjects.length()").value(6));
    }

    @Test
    void profile_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/student/profile"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void profile_withStudentAuth_returns200WithName() throws Exception {
        String loginBody = """
            {"email":"student@siaas.dev","password":"Student@123"}
            """;
        MvcResult loginResult = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

        String cookie = loginResult.getResponse().getHeader("Set-Cookie");

        mvc.perform(get("/api/v1/student/profile")
                .header("Cookie", cookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.fullName").isString())
            .andExpect(jsonPath("$.data.rollNumber").isString());
    }
}
