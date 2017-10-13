package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class BanCronJob {
    //private static final Logger log = LoggerFactory.getLogger(BanJob.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final UserDataFacade userDataFacade;
    private final UserService userService;

    public BanCronJob(final UserDataFacade userDataFacade,
                      final UserService userService) {
        this.userDataFacade = userDataFacade;
        this.userService = userService;
    }

    @Scheduled(fixedRate = 3600000)
    public void reportCurrentTime() {
        List<User> users = userDataFacade.retrieveAllUsers();
        for(User user : users){
            if(user.getBanned()){
                DateTime startTime = user.getBanTime();
                DateTime endTime = DateTime.now();
                Period p = new Period(startTime, endTime);
                int hours = p.getHours();
                if(hours >= 72) {
                    user.setBanned(false);
                    userService.updateUser(user);
                }
            }
        }
    }
}
