package freshtrash.freshtrashbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.config.TestSecurityConfig;
import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import freshtrash.freshtrashbackend.service.LocalFileService;
import freshtrash.freshtrashbackend.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberApi.class)
@Import(TestSecurityConfig.class)
class MemberApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @MockBean
    private LocalFileService localFileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 정보 조회")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_memberPrincipal_when_getMember_then_returnMemberResponse() throws Exception {
        Long memberId = 123L;
        Member member = Fixture.createMember();
        // given
        given(memberService.getMember(eq(memberId))).willReturn(member);
        // when
        mvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.rating").value(member.getRating()))
                .andExpect(jsonPath("$.fileName").value(member.getFileName()))
                .andExpect(
                        jsonPath("$.address.zipcode").value(member.getAddress().getZipcode()))
                .andExpect(jsonPath("$.address.state").value(member.getAddress().getState()))
                .andExpect(jsonPath("$.address.city").value(member.getAddress().getCity()))
                .andExpect(
                        jsonPath("$.address.district").value(member.getAddress().getDistrict()))
                .andExpect(
                        jsonPath("$.address.detail").value(member.getAddress().getDetail()));
        // then
    }

    @Test
    @DisplayName("유저 정보 수정")
    @WithUserDetails(value = "testUser@gmail.com", setupBefore = TEST_EXECUTION)
    void given_memberRequestAndImgFile_when_updateMember_then_returnUpdatedMemberValues() throws Exception {
        // given
        Long memberId = 123L;
        String oldFile = "oldFile.png";
        MockMultipartFile imgFile = Fixture.createMultipartFile("test_image");
        MemberRequest memberRequest = new MemberRequest("user111", Fixture.createAddress());
        Member member = Fixture.createLoginMember();
        member.setNickname(memberRequest.nickname());
        member.setAddress(memberRequest.address());
        member.setFileName(imgFile.getOriginalFilename());
        given(memberService.findFileNameOfMember(eq(memberId))).willReturn(new FileNameSummary(oldFile));
        given(memberService.updateMember(
                        any(MemberPrincipal.class), any(MemberRequest.class), any(MultipartFile.class)))
                .willReturn(member);
        willDoNothing().given(localFileService).deleteFileIfExists(eq(oldFile));
        // when
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/members")
                        .file("imgFile", imgFile.getBytes())
                        .file(new MockMultipartFile(
                                "memberRequest", "", "application/json", objectMapper.writeValueAsBytes(memberRequest)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(memberRequest.nickname()))
                .andExpect(jsonPath("$.fileName").value(imgFile.getOriginalFilename()))
                .andExpect(jsonPath("$.address.zipcode")
                        .value(memberRequest.address().getZipcode()))
                .andExpect(jsonPath("$.address.state")
                        .value(memberRequest.address().getState()))
                .andExpect(
                        jsonPath("$.address.city").value(memberRequest.address().getCity()))
                .andExpect(jsonPath("$.address.district")
                        .value(memberRequest.address().getDistrict()))
                .andExpect(jsonPath("$.address.detail")
                        .value(memberRequest.address().getDetail()));
        // then
    }
}