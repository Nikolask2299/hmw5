package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dto.CreateUserRequest;
import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.exception.UserNotFoundException;
import com.example.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(CreateUserRequest request){
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("User with " + request.email() + " already exists");
        }

        User user = new User(request.name(), request.email(), request.age());
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }
    
    public UserDto getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(
            () -> new UserNotFoundException("User with id " + id + " not found")
        );
        return new UserDto(user);
    }

    public List<UserDto> getAllUsers(){
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }

    public void deleteUser(Long id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    public UserDto updateUser(Long id, CreateUserRequest request){
        User user = userRepository.findById(id).orElseThrow(
            () -> new UserNotFoundException("User with id " + id + " not found")
        );

        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());

        User updatedUser = userRepository.save(user);
        return new UserDto(updatedUser);
    }


}
