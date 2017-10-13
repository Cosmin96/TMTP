package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.Job;

import java.util.List;

public interface JobsService {

    void createNewJob(Job job);

    Job retrieveJobById(String id);

    List<Job> retrieveAllJobs();
}
