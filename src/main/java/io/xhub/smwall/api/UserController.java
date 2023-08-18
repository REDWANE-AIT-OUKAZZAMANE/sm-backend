package io.xhub.smwall.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.xhub.smwall.commands.UserAddCommand;
import io.xhub.smwall.commands.UserUpdateCommand;
import io.xhub.smwall.constants.ApiPaths;
import io.xhub.smwall.dto.UserDTO;
import io.xhub.smwall.mappers.UserMapper;
import io.xhub.smwall.service.UserService;
import io.xhub.smwall.service.query.UserQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Api(tags = "User Management Resource")
@RestController
@RequestMapping(ApiPaths.V1 + ApiPaths.USERS)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserMapper userMapper;

    @ApiOperation(value = "List of users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') ")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(UserQuery userQuery, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                userService.getAllUsers(userQuery.buildPredicate(), pageable)
                        .map(userMapper::toDTO)
        );
    }

    @ApiOperation(value = "Update user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') ")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateCommand userUpdateCommand) {
        return ResponseEntity.ok()
                .body(userMapper.toDTO(userService.updateUser(userId, userUpdateCommand)));
    }

    @ApiOperation(value = "Create a new User")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') ")
    @PostMapping
    public ResponseEntity<UserDTO> CreateUser(@RequestBody @Valid UserAddCommand userAddCommand) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(userService.createUser(userAddCommand)));
    }

    @ApiOperation(value = "Delete user by ID")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') ")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Activate or Deactivate User Account")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') ")
    @PutMapping("/{userId}" + ApiPaths.USER_ACCOUNT_STATUS)
    public ResponseEntity<Void> toggleUserActivation(@PathVariable("userId") String userId) {
        userService.toggleUserActivation(userId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Send link to user by email to signUp")
    @PostMapping(ApiPaths.SIGNUP)
    public ResponseEntity<Void> sendLinkToSignUp(@RequestBody List<String> userIds) throws MessagingException, IOException {
        userService.sendLinkToUserToSetPassword(userIds);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Send link to user by email to reset password")
    @PostMapping(ApiPaths.RESET_PASSWORD + "/{email}")
    public ResponseEntity<Void> sendLinkToResetPassword(@PathVariable String email) throws MessagingException, IOException {
        userService.sendLinkToUserToResetPassword(email);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "check if user already signed up")
    @PostMapping(ApiPaths.SIGNED_UP + "/{email}")
    public ResponseEntity<Void> signedUpCheck(@PathVariable String email){
        userService.userAlreadySignedUp(email);
        return ResponseEntity.ok().build();
    }

}
