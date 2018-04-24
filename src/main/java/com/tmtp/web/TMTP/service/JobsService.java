package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.web.formobjects.JobForm;

import java.util.List;

public interface JobsService {

    void createNewJob(JobForm jobForm);

    Job retrieveJobById(String id);

    Job retrieveJobByDescription(String description);

    void updateJob(Job job);

    List<Job> retrieveAllJobs();
}
