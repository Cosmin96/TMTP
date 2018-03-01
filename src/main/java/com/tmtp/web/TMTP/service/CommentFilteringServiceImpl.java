package com.tmtp.web.TMTP.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CommentFilteringServiceImpl implements CommentFilteringService {

    private List<String> abusiveTerms = Arrays.asList("nigger",
                                                        "nigga",
                                                        "neger",
                                                        "paki",
                                                        "chinky",
                                                        "chinaman",
                                                        "chink",
                                                        "faggot",
                                                        "fag",
                                                        "faggit",
                                                        "slut",
                                                        "sloot",
                                                        "negro",
                                                        "negros",
                                                        "niggas",
                                                        "niggaz",
                                                        "pikie");



    @Override
    public boolean filterComment(String comment){
        boolean appearance = false;
//        if(abusiveTerms.stream().parallel().anyMatch(comment::contains)){
//            appearance = true;
//        }
        for(String abusiveTerm : abusiveTerms){
            if(comment.contains(abusiveTerm)){
                appearance = true;
                break;
            }
        }
        return appearance;
    }
}
