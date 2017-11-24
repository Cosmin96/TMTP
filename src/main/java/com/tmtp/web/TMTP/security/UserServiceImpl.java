package com.tmtp.web.TMTP.security;

import com.tmtp.web.TMTP.entity.PlayerKit;
import com.tmtp.web.TMTP.entity.Points;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.RoleRepository;
import com.tmtp.web.TMTP.repository.UserRepository;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Collections;
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
    public void save(User user){
        User userToSave = new User();

        File source=new File("src/main/resources/static/img/profile/profile.png");
        File destination=new File("src/main/resources/static/img/profile/" + user.getUsername() + ".jpg");
        try{
            FileUtils.copyFile(source, destination);
        }
        catch(IOException e){
            System.out.println(e);
        }
//        copyFile(source,destination);
        userToSave.setUsername(user.getUsername());
        userToSave.setEmail(user.getEmail());
        userToSave.setFirstName(user.getFirstName());
        userToSave.setLastName(user.getLastName());
        userToSave.setTitle("Stay in your lane!");
        userToSave.setProfile("/img/profile/profile.png");
        userToSave.setOverlay("");
        userToSave.setStadiumLevel(1);
        userToSave.setAdmin(false);
        userToSave.setBanned(false);
        userToSave.setPrivateLobby(false);
        userToSave.setBanTime(DateTime.now());
        userToSave.setShopItems(Collections.emptyList());
        userToSave.setPlayerKit(new PlayerKit("none", "none", "none", "none"));
        userToSave.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userToSave.setRoles(new HashSet<>(roleRepository.findAll()));
        userToSave.setPoints(createNewPointsObject());
        userRepository.save(userToSave);
    }

    @Override
    public void updateUser(User user){
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    private Points createNewPointsObject(){
        Points points = new Points();
        points.setGreen(0);
        points.setYellow(0);
        points.setRed(0);
        return points;
    }

    static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new RandomAccessFile(sourceFile,"rw").getChannel();
            destination = new RandomAccessFile(destFile,"rw").getChannel();

            long position = 0;
            long count    = source.size();

            source.transferTo(position, count, destination);
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

}
