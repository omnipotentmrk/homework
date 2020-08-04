package com.kakao.pay.api.contorller;

import com.kakao.pay.api.exception.ApiRuntimeException;
import com.kakao.pay.api.model.SpreadMoneyDistribution;
import com.kakao.pay.api.model.SpreadMoneyEvent;
import com.kakao.pay.api.model.SpreadMoneyStatus;
import com.kakao.pay.api.model.User;
import com.kakao.pay.api.service.SpreadMoneyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/spread-money", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpreadMoneyController {
    private final SpreadMoneyService spreadMoneyService;

    public SpreadMoneyController(SpreadMoneyService spreadMoneyService) {
        this.spreadMoneyService = spreadMoneyService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/event", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SpreadMoneyEvent request(User user, @RequestBody SpreadMoneyEvent spreadMoneyEvent) {
        if (!spreadMoneyEvent.isValidRequestParam()) {
            throw new ApiRuntimeException(HttpStatus.BAD_REQUEST);
        }

        spreadMoneyEvent.setUserId(user.getId());
        spreadMoneyEvent.setRoomId(user.getRoomId());

        return spreadMoneyService.createEvent(spreadMoneyEvent);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/receive")
    public SpreadMoneyDistribution receive(User user) {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(user);

        spreadMoneyService.checkValidateReceive(user, spreadMoneyStatus);

        return spreadMoneyService.receive(user, spreadMoneyStatus);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public SpreadMoneyStatus getStatus(User user) {
        SpreadMoneyStatus spreadMoneyStatus = spreadMoneyService.getSpreadMoneyStatus(user);

        spreadMoneyService.checkValidateStatus(user, spreadMoneyStatus);

        return spreadMoneyStatus;
    }
}
