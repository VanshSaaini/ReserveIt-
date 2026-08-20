package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.response.UserResponse;
import com.Reserveit.v1.entity.User;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Platform-wide operations available only to the SUPER_ADMIN role. */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final Mapper mapper;

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(mapper::toUserResponse).toList();
    }

    @Transactional
    public UserResponse setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setActive(active);
        return mapper.toUserResponse(user);
    }
}
