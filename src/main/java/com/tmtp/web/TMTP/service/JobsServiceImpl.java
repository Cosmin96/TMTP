package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.repository.JobRepository;
import com.tmtp.web.TMTP.web.formobjects.JobForm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobsServiceImpl implements JobsService {

    private final JobRepository jobRepository;

    public JobsServiceImpl(final JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void createNewJob(JobForm jobForm){
        Job job = new Job();
        job.setTitle(jobForm.getTitle());
        job.setContact(jobForm.getContact());
        job.setDate(jobForm.getDate());
        job.setDescription(jobForm.getDescription());
        job.setLocation(jobForm.getLocation());
        jobRepository.save(job);
    }

    @Override
    public void updateJob(Job job){
        jobRepository.save(job);
    }

    @Override
    public Job retrieveJobById(String id){
        return jobRepository.findById(id);
    }

    @Override
    public Job retrieveJobByDescription(String description){
        return jobRepository.findByDescription(description);
    }

    @Override
    public List<Job> retrieveAllJobs(){
        return jobRepository.findAll();
    }
}
