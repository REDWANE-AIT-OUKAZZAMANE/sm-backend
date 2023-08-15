package io.xhub.smwall.command;

import io.xhub.smwall.commands.UserAddCommand;
import io.xhub.smwall.constants.FormValidationCodes;
import io.xhub.smwall.constants.RoleName;
import io.xhub.smwall.domains.Authority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserAddCommandTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void should_not_throwAnyConstraintViolationException_when_validPayload() {
        Authority authority = new Authority("authorityId1", RoleName.ROLE_ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        System.out.println(authorities);
        UserAddCommand addCommand = new UserAddCommand(
                "valid firstName",
                "valid lastName",
                "Validemail@gmail.com",
                authorities
        );

        Set<ConstraintViolation<UserAddCommand>> violations = validator.validate(addCommand);

        assertEquals(0, violations.size());
    }

    @Test
    public void should_throwConstraintViolationException_when_invalidFirstName() {
        Authority authority = new Authority("authorityId1", RoleName.ROLE_ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        UserAddCommand addCommand = new UserAddCommand(
                "",
                "testUser",
                "testUser@gmail.com",
                authorities
        );

        Set<ConstraintViolation<UserAddCommand>> violations = validator.validate(addCommand);

        assertEquals(1, violations.size());

        ConstraintViolation<UserAddCommand> violation = violations.iterator().next();
        String violationMessage = violation.getMessage();

        assertEquals(FormValidationCodes.INVALID_USER_FIRST_NAME_PATTERN, violationMessage);
    }

    @Test
    public void should_throwConstraintViolationException_when_invalidLastName() {
        Authority authority = new Authority("authorityId1", RoleName.ROLE_ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        UserAddCommand addCommand = new UserAddCommand(
                "testUser",
                "",
                "testUser@gmail.com",
                authorities
        );

        Set<ConstraintViolation<UserAddCommand>> violations = validator.validate(addCommand);

        assertEquals(1, violations.size());

        ConstraintViolation<UserAddCommand> violation = violations.iterator().next();
        String violationMessage = violation.getMessage();

        assertEquals(FormValidationCodes.INVALID_USER_LAST_NAME_PATTERN, violationMessage);
    }

    @Test
    public void should_throwConstraintViolationException_when_invalidEmail() {
        Authority authority = new Authority("authorityId1", RoleName.ROLE_ADMIN);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        UserAddCommand addCommand = new UserAddCommand(
                "testUser",
                "testUser",
                "testUser.com",
                authorities
        );

        Set<ConstraintViolation<UserAddCommand>> violations = validator.validate(addCommand);

        assertEquals(1, violations.size());
    }
}
