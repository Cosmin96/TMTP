package com.tmtp.web.TMTP.web;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.dto.CloudinaryObject;
import com.tmtp.web.TMTP.dto.enums.FileType;
import com.tmtp.web.TMTP.entity.Job;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import com.tmtp.web.TMTP.payment.StripeService;
import com.tmtp.web.TMTP.service.cloud.CloudStorageService;
import com.tmtp.web.TMTP.web.formobjects.JobForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class JobController {

    @Value("${cloudinary.job.folder}")
    private String jobBucket;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;
    private final UserDataFacade userDataFacade;
    private final JobsDataFacade jobsDataFacade;
    private final StripeService paymentsService;
    private final CloudStorageService cloudStorage;

    public JobController(final UserDataFacade userDataFacade,
                         final JobsDataFacade jobsDataFacade,
                         final StripeService paymentsService,
                         final CloudStorageService cloudStorage) {
        this.userDataFacade = userDataFacade;
        this.jobsDataFacade = jobsDataFacade;
        this.paymentsService = paymentsService;
        this.cloudStorage = cloudStorage;
    }

    @RequestMapping("/jobs")
    public String getJobsPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("jobs", jobsDataFacade.retrieveAllJobs());
        model.addAttribute("jobForm", new JobForm());
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "jobs";
    }

    @RequestMapping("/jobs/submit")
    public String submitNewJob(@ModelAttribute("jobForm") JobForm jobForm, ChargeRequest chargeRequest, Model model) throws StripeException, IOException {
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }
        jobsDataFacade.createNewJob(jobForm);
        if(!jobForm.getImagePath().isEmpty()) {
            Job job = jobsDataFacade.retrieveJobByDescription(jobForm.getDescription());
            job.setImagePath(jobPhotoUpload(jobForm.getImagePath()));
            jobsDataFacade.updateJob(job);
        }

        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentsService.charge(chargeRequest);

        return "redirect:/jobs";
    }

    private String jobPhotoUpload(MultipartFile file) throws IOException {
        CloudinaryObject cloudinaryObject = cloudStorage.uploadFile(file, FileType.IMAGE, jobBucket);
        return cloudinaryObject.getSecureUrl();
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }
}
