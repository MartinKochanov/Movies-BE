package com.mk.movies.domain.role.service;

import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleRequest;
import com.mk.movies.domain.role.dto.RoleUpdateRequest;
import com.mk.movies.domain.role.dto.RoleView;
import com.mk.movies.domain.role.repository.RoleRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Role create(RoleRequest roleRequest, ObjectId movieId) {
        var role = roleMapper.toDocument(roleRequest);
        role.setMovieId(movieId);
        return roleRepository.save(role);
    }

    public RoleView updateRole(ObjectId id, RoleUpdateRequest roleRequest) {
        var role = getRole(id);

        roleMapper.updateDocument(roleRequest, role);

        return roleMapper.toView(roleRepository.save(role));

    }

    public void deleteRole(ObjectId id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found");
        }

        roleRepository.deleteById(id);
    }

    private Role getRole(ObjectId id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }
}
