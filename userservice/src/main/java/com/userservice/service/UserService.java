package com.userservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.userservice.dto.CreateUserRequest;
import com.userservice.dto.UserActionEvent;
import com.userservice.dto.UserDto;
import com.userservice.entity.User;
import com.userservice.exception.UserNotFoundException;
import com.userservice.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, UserActionEvent> kafkaTemplate;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(CreateUserRequest request){
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("User with " + request.email() + " already exists");
        }

        User user = new User(request.name(), request.email(), request.age());
        User savedUser = userRepository.save(user);
        kafkaTemplate.send("user-actions", new UserActionEvent("CREATE", user.getEmail()));
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
            Optional<User> userOpt = userRepository.findById(id);
            User user = userOpt.get();
            userRepository.deleteById(id);
            kafkaTemplate.send("user-actions", new UserActionEvent("CREATE", user.getEmail()));
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
