package io.xhub.smwall.service;


import com.querydsl.core.types.Predicate;
import io.xhub.smwall.commands.UserAddCommand;
import io.xhub.smwall.commands.UserUpdateCommand;
import io.xhub.smwall.constants.RoleName;
import io.xhub.smwall.domains.Authority;
import io.xhub.smwall.domains.User;
import io.xhub.smwall.exceptions.BusinessException;
import io.xhub.smwall.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User user;
    private UserUpdateCommand userUpdateCommand;
    private UserAddCommand addCommand;

    @BeforeEach
    void setup() {
        Authority authority1 = new Authority();
        authority1.setId("authorityId1");
        authority1.setName(RoleName.ROLE_ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority1);
        addCommand = new UserAddCommand(
                "testUser",
                "test",
                "testUser@gmail.com",
                authorities
        );
        user = new User();
        user.setId("123");
        user.setFirstName("user1");
        user.setLastName("user2");
        user.setEmail("user@email.com");
        user.setAuthorities(authorities);

        Authority adminAuthority = new Authority(
                "6466001f342fd96df0b30fae",
                RoleName.ROLE_ADMIN
        );
        authorities.add(adminAuthority);
        userUpdateCommand = new UserUpdateCommand(
                "newFirstName",
                "newLastName",
                "newEmail@gmail.com",
                authorities
        );
    }
    @Test
    public void should_returnAllUsers_withoutPredicate() {
        Pageable pageable = mock(Pageable.class);
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);
        Page<User> expectedPage = new PageImpl<>(users);
        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<User> resultPage = userService.getAllUsers(null, pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        assertTrue(resultPage.getContent().containsAll(users));
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void should_returnFilteredUsers_withPredicate() {
        Predicate predicate = mock(Predicate.class);
        Pageable pageable = mock(Pageable.class);
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);
        Page<User> expectedPage = new PageImpl<>(users);
        when(userRepository.findAll(predicate, pageable)).thenReturn(expectedPage);

        Page<User> resultPage = userService.getAllUsers(predicate, pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        assertTrue(resultPage.getContent().containsAll(users));
        verify(userRepository, times(1)).findAll(predicate, pageable);
    }

    @Test
    public void should_returnUserById_when_exists() {
        when(userRepository.findById(this.user.getId())).thenReturn(Optional.ofNullable(this.user));
        User resultUser = userService.getUserById(this.user.getId());
        assertNotNull(resultUser);
        assertSame(this.user, resultUser);
        verify(userRepository, times(1)).findById(this.user.getId());
    }

    @Test
    public void should_throwBusinessException_when_idDoesNotExist() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.getUserById(anyString()));
        verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    public void should_updateUser_when_userExists() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user.getId(), userUpdateCommand);

        assertEquals(user, result);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), userUpdateCommand.getEmail());
        assertEquals(user.getFirstName(), userUpdateCommand.getFirstName());
        assertEquals(user.getLastName(), userUpdateCommand.getLastName());

        verify(userRepository, times(1)).findById(user.getId());
    }


    @Test
    public void should_throwBusinessException_when_userDoesNotExist() {

        String invalidId = "invalidId";

        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.updateUser(invalidId, userUpdateCommand));
    }

    @Test
    public void should_toggleUserActivation_when_userExist() {
        User user1 = new User();
        user1.setId("testId");
        user1.setActivated(true);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        userService.toggleUserActivation(user1.getId());

        assertFalse(user1.isActivated());

        verify(userRepository).save(user1);
    }

    @Test
    void should_createUser_when_userIsValid() {
        User expectedUser = User.create(addCommand);

        when(userRepository.save(any())).thenReturn(expectedUser);
        User result = userService.createUser(addCommand);

        assertNotNull(result);
        Assertions.assertEquals(expectedUser, result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void should_deleteUser_when_userExists() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUserById(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void should_throwBusinessException_when_deleting_user_with_invalidId() {

        String invalidId = "invalidId";

        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.deleteUserById(invalidId));

        verify(userRepository, never()).delete(any(User.class));
    }
}
