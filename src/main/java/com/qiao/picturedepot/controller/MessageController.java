package com.qiao.picturedepot.controller;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.AuthUserDto;
import com.qiao.picturedepot.pojo.dto.MessageDto;
import com.qiao.picturedepot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/messages")
    public PageInfo<MessageDto> getMessages(@RequestParam("pageNo") Integer pageNo,
                                            @RequestParam("pageSize") Integer pageSize,
                                            @AuthenticationPrincipal AuthUserDto user){
        return messageService.getMessageByReceiver(user.getId(), pageNo, pageSize);
    }

    @GetMapping("/messages/unacknowledged-message-count")
    public Integer getUnacknowledgedMessageCount(@AuthenticationPrincipal AuthUserDto user){
        return messageService.getUnacknowledgedMessageCountByReceiver(user.getId());
    }

    @PostMapping("/messages/acknowledge")
    public void acknowledgeMessages(@RequestBody List<Long> messageIds, @AuthenticationPrincipal AuthUserDto user) {
        messageService.acknowledgeMessages(user.getId(), messageIds);
    }

    @PostMapping("/messages/acknowledge-before/{time}")
    public void acknowledgeMessagesBefore(@PathVariable("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time, @AuthenticationPrincipal User user){
        messageService.acknowledgeMessagesBefore(user.getId(), time);
    }
}
