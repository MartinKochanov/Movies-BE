package com.mk.movies.domain.user.service;

import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.dto.UserRequest;
import com.mk.movies.domain.user.dto.UserUpdateRequest;
import com.mk.movies.domain.user.dto.UserView;
import com.mk.movies.domain.user.repository.UserRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void creatUser(UserRequest userRequest) {

        var User = userMapper.toDocument(userRequest);
        userRepository.save(User);
    }

    public Page<UserView> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toView);
    }

    public UserView getUserById(ObjectId id) {
        return userMapper.toView(getUser(id));
    }

    private User getUser(ObjectId id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserView updateUser(ObjectId id, UserUpdateRequest userRequest) {
        var user = getUser(id);

        userMapper.updateDocument(userRequest, user);

        var updateduser = userRepository.save(user);

        return userMapper.toView(updateduser);
    }

    public void deleteUser(ObjectId id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(
                () -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
