package com.uberclocked.api.wheel.controller;

import com.uberclocked.api.wheel.model.dto.WheelDto;
import com.uberclocked.api.wheel.service.WheelService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.ZoneId;
import java.util.UUID;

@RestController
@RequestMapping("/wheel")
public class WheelController {

    private final WheelService wheelService;

    public WheelController(WheelService wheelService) {
        this.wheelService = wheelService;
    }

    @GetMapping("/status")
    public WheelDto.WheelStatusResponse status(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return wheelService.status(userId, ZoneId.of("America/Argentina/Buenos_Aires"));
    }

    @PostMapping("/spin")
    public WheelDto.WheelSpinResponse spin(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return wheelService.spin(userId, ZoneId.of("America/Argentina/Buenos_Aires"));
    }
}