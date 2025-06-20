package com.mk.movies.domain.user.service;

import static com.mk.movies.infrastructure.minio.MinioConstants.USER_PROFILE_PICTURE_BUCKET;
import static com.mk.movies.infrastructure.minio.MinioUtil.extractFileName;

import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.dto.UserRequest;
import com.mk.movies.domain.user.dto.UserUpdateRequest;
import com.mk.movies.domain.user.dto.UserView;
import com.mk.movies.domain.user.repository.UserRepository;
import com.mk.movies.infrastructure.exceptions.ResourceNotFoundException;
import com.mk.movies.infrastructure.mappers.UserMapper;
import com.mk.movies.infrastructure.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MinioService minioService;

    public void creatUser(UserRequest userRequest) {

        var user = userMapper.toDocument(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));

        userRepository.save(user);
    }

    public Page<UserView> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toView);
    }

    public UserView getUserById(ObjectId id) {
        return userMapper.toView(getUser(id));
    }

    public UserView updateUser(ObjectId id, UserUpdateRequest userRequest) {
        var user = getUser(id);

        if (userRequest.image() != null && !userRequest.image().isEmpty()) {
            if (user.getImageUrl() != null) {
                String oldImageName = extractFileName(user.getImageUrl());
                minioService.deleteFile(USER_PROFILE_PICTURE_BUCKET, oldImageName);
            }

            var profilePictureUrl = minioService.uploadFile(
                USER_PROFILE_PICTURE_BUCKET, userRequest.image());
            user.setImageUrl(profilePictureUrl);
        }

        userMapper.updateDocument(userRequest, user);
        var updateduser = userRepository.save(user);

        return userMapper.toView(updateduser);
    }

    public void deleteUser(ObjectId id) {
        var  user = getUser(id);

        if (user.getImageUrl() != null) {
            String profilePictureName = extractFileName(user.getImageUrl());
            minioService.deleteFile(USER_PROFILE_PICTURE_BUCKET, profilePictureName);
        }

        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(
                () -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private User getUser(ObjectId id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
