package com.tmtp.web.TMTP.security;

import com.tmtp.web.TMTP.entity.Points;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.RoleRepository;
import com.tmtp.web.TMTP.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(final UserRepository userRepository,
                           final RoleRepository roleRepository,
                           final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void save(User user) {
        User userToSave = new User();
        userToSave.setUsername(user.getUsername());
        userToSave.setEmail(user.getEmail());
        userToSave.setFirstName(user.getFirstName());
        userToSave.setLastName(user.getLastName());
        userToSave.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userToSave.setRoles(new HashSet<>(roleRepository.findAll()));
        userToSave.setPoints(createNewPointsObject());
        userRepository.save(userToSave);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private Points createNewPointsObject(){
        Points points = new Points();
        points.setGreen(0);
        points.setYellow(0);
        points.setRed(0);
        return points;
    }

}
