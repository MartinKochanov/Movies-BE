package com.mk.movies.web;

import static org.springframework.http.HttpStatus.CREATED;

import com.mk.movies.domain.user.dto.UserRequest;
import com.mk.movies.domain.user.dto.UserUpdateRequest;
import com.mk.movies.domain.user.dto.UserView;
import com.mk.movies.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "API for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a user", description = "Create a user with the given data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid UserRequest userRequest) {
        userService.creatUser(userRequest);
        return ResponseEntity
            .status(CREATED)
            .build();
    }

    @Operation(summary = "Get all users", description = "Get all users with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved"),
    })
    @GetMapping
    public ResponseEntity<Page<UserView>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @Operation(summary = "Get user by ID", description = "Get a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid object ID"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserView> getById(@PathVariable ObjectId id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user by ID", description = "Update an existing user by their ID. Support partial updates.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "400", description = "Invalid object ID or input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserView> update(@PathVariable ObjectId id, @RequestBody @Valid UserUpdateRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @Operation(summary = "Delete user by ID", description = "Delete a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "400", description = "Invalid object ID or input data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ObjectId id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
