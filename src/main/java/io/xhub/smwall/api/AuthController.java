package io.xhub.smwall.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.xhub.smwall.commands.LoginCommand;
import io.xhub.smwall.config.JwtProperties;
import io.xhub.smwall.constants.ApiPaths;
import io.xhub.smwall.constants.CookiesNames;
import io.xhub.smwall.dto.UserDTO;
import io.xhub.smwall.mappers.UserMapper;
import io.xhub.smwall.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Authentication Resource")
@RestController
@RequestMapping(ApiPaths.V1 + ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final JwtProperties jwtProperties;
    private final AuthService authService;
    private final UserMapper userMapper;

    @ApiOperation(value = "Authenticate a user")
    @PostMapping(ApiPaths.LOGIN)
    public ResponseEntity<Void> authenticate(@Valid @RequestBody LoginCommand loginCommand, HttpServletResponse response) {
        String jwt = authService.authenticate(loginCommand);

        // Set the JWT as a cookie
        Cookie cookie = new Cookie(CookiesNames.JWT_KEY, jwt);
        cookie.setMaxAge((int) jwtProperties.getTokenValidity());
        cookie.setPath("/api/v1");
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Logout the current user")
    @PostMapping(ApiPaths.LOGOUT)
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Remove the JWT cookie by setting its expiration to 0
        Cookie cookie = new Cookie(CookiesNames.JWT_KEY, null);
        cookie.setMaxAge(0);
        cookie.setPath("/api/v1");
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get current user")
    @GetMapping(ApiPaths.CURRENT)
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok()
                .body(userMapper.toDTO(authService.getCurrentUser()));
    }

    @ApiOperation(value = "Sign up or update password")
    @PatchMapping(ApiPaths.UPDATE_PASSWORD)
    public ResponseEntity<Void> updatePassword(@RequestBody LoginCommand loginCommand) {
        authService.updatePassword(loginCommand.getEmail(), loginCommand.getPassword());

        return ResponseEntity.ok().build();
    }
}
