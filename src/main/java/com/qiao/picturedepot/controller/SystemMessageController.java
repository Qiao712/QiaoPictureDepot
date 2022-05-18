package com.qiao.picturedepot.controller;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.SystemMessageDto;
import com.qiao.picturedepot.service.SystemMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SystemMessageController {
    @Autowired
    private SystemMessageService systemMessageService;

    @GetMapping("/system-messages")
    public List<SystemMessageDto> getSystemMessages(@AuthenticationPrincipal User user){
        return systemMessageService.getSystemMessageByReceiver(user.getId());
    }

    @GetMapping("/system-messages/unacknowledged-message-count")
    public Integer getUnacknowledgedSystemMessageCount(@AuthenticationPrincipal User user){
        return systemMessageService.getUnacknowledgedMessageCountByReceiver(user.getId());
    }

    @PostMapping("/system-messages/acknowledge")
    public void acknowledgeSystemMessage(@RequestBody List<Long> systemMessageIds, @AuthenticationPrincipal User user) {
        systemMessageService.acknowledgeSystemMessage(user.getId(), systemMessageIds);
    }
}
