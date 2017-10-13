package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobsServiceImpl implements JobsService {

    private final JobRepository jobRepository;

    public JobsServiceImpl(final JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void createNewJob(Job job){
        jobRepository.save(job);
    }

    @Override
    public Job retrieveJobById(String id){
        return jobRepository.findById(id);
    }

    @Override
    public List<Job> retrieveAllJobs(){
        return jobRepository.findAll();
    }
}
