package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.service.JobsService;
import com.tmtp.web.TMTP.web.formobjects.JobForm;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobsDataFacade {

    private final JobsService jobsService;

    public JobsDataFacade(final JobsService jobsService) {
        this.jobsService = jobsService;
    }

    public void createNewJob(JobForm jobForm){
        jobsService.createNewJob(jobForm);
    }

    public List<Job> retrieveAllJobs(){
        return jobsService.retrieveAllJobs();
    }

    public Job retrieveJobByDescription(String description){
        return jobsService.retrieveJobByDescription(description);
    }

    public void updateJob(Job job){
        jobsService.updateJob(job);
    }

    public Job retrieveJobById(String id){
        return jobsService.retrieveJobById(id);
    }
}
