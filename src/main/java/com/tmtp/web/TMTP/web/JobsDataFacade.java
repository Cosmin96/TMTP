package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.service.JobsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobsDataFacade {

    private final JobsService jobsService;

    public JobsDataFacade(final JobsService jobsService) {
        this.jobsService = jobsService;
    }

    public void createNewJob(Job job){
        jobsService.createNewJob(job);
    }

    public List<Job> retrieveAllJobs(){
        return jobsService.retrieveAllJobs();
    }

    public Job retrieveJobById(String id){
        return jobsService.retrieveJobById(id);
    }
}
