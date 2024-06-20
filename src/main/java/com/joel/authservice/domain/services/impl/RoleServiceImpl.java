package com.joel.authservice.domain.services.impl;

import com.joel.authservice.domain.exceptions.RoleNotFoundException;
import com.joel.authservice.domain.models.RoleModel;
import com.joel.authservice.domain.enums.RoleType;
import com.joel.authservice.domain.repositories.RoleRepository;
import com.joel.authservice.domain.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {


    public static final String NO_ROLE_REGISTERED = "There is no role registered in the database";
    private final RoleRepository roleRepository;

    @Override
    public RoleModel findByRoleName(RoleType roleType) {
        return roleRepository.findByRoleName(roleType)
                .orElseThrow(() -> new RoleNotFoundException(NO_ROLE_REGISTERED));
    }
}
