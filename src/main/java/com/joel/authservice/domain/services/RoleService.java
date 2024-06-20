package com.joel.authservice.domain.services;

import com.joel.authservice.domain.models.RoleModel;
import com.joel.authservice.domain.enums.RoleType;

public interface RoleService {

    RoleModel findByRoleName(RoleType roleType);

}
