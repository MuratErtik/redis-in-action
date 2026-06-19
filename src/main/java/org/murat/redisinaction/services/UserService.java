package org.murat.redisinaction.services;

import lombok.RequiredArgsConstructor;
import org.murat.redisinaction.dto.CreateUserRequest;
import org.murat.redisinaction.dto.CreateUserResponse;
import org.murat.redisinaction.dto.UpdateUserDto;
import org.murat.redisinaction.entities.User;
import org.murat.redisinaction.repos.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RedisTemplate<String, User> redisTemplate;


    @CacheEvict(value = {"user_list"},allEntries = true) // while this method revoking cache always be empty for consistency
    public CreateUserResponse createUser(CreateUserRequest request) {

        //validation omitted.
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        userRepository.save(user);

        redisTemplate.opsForList().rightPush("user_list", user); //Write-Through it wont delete the list added the new object into the list.

        return CreateUserResponse.builder()
                .message("User created successfully with username " + user.getUsername())
                .build();
    }

    @Cacheable(value = "users", key = "#username", unless = "#result == null") //users::murat in redis
    public User findUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public User getUserWithSlidingCache(Long id) {
        String cacheKey = "users::" + id;

        User user = (User) redisTemplate.opsForValue().get(cacheKey);

        if (user != null) {
            redisTemplate.expire(cacheKey, Duration.ofMinutes(15));
            return user;
        }


        return findUserByUsername(id.toString()); //it takes from DB (proxy-pattern is on, so the annotation won't work)
    }

    @Cacheable(value = "users", key = "#root.methodName", unless = "#result == null")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        redisTemplate.opsForList().remove("users::getUsers", 1, id); // if the system getting lots of requests use this way
        return "User deleted";
    }

    @CachePut(cacheNames = "users", key = "#dto.id", unless = "#result == null")
    public User updateUser(UpdateUserDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getId()));

        user.setPassword(dto.getPassword());

        return userRepository.save(user);
    }

}
