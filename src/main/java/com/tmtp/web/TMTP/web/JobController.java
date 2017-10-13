package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.web.formobjects.JobForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JobController {

    private final UserDataFacade userDataFacade;
    private final JobsDataFacade jobsDataFacade;

    public JobController(final UserDataFacade userDataFacade,
                         final JobsDataFacade jobsDataFacade) {
        this.userDataFacade = userDataFacade;
        this.jobsDataFacade = jobsDataFacade;
    }

    @RequestMapping("/jobs")
    public String getJobsPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        model.addAttribute("user", user);
        model.addAttribute("jobs", jobsDataFacade.retrieveAllJobs());
        model.addAttribute("jobForm", new JobForm());
        return "jobs";
    }

    @RequestMapping("/jobs/submit")
    public String submitNewJob(@ModelAttribute("jobForm") JobForm jobForm, Model model){
        Job job = new Job(jobForm.getId(), jobForm.getTitle(),jobForm.getDescription(),jobForm.getDescription(), jobForm.getImagePath());
        jobsDataFacade.createNewJob(job);
        return "redirect:/jobs";
    }

    @RequestMapping("/jobs/{id}")
    public String getJob(@PathVariable("id") String id, Model model){
        User user = userDataFacade.retrieveLoggedUser();
        Job job = jobsDataFacade.retrieveJobById(id);
        model.addAttribute("job", job);
        model.addAttribute("user", user);
        return "job";
    }

}
