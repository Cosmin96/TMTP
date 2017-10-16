package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.service.StorageService;
import com.tmtp.web.TMTP.web.formobjects.JobForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class JobController {

    private final UserDataFacade userDataFacade;
    private final JobsDataFacade jobsDataFacade;
    private final StorageService storageService;

    public JobController(final UserDataFacade userDataFacade,
                         final JobsDataFacade jobsDataFacade,
                         final StorageService storageService) {
        this.userDataFacade = userDataFacade;
        this.jobsDataFacade = jobsDataFacade;
        this.storageService = storageService;
    }

    @RequestMapping("/jobs")
    public String getJobsPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("jobs", jobsDataFacade.retrieveAllJobs());
        model.addAttribute("jobForm", new JobForm());
        return "jobs";
    }

    @RequestMapping("/jobs/submit")
    public String submitNewJob(@ModelAttribute("jobForm") JobForm jobForm, Model model){

        jobsDataFacade.createNewJob(jobForm);
        Job job = jobsDataFacade.retrieveJobByDescription(jobForm.getDescription());
        jobPhotoUpload(jobForm.getImagePath(), job.getId());
        job.setImagePath(job.getId());
        jobsDataFacade.updateJob(job);
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

    private void jobPhotoUpload(MultipartFile file, String id) {
        String photoName = storageService.storeJobPhoto(file, id);
    }
}
