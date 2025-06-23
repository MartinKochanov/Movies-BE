package com.mk.movies.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.dto.UserRequest;
import com.mk.movies.domain.user.dto.UserUpdateRequest;
import com.mk.movies.domain.user.dto.UserView;
import com.mk.movies.domain.user.enums.Role;
import com.mk.movies.domain.user.repository.UserRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.UserMapper;
import com.mk.movies.infrastructure.minio.MinioService;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private UserUpdateRequest userUpdateRequest;
    private UserView userView;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(new ObjectId());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("Password123!");
        user.setRole(Role.CLIENT);

        userRequest = new UserRequest(
            "John", "Doe", "john.doe@example.com", "Password123!"
        );

        userUpdateRequest = new UserUpdateRequest(
            "Jane", "Smith", null, Role.CLIENT
        );

        userView = new UserView(
            user.getId().toHexString(),
            "John",
            "Doe",
            "john.doe@example.com",
            null,
            Role.CLIENT.name()
        );
    }

    @Test
    void createUser_savesUser_andReturnsVoid() {
        when(userMapper.toDocument(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> userService.creatUser(userRequest));
        verify(userRepository).save(user);
    }

    @Test
    void getUsers_returnsPageOfUserView() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toView(user)).thenReturn(userView);

        Page<UserView> result = userService.getUsers(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(userView, result.getContent().get(0));
    }

    @Test
    void getUserById_returnsUserView_whenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toView(user)).thenReturn(userView);

        UserView result = userService.getUserById(user.getId());

        assertEquals(userView, result);
    }

    @Test
    void getUserById_throwsResourceNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void updateUser_returnsUpdatedUserView_whenUserIsUpdated() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateDocument(userUpdateRequest, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toView(user)).thenReturn(userView);

        var result = userService.updateUser(user.getId(), userUpdateRequest);

        assertEquals(userView, result);
    }

    @Test
    void updateUser_throwsResourceNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userService.updateUser(user.getId(), userUpdateRequest));
    }

    @Test
    void deleteUser_deletesUser_whenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void deleteUser_throwsResourceNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(user.getId()));
    }
}