package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.common.pagination.Pagination;
import com.dnd12th_4.pickitalki.controller.channel.dto.InviteCodeDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.request.ChannelCreateRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelNameUpdateResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelSpecificResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelStatusResponse;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.INVITEDALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.MADEALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.SHOWALL;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PutMapping("/{channelId}/channelName")
    public ResponseEntity<ChannelNameUpdateResponse> updateChannelName(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestParam("channelName") @Valid String channelName
    ) {
        String updatedName = channelService.updateChannelName(memberId, channelId, channelName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ChannelNameUpdateResponse.builder()
                        .updatedName(updatedName)
                        .build());
    }

    @PostMapping
    public ResponseEntity<ChannelResponse> makeChannel(
            @MemberId Long memberId,
            @RequestBody ChannelCreateRequest channelCreateRequest
    ) {
        ChannelResponse channelResponse = channelService.save(memberId,
                channelCreateRequest.channelName(), channelCreateRequest.codeName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelResponse);
    }


    @GetMapping("/{channelId}/status")
    public Api<ChannelStatusResponse> findChannelStatus(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        ChannelStatusResponse channelStatus = channelService.findChannelStatus(memberId, channelId);

        return Api.OK(channelStatus);
    }


    @GetMapping("/inviteCode")
    public ResponseEntity<InviteCodeDto> findChannelInviteCode(
            @MemberId Long memberId,
            @RequestParam("channelName") @Valid String channelName
    ) {
        String inviteCode = channelService.findInviteCode(memberId, channelName);
        return ResponseEntity.ok(new InviteCodeDto(inviteCode));
    }

    @GetMapping
    public Api<ChannelSpecificResponse> findChannelByName(
            @MemberId Long memberId,
            @RequestParam(value = "channelName") String channelName
    ) {
        ChannelSpecificResponse channelSpecificResponse = channelService.findChannelByChannelName(memberId, channelName);

        return Api.OK(channelSpecificResponse);
    }

    @GetMapping("/{channelId}")
    public Api<ChannelSpecificResponse> findChannelById(
            @MemberId Long memberId,
            @PathVariable(value = "channelId") String channelId
    ) {
        ChannelSpecificResponse channelSpecificResponse = channelService.findChannelByChannelId(memberId, channelId);

        return Api.OK(channelSpecificResponse);
    }

    @GetMapping("/channel-profile")
    public Api<ChannelShowAllResponse> findChannelsByRole(
            @Parameter(hidden = true) @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @RequestParam(value = "tab", defaultValue = "all") String channelFilter,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        ChannelControllerEnums channelEnum = getChannelEnumFromFilter(channelFilter);

        Pageable pageable = Pagination.validateGetPage(sort, pageParamRequest);
        ChannelShowAllResponse channelShowAllResponse =  channelService.findAllMyChannels(memberId, channelEnum, pageable);
        return Api.OK(channelShowAllResponse);
    }

    private static ChannelControllerEnums getChannelEnumFromFilter(String channelFilter) {
        ChannelControllerEnums channelEnum;
        if (channelFilter.equals("all")) {
            channelEnum = SHOWALL;
        } else if (channelFilter.equals("my-channel")) {
            channelEnum = MADEALL;
        } else if (channelFilter.equals("invited-channel")) {
            channelEnum = INVITEDALL;
        } else {
            throw new IllegalArgumentException("지원하지 않는 파라미터입니다. all, my-channel, invited-channel 중 1개를 요청헤주세요");
        }
        return channelEnum;
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<String> deleteChannel(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        channelService.deleteChannel(memberId, channelId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
