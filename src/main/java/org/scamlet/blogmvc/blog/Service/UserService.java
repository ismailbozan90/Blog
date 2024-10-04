package org.scamlet.blogmvc.blog.Service;


import jakarta.transaction.Transactional;
import org.scamlet.blogmvc.blog.Entity.Role;
import org.scamlet.blogmvc.blog.Entity.User;
import org.scamlet.blogmvc.blog.Repository.RoleRepository;
import org.scamlet.blogmvc.blog.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void registerUser(User user) {
        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Default role
        Role tempRole = roleRepository.findByRole("USER");
        user.setRoles(Set.of(tempRole));
        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public boolean checkUserName(String username) {
        User user = userRepository.findByUserName(username);
        return user != null;
    }

    public boolean checkUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void changeUserPassword(User user, String password) {
        String hashedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

}
