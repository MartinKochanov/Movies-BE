package com.mk.movies.domain.role.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mk.movies.domain.role.document.Role;
import com.mk.movies.domain.role.dto.RoleRequest;
import com.mk.movies.domain.role.dto.RoleUpdateRequest;
import com.mk.movies.domain.role.dto.RoleView;
import com.mk.movies.domain.role.repository.RoleRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.RoleMapper;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleRequest roleRequest;
    private RoleUpdateRequest roleUpdateRequest;
    private RoleView roleView;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(new ObjectId());
        role.setName("Role");
        role.setCastId(new ObjectId());
        role.setMovieId(new ObjectId());

        roleRequest = new RoleRequest("Role", role.getCastId(), role.getMovieId());

        roleUpdateRequest = new RoleUpdateRequest("Updated Role", null);

        roleView = new RoleView(role.getId().toHexString(), role.getName());
    }

    @Test
    void create_returnsRole_whenRoleIsSaved() {
        when(roleMapper.toDocument(roleRequest)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        var result = roleService.create(roleRequest, role.getMovieId());

        assertEquals(role, result);
        verify(roleMapper).toDocument(roleRequest);
        verify(roleRepository).save(role);
    }

    @Test
    void updateRole_returnsUpdatedRoleView_whenRoleIsUpdated() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toView(role)).thenReturn(roleView);

        var result = roleService.updateRole(role.getId(), roleUpdateRequest);

        assertEquals(roleView, result);
        verify(roleRepository).findById(role.getId());
        verify(roleMapper).updateDocument(roleUpdateRequest, role);
        verify(roleRepository).save(role);
        verify(roleMapper).toView(role);
    }

    @Test
    void deleteRole_deletesRole_whenRoleExists() {
        when(roleRepository.existsById(role.getId())).thenReturn(true);

        roleService.deleteRole(role.getId());

        verify(roleRepository).deleteById(role.getId());
    }

    @Test
    void deleteRole_throwsResourceNotFoundException_whenRoleDoesNotExist() {
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(role.getId()));
    }

    @Test
    void getRole_throwsResourceNotFoundException_whenRoleDoesNotExist() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> roleService.updateRole(role.getId(), roleUpdateRequest));
    }
}