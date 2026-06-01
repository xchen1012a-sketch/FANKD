package com.fankeda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FankedaApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginReturnsDemoTokenAndUserProfile() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"wx-demo-code\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.msg", is("ok")))
            .andExpect(jsonPath("$.data.token", not(is(""))))
            .andExpect(jsonPath("$.data.user.id", is(1)))
            .andExpect(jsonPath("$.data.user.openid", is("mock-openid-wx-demo-code")));
    }

    @Test
    void rootPathReturnsApiIndex() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.name", is("饭刻达 MVP API")))
            .andExpect(jsonPath("$.data.endpoints", hasItem("GET /api/stalls")));
    }

    @Test
    void unknownRouteReturnsNotFoundApiResponse() throws Exception {
        mockMvc.perform(get("/missing-route"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(404)))
            .andExpect(jsonPath("$.msg", is("接口不存在")));
    }

    @Test
    void stallsReturnSeedDataWithLatestQueueSnapshot() throws Exception {
        mockMvc.perform(get("/api/stalls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data", hasSize(6)))
            .andExpect(jsonPath("$.data[0].stallId", is(1)))
            .andExpect(jsonPath("$.data[0].name", is("快餐米饭")))
            .andExpect(jsonPath("$.data[0].queueCount", notNullValue()))
            .andExpect(jsonPath("$.data[0].waitTime", notNullValue()))
            .andExpect(jsonPath("$.data[0].level", not(is(""))));
    }

    @Test
    void recommendRanksTheFastestWindowAndRushModeFiltersSlowOptions() throws Exception {
        mockMvc.perform(get("/api/recommend").param("rushMode", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.best.stallId", is(5)))
            .andExpect(jsonPath("$.data.best.name", is("包子粥铺")))
            .andExpect(jsonPath("$.data.stalls", hasSize(6)))
            .andExpect(jsonPath("$.data.stalls[0].score", notNullValue()));

        mockMvc.perform(get("/api/recommend").param("rushMode", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.best.totalTime").value(closeTo(4.04, 0.2)))
            .andExpect(jsonPath("$.data.stalls[?(@.stallId == 4)]", empty()));
    }

    @Test
    void queueReportUpdatesLatestSnapshot() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(post("/api/queue/report")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stallId\":5,\"queueCount\":18}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stallId", is(5)))
            .andExpect(jsonPath("$.data.queueCount", is(18)));

        mockMvc.perform(get("/api/queue/latest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[?(@.stallId == 5)].queueCount", hasItem(18)));
    }

    @Test
    void inaccurateFeedbackCalibratesServeSpeedDown() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(post("/api/feedback")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stallId\":6,\"isAccurate\":false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stallId", is(6)))
            .andExpect(jsonPath("$.data.isAccurate", is(false)))
            .andExpect(jsonPath("$.data.adjustedServeSpeed").value(closeTo(3.08, 0.001)));

        mockMvc.perform(get("/api/stalls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[?(@.stallId == 6)].serveSpeed", hasItem(3.08)));
    }

    @Test
    void userProfileRequiresTokenAndCanUpdateClassEndTime() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code", is(401)));

        String token = loginAndGetToken();

        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"classEndTime\":\"12:05\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.classEndTime", is("12:05")));

        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.openid", is("mock-openid-wx-demo-code")))
            .andExpect(jsonPath("$.data.classEndTime", is("12:05")));
    }

    @Test
    void invalidRequestsReturnBadRequestApiResponse() throws Exception {
        mockMvc.perform(post("/api/queue/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stallId\":5,\"queueCount\":-1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.msg", not(is(""))));
    }

    @Test
    void adminCanCreateUpdateAndDeleteStall() throws Exception {
        mockMvc.perform(post("/api/admin/stalls")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "测试窗口",
                      "type": "套餐",
                      "posX": 12,
                      "posY": 20,
                      "serveSpeed": 2.4,
                      "distance": 80,
                      "avgPrep": 3.2,
                      "rating": 4.5
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.stallId", is(7)))
            .andExpect(jsonPath("$.data.name", is("测试窗口")))
            .andExpect(jsonPath("$.data.queueCount", is(0)));

        mockMvc.perform(put("/api/admin/stalls/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "测试窗口升级",
                      "type": "盖饭",
                      "posX": 14,
                      "posY": 22,
                      "serveSpeed": 2.8,
                      "distance": 90,
                      "avgPrep": 2.9,
                      "rating": 4.7
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stallId", is(7)))
            .andExpect(jsonPath("$.data.name", is("测试窗口升级")))
            .andExpect(jsonPath("$.data.serveSpeed").value(closeTo(2.8, 0.001)));

        mockMvc.perform(get("/api/stalls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[?(@.stallId == 7)].name", hasItem("测试窗口升级")));

        mockMvc.perform(delete("/api/admin/stalls/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data", is(true)));

        mockMvc.perform(get("/api/stalls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[?(@.stallId == 7)]", empty()));
    }

    @Test
    void adminStallValidationRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/admin/stalls")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "",
                      "type": "套餐",
                      "posX": 12,
                      "posY": 20,
                      "serveSpeed": 0,
                      "distance": -1,
                      "avgPrep": 3.2,
                      "rating": 6
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.msg", not(is(""))));
    }

    @Test
    void adminCanListFeedbackAndUsers() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(post("/api/feedback")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stallId\":6,\"isAccurate\":false}"))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/queue/report")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stallId\":5,\"queueCount\":18}"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/feedback"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].stallId", is(6)))
            .andExpect(jsonPath("$.data[0].stallName", is("预制套餐")))
            .andExpect(jsonPath("$.data[0].isAccurate", is(false)))
            .andExpect(jsonPath("$.data[0].userNickname", is("饭刻达用户")));

        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].openid", is("mock-openid-wx-demo-code")))
            .andExpect(jsonPath("$.data[0].reportCount", is(1)));
    }

    private String loginAndGetToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"wx-demo-code\"}"))
            .andExpect(status().isOk())
            .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
        assertThat(token).isNotBlank();
        return token;
    }
}
