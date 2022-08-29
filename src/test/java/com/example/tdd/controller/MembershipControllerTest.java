package com.example.tdd.controller;

import com.example.tdd.common.GlobalExceptionHandler;
import com.example.tdd.domain.dto.MembershipDetailResponse;
import com.example.tdd.domain.dto.MembershipRequest;
import com.example.tdd.domain.dto.MembershipAddResponse;
import com.example.tdd.domain.enums.MembershipType;
import com.example.tdd.exception.MembershipErrorResult;
import com.example.tdd.exception.MembershipException;
import com.example.tdd.service.MembershipService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.example.tdd.constants.MembershipConstants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
public class MembershipControllerTest {

    @InjectMocks
    private MembershipController target;
    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void mockMvc가Null이아님() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .build();
        assertThat(target).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                .content(gson.toJson(membershipRequest(1000, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버십등록실패_포인트가null() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                .header(USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(null, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_포인트가음수() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequest(-1, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_멤버십종류가Null() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequest(10000, null)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_MemberService에서에러Throw() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", MembershipType.NAVER, 10000);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                .header(USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버십등록성공() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        final MembershipAddResponse membershipAddResponse = MembershipAddResponse.builder()
                .id(-1L)
                .membershipType(MembershipType.NAVER).build();

        doReturn(membershipAddResponse).when(membershipService).addMembership("12345", MembershipType.NAVER, 10000);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                .header(USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated());

        final MembershipAddResponse response = gson.fromJson(resultActions.andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8), MembershipAddResponse.class);

        assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(response.getId()).isNotNull();
    }
    
    @Test
    public void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        
        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );
        
        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십목록조회성공() throws Exception {
        // given
        final String url = "/api/v1/memberships";
        doReturn(Arrays.asList(
            MembershipDetailResponse.builder().build(),
            MembershipDetailResponse.builder().build(),
            MembershipDetailResponse.builder().build()
        )).when(membershipService).getMembershipList("12345");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(USER_ID_HEADER, "12345")
        );

        // then
        resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void 멤버십상세조회실패_사용자식별값이헤더에없음() throws Exception {
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(url)
        );
        
        // then
        resultActions.andExpect(status().isBadRequest());
    }
    
    @Test
    public void 멤버십상세조회실패_멤버십이없음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";
        doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
            .when(membershipService)
            .getMembership(-1L, "12345");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(url).header(USER_ID_HEADER, "12345")
        );

        // then
        resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void 멤버십상세조회성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";
        doReturn(MembershipDetailResponse.builder().build()).when(membershipService).getMembership(-1L,"12345");
        
        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(USER_ID_HEADER, "12345")
                .param("membershipType", MembershipType.NAVER.name())
        );
        
        // then
        resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void 멤버십삭제실패_사용자식별값이없음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
        );
        
        // then
        resultActions.andExpect(status().isBadRequest());
    }
    
    @Test
    public void 멤버십삭제성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";
        
        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
                .header(USER_ID_HEADER, "12345")
        );
        
        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void 멤버십적립실패_사용자식별값없음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1/accumulate";

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .content(gson.toJson(membershipRequest(10000)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십적립실패_포인트가음수() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1/accumulate";

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .header(USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(-10000, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십적립성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1/accumulate";

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .header(USER_ID_HEADER, "12345")
                .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    private MembershipRequest membershipRequest(final Integer point, final MembershipType membershipType) {
        return MembershipRequest.builder()
            .point(point)
            .membershipType(membershipType)
            .build();
    }

    private MembershipRequest membershipRequest(final Integer point) {
        return MembershipRequest.builder()
            .point(point)
            .build();
    }
}
