package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelCreateRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.InviteCodeDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelSpecificResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelStatusResponse;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.INVITEDALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.MADEALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.SHOWALL;
import static java.util.Comparator.comparing;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

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
    public Api<List<ChannelShowAllResponse>> findChannelsByRole(
            @MemberId Long memberId,
            @RequestParam("tab") String channelFilter,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
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

        List<ChannelShowAllResponse> channelShowAllResponses = channelService.findAllMyChannels(memberId, channelEnum);

        if (sort.equals("latest")) {
            channelShowAllResponses.sort(comparing(ChannelShowAllResponse::getCreatedAt).reversed());
        } else if (sort.equals("oldest")) {
            channelShowAllResponses.sort(comparing(ChannelShowAllResponse::getCreatedAt));
        } else {
            throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다. latest 또는 oldest 중 1개를 요청해주세요.");
        }

        return Api.OK(channelShowAllResponses);
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
