package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.MessageDto;
import com.qiao.picturedepot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/messages")
    public List<MessageDto> getMessages(@AuthenticationPrincipal User user){
        return messageService.getMessageByReceiver(user.getId());
    }

    @GetMapping("/messages/unacknowledged-message-count")
    public Integer getUnacknowledgedMessageCount(@AuthenticationPrincipal User user){
        return messageService.getUnacknowledgedMessageCountByReceiver(user.getId());
    }

    @PostMapping("/messages/acknowledge")
    public void acknowledgeMessage(@RequestBody List<Long> messageIds, @AuthenticationPrincipal User user) {
        messageService.acknowledgeMessage(user.getId(), messageIds);
    }
}
