package com.tmtp.web.TMTP.web;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.entity.*;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import com.tmtp.web.TMTP.payment.StripeService;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.StorageService;
import com.tmtp.web.TMTP.web.formobjects.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class MobileController {

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;
    private final JobsDataFacade jobsDataFacade;
    private final StorageService storageService;
    private final StripeService paymentsService;
    private final UserDataFacade userDataFacade;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ChatMessageRepository chatMessageRepository;
    private final PrivateLobbyFacade privateLobbyFacade;
    private final ShopItemFacade shopItemFacade;

    public MobileController(final JobsDataFacade jobsDataFacade,
                            final StorageService storageService,
                            final StripeService paymentsService,
                            final UserDataFacade userDataFacade,
                            final UserService userService,
                            final BCryptPasswordEncoder bCryptPasswordEncoder,
                            final ChatMessageRepository chatMessageRepository,
                            final PrivateLobbyFacade privateLobbyFacade,
                            final ShopItemFacade shopItemFacade) {
        this.jobsDataFacade = jobsDataFacade;
        this.storageService = storageService;
        this.paymentsService = paymentsService;
        this.userDataFacade = userDataFacade;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.chatMessageRepository = chatMessageRepository;
        this.privateLobbyFacade = privateLobbyFacade;
        this.shopItemFacade = shopItemFacade;
    }

    //ADMIN CONTROLLER ENDPOINTS

    @RequestMapping("/mobile/admin")
    public String getAdminPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(!user.getAdmin()){
            return "redirect:/home";
        }
        List<User> users = userDataFacade.retrieveAllUsers();
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        return "adminpanel_mobile";
    }

    @RequestMapping("/mobile/admin/delete/{username}")
    public String deleteUser(@PathVariable("username") String username){
        User user = userDataFacade.retrieveLoggedUser();
        if(!user.getAdmin()){
            return "redirect:/home";
        }
        User userToDelete = userDataFacade.retrieveUser(username);
        userDataFacade.deleteUser(userToDelete);
        return "redirect:/mobile/admin";
    }

    @RequestMapping("/mobile/admin/edit/{username}")
    public String editUser(@PathVariable("username") String username, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        model.addAttribute("loggeduser", loggedInUser);
        model.addAttribute("user", user);
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("inventory", user.getShopItems());
        model.addAttribute("nameForm", new NameForm());
        model.addAttribute("passForm", new PassForm());
        model.addAttribute("overlayForm", new OverlayForm());
        model.addAttribute("kitForm", new PlayerKitForm());
        model.addAttribute("pointsForm", new PointsForm());
        model.addAttribute("bannedForm", new BannedForm());
        model.addAttribute("titleForm", new TitleForm());
        return "adminpaneledit_mobile";
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updateName", method = RequestMethod.POST)
    public String updateNameByAdmin(@PathVariable("username") String username, @ModelAttribute("nameForm") NameForm nameForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        user.setFirstName(nameForm.getFname());
        user.setLastName(nameForm.getLname());
        userService.updateUser(user);

        return "redirect:/mobile/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updatePassword", method = RequestMethod.POST)
    public String updatePasswordByAdmin(@PathVariable("username") String username, @ModelAttribute("passForm") PassForm passForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);

        if(passForm.getOldPass().isEmpty() || passForm.getNewPass().isEmpty()){
            return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
        }

        if(passForm.getOldPass().equals(passForm.getNewPass())){
            user.setPassword(bCryptPasswordEncoder.encode(passForm.getNewPass()));
            userService.updateUser(user);
        }

        return "redirect:/mobile/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updateTitle", method = RequestMethod.POST)
    public String updateTitleAdmin(@PathVariable("username") String username, @ModelAttribute("titleForm") TitleForm titleForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User pageUser = userDataFacade.retrieveUser(username);
        pageUser.setTitle(titleForm.getTitleName());
        userService.updateUser(pageUser);

        return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updatePoints", method = RequestMethod.POST)
    public String updatePointsByAdmin(@PathVariable("username") String username, @ModelAttribute("pointsForm") PointsForm pointsForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        switch (pointsForm.getColor()){
            case "green":
                user.getPoints().setGreen(pointsForm.getPoints());
                break;
            case "yellow":
                user.getPoints().setYellow(pointsForm.getPoints());
                break;
            case "red":
                user.getPoints().setRed(pointsForm.getPoints());
                break;
            default:
                break;
        }
        userService.updateUser(user);
        return "redirect:/mobile/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updateBan", method = RequestMethod.POST)
    public String updatePointsByAdmin(@PathVariable("username") String username, @ModelAttribute("bannedForm") BannedForm bannedForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        user.setBanned(bannedForm.getBanned());
        userService.updateUser(user);
        return "redirect:/mobile/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updateOverlay", method = RequestMethod.POST)
    public String updateOverlayByAdmin(@PathVariable("username") String username, @ModelAttribute("overlayForm") OverlayForm overlayForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);

        user.setOverlay(overlayForm.getOverlayName());
        userService.updateUser(user);
        return "redirect:/mobile/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/mobile/admin/edit/{username}/updateKit", method = RequestMethod.POST)
    public String updatePlayerKitByAdmin(@PathVariable("username") String username, @ModelAttribute("kitForm") PlayerKitForm playerKitForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        user.setPlayerKit(new PlayerKit(playerKitForm.getJacket(), playerKitForm.getShorts(), playerKitForm.getSocks(), playerKitForm.getFootball()));
        userService.updateUser(user);

        return "redirect:/mobile/admin/edit/" + user.getUsername();
    }

    //JOBS CONTROLLER ENDPOINTS
    @RequestMapping("/mobile/jobs")
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
        return "jobs_mobile";
    }

    @RequestMapping("/mobile/jobs/submit")
    public String submitNewJob(@ModelAttribute("jobForm") JobForm jobForm, ChargeRequest chargeRequest, Model model) throws StripeException {
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        jobsDataFacade.createNewJob(jobForm);
        if(!jobForm.getImagePath().isEmpty()) {
            Job job = jobsDataFacade.retrieveJobByDescription(jobForm.getDescription());
            jobPhotoUpload(jobForm.getImagePath(), job.getId());
            job.setImagePath(job.getId());
            jobsDataFacade.updateJob(job);
        }

        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentsService.charge(chargeRequest);

        return "redirect:/mobile/jobs";
    }

    private void jobPhotoUpload(MultipartFile file, String id) {
        String photoName = storageService.storeJobPhoto(file, id);
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }

    //LOBBIESCONTROLLER ENDPOINTS
    @RequestMapping("/mobile/lobbies/{type}")
    public String scorePage(@PathVariable ("type") String type, Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("lobbyType", type);
        return "lobbies_mobile";
    }

    @RequestMapping("/mobile/lobby/{league}")
    public String lobbyPage(@PathVariable("league") String league, Model model){
        int n = 0;
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        switch(league){
            case "fapremier":
                n = 1;
                break;
            case "league1":
                n = 2;
                break;
            case "laliga":
                n = 3;
                break;
            case "brasil":
                n = 4;
                break;
            case "bundesliga":
                n = 5;
                break;
            case "primeiraliga":
                n = 6;
                break;
            case "serieA":
                n = 7;
                break;
            case "usamls":
                n = 8;
                break;
            case "chinese":
                n = 9;
                break;
            case "dutcheredivisie":
                n = 10;
                break;
            case "mexican":
                n = 11;
                break;
            case "belgium":
                n = 12;
                break;
            case "swiss":
                n = 13;
                break;
            case "russian":
                n = 14;
                break;
            case "ukranian":
                n = 15;
                break;
            case "turkish":
                n = 16;
                break;
            case "japan":
                n = 17;
                break;
            case "argentina":
                n = 18;
                break;
            case "scotland":
                n = 19;
                break;
            case "polish":
                n = 20;
                break;
            case "women":
                n = 21;
                break;
            case "barcelona":
                n = 22;
                break;
            case "realmadrid":
                n = 23;
                break;
            case "manu":
                n = 24;
                break;
            case "chelsea":
                n = 25;
                break;
            case "arsenal":
                n = 26;
                break;
            case "bayern":
                n = 27;
                break;
            case "liverpool":
                n = 28;
                break;
            case "psg":
                n = 29;
                break;
            case "juventus":
                n = 30;
                break;
            case "acmilan":
                n = 31;
                break;
            case "mancity":
                n = 32;
                break;
            case "tottenham":
                n = 33;
                break;
            case "atletico":
                n = 34;
                break;
            case "lyonnais":
                n = 35;
                break;
            case "palmeiras":
                n = 36;
                break;
            case "santos":
                n = 37;
                break;
            case "ajax":
                n = 38;
                break;
            case "celtic":
                n = 39;
                break;
            case "innermilan":
                n = 40;
                break;
            case "asroma":
                n = 41;
                break;
            case "dortmund":
                n = 42;
                break;
            case "gay":
                n = 43;
                break;
            case "everton":
                n = 44;
                break;
            case "leicester":
                n = 45;
                break;
            case "marseille":
                n = 46;
                break;
            case "monaco":
                n = 47;
                break;
            case "valencia":
                n = 48;
                break;
            case "sevilla":
                n = 49;
                break;
            case "napoli":
                n = 50;
                break;
            case "lazio":
                n = 51;
                break;
            case "eindhoven":
                n = 52;
                break;
            case "feyenoord":
                n = 53;
                break;
            case "rangers":
                n = 54;
                break;
            case "aberdeen":
                n = 55;
                break;
            case "corinthians":
                n = 56;
                break;
            case "leverkusen":
                n = 57;
                break;
            case "schalke":
                n = 58;
                break;
            case "african":
                n = 59;
                break;
        }

        int lastNMessages = 30;
        List<ChatMessage> chatMessages = chatMessageRepository.findByName("chat-" + n);
        int total = chatMessages.size();

        if(total > 30) {
            chatMessages = chatMessages.subList(total - lastNMessages, total);
        }

        model.addAttribute("league", n);
        model.addAttribute("user", user);
        model.addAttribute("messages", chatMessages);
        model.addAttribute("totalMessages", total);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        return "league_mobile";
    }

    //PRIVATE LOBBY CONTROLLER
    @RequestMapping("/mobile/privateLobby/{id}")
    public String openLobby(@PathVariable("id") String id, Model model){
        PrivateLobby privateLobby = privateLobbyFacade.findById(id);
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }

        int lastNMessages = 30;
        List<ChatMessage> chatMessages = chatMessageRepository.findByName("chat-" + privateLobby.getId());
        int total = chatMessages.size();

        if(total > 30) {
            chatMessages = chatMessages.subList(total - lastNMessages, total);
        }

        boolean owner = false;
        boolean joined = false;
        model.addAttribute("lobby", privateLobby);
        model.addAttribute("user", user);
        model.addAttribute("messages", chatMessages);
        model.addAttribute("totalMessages", total);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("lobbyCreator", privateLobby.getCreator());
        model.addAttribute("trophyMessageCheck", false);
        model.addAttribute("trophyMessage", "Congratulations for creating your own VIP room. You have won a free golden trophy");

        if(!privateLobby.getJoinedUsers().isEmpty()){
            if(privateLobby.getJoinedUsers().contains(user.getUsername())){
                joined = true;
            }
        }
        if(privateLobby.getCreator().equals(user.getUsername())){
            owner = true;
        }
        if(owner || joined){
            model.addAttribute("showLobby", true);
        }
        else{
            model.addAttribute("showLobby", false);
        }
        return "privatelobby_mobile";
    }

    @RequestMapping("/mobile/privateLobby/join/{id}")
    public String joinLobby(@PathVariable("id") String id, ChargeRequest chargeRequest) throws StripeException{
        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentsService.charge(chargeRequest);

        PrivateLobby privateLobby = privateLobbyFacade.findById(id);
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }
        List<String> joinedUsers = privateLobby.getJoinedUsers();
        joinedUsers.add(user.getUsername());
        privateLobby.setJoinedUsers(joinedUsers);
        privateLobbyFacade.updateLobby(privateLobby);

        return "redirect:/mobile/privateLobby/" + privateLobby.getId();
    }

    @RequestMapping("/mobile/lobby/create/{paymentType}")
    public String createLobby(@PathVariable("paymentType") String paymentType, ChargeRequest chargeRequest, RedirectAttributes redirectAttributes) throws StripeException{
        User user = userDataFacade.retrieveLoggedUser();
        if(paymentType.equals("card")){
            chargeRequest.setDescription("Payment");
            chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
            Charge charge = paymentsService.charge(chargeRequest);
        }
        else{
            user.getPoints().setGreen(user.getPoints().getGreen() - 50);
        }

        ShopItem goldenTrophy = new ShopItem();
        goldenTrophy.setName("Golden Trophy 5");
        goldenTrophy.setType("trophies");
        goldenTrophy.setGbpPrice(69);
        goldenTrophy.setGbpPriceFormatted("£0.69");
        goldenTrophy.setImgPath("/img/kits/trophies/6.png");
        goldenTrophy.setId("59ea4154081e8648c47614f6");
        goldenTrophy.setPointPrice(50);

        user.setPrivateLobby(true);
        user.getShopItems().add(goldenTrophy);
        user.setTrophy("Golden Trophy 5");
        userDataFacade.updateUser(user);

        PrivateLobby privateLobby = new PrivateLobby();
        privateLobby.setCreator(user.getUsername());
        privateLobby.setJoinedUsers(Collections.emptyList());
        privateLobbyFacade.createLobby(privateLobby);
        privateLobby = privateLobbyFacade.findByCreator(user.getUsername());

        redirectAttributes.addFlashAttribute("showLobby", true);
        redirectAttributes.addFlashAttribute("owner", true);
        redirectAttributes.addFlashAttribute("trophyMessageCheck", true);
        redirectAttributes.addFlashAttribute("trophyMessage", "Congratulations for creating your own VIP room. You have won a free golden trophy");

        return "redirect:/mobile/privateLobby/" + privateLobby.getId();
    }

    //PROFILE CONTROLLER ENDPoINTS
    @RequestMapping("/mobile/profile/{username}")
    public String profilePage(@PathVariable("username") String username, Model model){

        User pageuser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();

        if(pageuser.getId().equals(loggedInUser.getId())){
            model.addAttribute("settingsButton", true);
        }
        else{
            model.addAttribute("settingsButton", false);
        }
        getStadium(model, pageuser);
        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("user", pageuser);
        model.addAttribute("greenPoints", pageuser.getPoints().getGreen());
        model.addAttribute("yellowPoints", pageuser.getPoints().getYellow());
        model.addAttribute("redPoints", pageuser.getPoints().getRed());
        return "profile_mobile";
    }

    @RequestMapping("/mobile/profile/{username}/stadiumUpgrade")
    public String upgradeStadium(@PathVariable("username") String username, Model model, RedirectAttributes redirectAttributes){
        User user = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!user.getUsername().equals(loggedInUser.getUsername())){
            return "redirect:/home";
        }

        switch(user.getStadiumLevel()){
            case 1:
                if(user.getPoints().getGreen() < 100){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/mobile/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 100);
                break;
            case 2:
                if(user.getPoints().getGreen() < 150){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/mobile/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 150);
                break;
            case 3:
                if(user.getPoints().getGreen() < 200){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/mobile/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 200);
                break;
            case 4:
                if(user.getPoints().getGreen() < 250){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/mobile/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 200);
                redirectAttributes.addFlashAttribute("finalStadium", true);
                break;
        }
        user.setStadiumLevel(user.getStadiumLevel() + 1);
        userService.updateUser(user);

        return "redirect:/mobile/profile/" + username;
    }

    private void getStadium(Model model, User user){
        switch(user.getStadiumLevel()){
            case 1:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 10){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 100 points");
                break;
            case 2:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 50){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 150 points");
                break;
            case 3:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 100){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 200 points");
                break;
            case 4:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 250){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 250 points");
                break;
            case 5:
                model.addAttribute("upgradeButton", false);
                if(user.getPoints().getGreen() < 300){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 300 points");
                break;
        }
    }

    //PROFILE SETTINGS CONTROLLER
    @RequestMapping("/mobile/settings/{username}")
    public String profilePageSettings(@PathVariable("username") String username, Model model){

        User pageUser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();

        if(pageUser.getId().equals(loggedInUser.getId())){

            model.addAttribute("fname", loggedInUser.getFirstName());
            model.addAttribute("user", pageUser);
            model.addAttribute("inventory", loggedInUser.getShopItems());
            model.addAttribute("greenPoints", pageUser.getPoints().getGreen());
            model.addAttribute("yellowPoints", pageUser.getPoints().getYellow());
            model.addAttribute("redPoints", pageUser.getPoints().getRed());
            model.addAttribute("nameForm", new NameForm());
            model.addAttribute("passForm", new PassForm());
            model.addAttribute("overlayForm", new OverlayForm());
            model.addAttribute("kitForm", new PlayerKitForm());
            model.addAttribute("titleForm", new TitleForm());
            model.addAttribute("trophyForm", new TrophyForm());

            return "settings_mobile";
        }
        else{
            return "redirect:/home";
        }
    }

    @RequestMapping(value = "/mobile/settings/{username}/updateName", method = RequestMethod.POST)
    public String updateFirstandLastNames(@PathVariable("username") String username, @ModelAttribute("nameForm") NameForm nameForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);

        if(!isUserRight){
            return "redirect:/home";
        }

        User loggedInUser = userDataFacade.retrieveLoggedUser();

        loggedInUser.setFirstName(nameForm.getFname());
        loggedInUser.setLastName(nameForm.getLname());
        userService.updateUser(loggedInUser);

        return "redirect:/mobile/settings/" + loggedInUser.getUsername();
    }

    @RequestMapping(value = "/mobile/settings/{username}/updatePassword", method = RequestMethod.POST)
    public String updatePassword(@PathVariable("username") String username, @ModelAttribute("passForm") PassForm passForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        if(passForm.getOldPass().isEmpty() || passForm.getNewPass().isEmpty()){
            return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
        }

        if(passForm.getOldPass().equals(passForm.getNewPass())){
            pageUser.setPassword(bCryptPasswordEncoder.encode(passForm.getNewPass()));
            userService.updateUser(pageUser);
        }

        return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/mobile/settings/{username}/updateOverlay", method = RequestMethod.POST)
    public String updateOverlay(@PathVariable("username") String username, @ModelAttribute("overlayForm") OverlayForm overlayForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setOverlay(overlayForm.getOverlayName());
        userService.updateUser(pageUser);


        return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/mobile/settings/{username}/updateTitle", method = RequestMethod.POST)
    public String updateTitle(@PathVariable("username") String username, @ModelAttribute("titleForm") TitleForm titleForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setTitle(titleForm.getTitleName());
        userService.updateUser(pageUser);

        return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/mobile/settings/{username}/updateTrophy", method = RequestMethod.POST)
    public String updateTrophy(@PathVariable("username") String username, @ModelAttribute("trophyForm") TrophyForm trophyForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setTrophy(trophyForm.getName());
        userService.updateUser(pageUser);

        return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/mobile/settings/{username}/updateKit", method = RequestMethod.POST)
    public String updatePlayerKit(@PathVariable("username") String username, @ModelAttribute("kitForm") PlayerKitForm playerKitForm, Model model){

        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setPlayerKit(new PlayerKit(playerKitForm.getJacket(), playerKitForm.getShorts(), playerKitForm.getSocks(), playerKitForm.getFootball()));
        userService.updateUser(pageUser);

        return "redirect:/mobile/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    private Boolean checkUsersAreSame(String username){
        User pageUser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        return pageUser.getId().equals(loggedInUser.getId());
    }

    //SHOP CONTROLLER ENDPoINTS
    @RequestMapping("/mobile/shop")
    public String storePage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("category", "jacket");
        return "store_mobile";
    }

    @RequestMapping("/mobile/shop/shorts")
    public String storePageShorts(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }

        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("category", "shorts");
        return "store_mobile";
    }

    @RequestMapping("/mobile/shop/socks")
    public String storePageSocks(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }

        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("category", "socks");
        return "store_mobile";
    }

    @RequestMapping("/mobile/shop/footballs")
    public String storePageFootballs(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("category", "football");
        return "store_mobile";
    }

    @RequestMapping("/mobile/shop/trophies")
    public String storePageTrophies(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("category", "trophies");
        return "store_mobile";
    }

    @RequestMapping("/mobile/shop/overlays")
    public String storePageOverlays(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        List<ShopItem> items = shopItemFacade.retrieveAllItems();

        List<ShopItem> countryOverlays = new ArrayList<ShopItem>();
        List<ShopItem> clubOverlays = new ArrayList<ShopItem>();

        for(ShopItem item : items){
            if(item.getType().equals("overlay")){
                countryOverlays.add(item);
            }
            else if(item.getType().equals("clubOverlay")){
                clubOverlays.add(item);
            }
        }
        items = Collections.emptyList();
        Collections.shuffle(countryOverlays);
        Collections.shuffle(clubOverlays);

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("countryOverlays", countryOverlays);
        model.addAttribute("clubOverlays", clubOverlays);
        model.addAttribute("category", "overlay");
        return "store_mobile";
    }

    @RequestMapping("/mobile/shop/titles")
    public String storePageTitles(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory",  items);
        model.addAttribute("category", "title");
        return "store_mobile";
    }

    @RequestMapping("/mobile/buy/{id}")
    public String buyWithPoints(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes){
        ShopItem shopItem = shopItemFacade.retrieveItemById(id);
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        for(ShopItem item : user.getShopItems()) {
            if (item.getName().equals(shopItem.getName())) {
                redirectAttributes.addFlashAttribute("error", true);
                redirectAttributes.addFlashAttribute("message", "You already own this item!");
                return "redirect:/mobile/shop";
            }
        }
        user.getPoints().setGreen(user.getPoints().getGreen() - shopItem.getPointPrice());
        user.getShopItems().add(shopItem);
        userDataFacade.updateUser(user);
        redirectAttributes.addFlashAttribute("confirmation", true);
        redirectAttributes.addFlashAttribute("boughtItem", shopItem.getName());
        return "redirect:/mobile/shop";
    }

    @RequestMapping("/mobile/buy/chest/{type}")
    public String buyChest(@PathVariable("type") String type, ChargeRequest chargeRequest,
                           Model model, RedirectAttributes redirectAttributes) throws StripeException{
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/mobile/scores";
        }
        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentsService.charge(chargeRequest);

        switch(type){
            case "small":
                user.getPoints().setGreen(user.getPoints().getGreen() + 50);
                break;
            case "medium":
                user.getPoints().setGreen(user.getPoints().getGreen() + 100);
                break;
            case "large":
                user.getPoints().setGreen(user.getPoints().getGreen() + 150);
                break;
        }
        userDataFacade.updateUser(user);
        redirectAttributes.addFlashAttribute("confirmation", true);
        redirectAttributes.addFlashAttribute("boughtItem", type.toUpperCase() + " Chest");
        return "redirect:/mobile/shop";
    }

    //SCORES ENDPoINT
    @RequestMapping(value = "/mobile/scores")
    public String getScoresPage(Model model) {

        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "You have been banned. Your ban will revoke in 3 days or if an admin changes it");
        }
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "scores_mobile";
    }

    @GetMapping("/retrieve/user/{type}/{value}")
    @ResponseBody
    public ResponseEntity<User> retrieveUserFromDB(@PathVariable("type") String type,
                                                   @PathVariable("value") String value){
        User user;
        if(type.equals("id")){
            user = userService.findById(value);
        }
        else{
            user = userService.findByUsername(value);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
}