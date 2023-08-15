package io.xhub.smwall.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xhub.smwall.commands.UserAddCommand;
import io.xhub.smwall.commands.UserUpdateCommand;
import io.xhub.smwall.constants.ApiClientErrorCodes;
import io.xhub.smwall.constants.ApiPaths;
import io.xhub.smwall.constants.RoleName;
import io.xhub.smwall.domains.Authority;
import io.xhub.smwall.domains.User;
import io.xhub.smwall.exceptions.BusinessException;
import io.xhub.smwall.service.UserService;
import io.xhub.smwall.utlis.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    UserUpdateCommand userUpdateCommand;

    UserAddCommand addCommand;

    @Test
    public void should_updateUser_when_userExists() throws Exception {

        User user = new User();

        user.setId("123");
        user.setEmail("email@gmail.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");

        Authority adminAuthority = new Authority(
                "6466001f342fd96df0b30fae",
                RoleName.ROLE_ADMIN
        );
        Set<Authority> authorities = new HashSet<>();
        authorities.add(adminAuthority);
        userUpdateCommand = new UserUpdateCommand(
                "NewFirstName",
                "NewLastName",
                "new-email@gmail.com",
                authorities
        );

        when(userService.updateUser(user.getId(), userUpdateCommand)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateCommand)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService, times(1))
                .updateUser(anyString(), any(UserUpdateCommand.class));

    }

    @Test
    public void should_throwBusinessException_when_userDoesNotExist() throws Exception {

        String invalidId = "invalidId";

        when(userService.updateUser(anyString(), any(UserUpdateCommand.class)))
                .thenThrow(new BusinessException(ApiClientErrorCodes.USER_NOT_FOUND.getErrorMessage()));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        verify(userService, times(0))
                .updateUser(anyString(), any(UserUpdateCommand.class));
    }

    @Test
    public void should_get_allUsers() throws Exception {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId("1");
        user1.setFirstName("First Name 1");
        user1.setLastName("Last Name 1");

        User user2 = new User();
        user2.setId("2");
        user2.setFirstName("First Name 2");
        user2.setLastName("Last Name 2");

        userList.add(user1);
        userList.add(user2);

        when(userService.getAllUsers(any(), any())).thenReturn(new PageImpl<>(userList));

        mockMvc.perform(get(ApiPaths.V1 + ApiPaths.USERS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers(any(), any());
    }

    @Test
    public void should_get_empty_user_list() throws Exception {
        List<User> emptyUserList = Collections.emptyList();
        Page<User> emptyUserPage = new PageImpl<>(emptyUserList);

        when(userService.getAllUsers(any(), any())).thenReturn(emptyUserPage);

        mockMvc.perform(get(ApiPaths.V1 + ApiPaths.USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void should_deleteUser_when_userExists() throws Exception {
        User user = new User();
        user.setId("userId");

        mockMvc.perform(delete(ApiPaths.V1 + ApiPaths.USERS + "/" + user.getId()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(user.getId());
    }

    @Test
    public void should_throwBusinessException_when_deleting_user_with_invalidId() throws Exception {
        String invalidId = "invalidId";

        doThrow(new BusinessException(ApiClientErrorCodes.USER_NOT_FOUND.getErrorMessage()))
                .when(userService).deleteUserById(invalidId);

        mockMvc.perform(delete(ApiPaths.V1 + ApiPaths.USERS + "/" + invalidId))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).deleteUserById(invalidId);
    }


    @Test
    public void should_toggleUserActivation_when_userExist() throws Exception {
        String userId = "testId";
        mockMvc.perform(MockMvcRequestBuilders.put(ApiPaths.V1 + ApiPaths.USERS + "/" + userId + ApiPaths.USER_ACCOUNT_STATUS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).toggleUserActivation(userId);
    }

    @Test
    public void should_createNewUser_when_payloadIsValid() throws Exception {
        Authority authority = new Authority("authorityId1", RoleName.ROLE_ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        addCommand = new UserAddCommand(
                "Valid firstName",
                "Valid lastName",
                "Validemail@gmail.com",
                authorities
        );
        when(userService.createUser(addCommand)).thenReturn(any(User.class));

        mockMvc.perform(post(ApiPaths.V1 + ApiPaths.USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJsonString(addCommand)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(UserAddCommand.class));
    }
}


