package io.xhub.smwall.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.xhub.smwall.constants.ApiPaths;
import io.xhub.smwall.dto.AuthorityDTO;
import io.xhub.smwall.mappers.AuthorityMapper;
import io.xhub.smwall.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Authority Management Resource")
@RestController
@RequestMapping(ApiPaths.V1 + ApiPaths.AUTHORITIES)
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AuthorityController {
    private final AuthorityService authorityService;
    private final AuthorityMapper authorityMapper;

    @ApiOperation(value = "List of authorities")
    @GetMapping
    public ResponseEntity<List<AuthorityDTO>> getAllAuthorities() {
        return ResponseEntity.ok(authorityService.getAllAuthorities()
                .stream()
                .map(authorityMapper::toDTO)
                .collect(Collectors.toList()));
    }
}
