package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.entity.Team;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.repository.ShopItemRepository;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.security.UserValidator;
import com.tmtp.web.TMTP.service.StorageService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@Controller
public class LoginController {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserDataFacade userDataFacade;
    private final VideoPostsFacade videoPostsFacade;
    private final MessageSource messageSource;
    private final PrivateLobbyFacade privateLobbyFacade;
    private final StorageService storageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ShopItemRepository shopItemRepository;

    public LoginController(final UserService userService,
                           final SecurityService securityService,
                           final UserValidator userValidator,
                           final UserDataFacade userDataFacade,
                           final VideoPostsFacade videoPostsFacade,
                           final MessageSource messageSource,
                           final PrivateLobbyFacade privateLobbyFacade,
                           final StorageService storageService,
                           final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
        this.messageSource = messageSource;
        this.privateLobbyFacade = privateLobbyFacade;
        this.storageService = storageService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userForm", new User());
        model.addAttribute("teams", EnumSet.allOf(Team.class));

        return "login";
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public String registration(@ModelAttribute("user") User userForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               @RequestParam("file") MultipartFile file,
                               @RequestParam("inviteCode") String inviteCode) {

        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorLogin", true);
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage(bindingResult.getFieldError(), null));
            return "redirect:/register";
        }
        userForm.setUsername(userForm.getUsername().replaceAll(" ",""));
        userForm.setUsername(userForm.getUsername().replaceAll("[^\\w\\s]+",""));
        userForm.setUsername(userForm.getUsername().toLowerCase());
        if(!file.isEmpty()) {
            String photoName = storageService.store(file, userForm.getUsername());
            userForm.setProfile("yes");
        }
        else userForm.setProfile("no");

        // If invite code provided
        if(inviteCode != null) {
            // Check if code belongs to any user
            User referredUser = userService.findById(inviteCode);
            if(referredUser != null) {
                referredUser.getPoints().setGreen(referredUser.getPoints().getGreen() + 3);
                userService.updateUser(referredUser);
            }
        }

        userService.save(userForm);
        securityService.autologin(userForm.getUsername(), userForm.getPassword());

        // Set flag to display Amazon popup after registration
        redirectAttributes.addFlashAttribute("popup", "amazon");

        return "redirect:/home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("userForm") User userForm, Model model, RedirectAttributes redirectAttributes){
        userForm.setUsername(userForm.getUsername().toLowerCase());
        User user = userDataFacade.retrieveUser(userForm.getUsername());

        if(user == null || !bCryptPasswordEncoder.matches(userForm.getPassword(), user.getPassword())){
            redirectAttributes.addFlashAttribute("errorLogin",true);
            redirectAttributes.addFlashAttribute("errorMessage","Username or password were incorrect");
            return "redirect:/register";
        }
//        if(user.getBanned()){
//            //redirectAttributes.addFlashAttribute("error", true);
//            //redirectAttributes.addFlashAttribute("errorMessage", "You are currently banned for unsuitable behaviour! Please wait for an admin to remove your restrictions");
//            return "redirect:/scores";
//        }
        securityService.autologin(userForm.getUsername(), userForm.getPassword());
        // Set flag to display Amazon popup after each login
        redirectAttributes.addFlashAttribute("popup", "amazon");

        return "redirect:/home";
    }

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model) {

        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }

        List<VideoPosts> posts = videoPostsFacade.retrieveListOfVideoPosts();

        //"algorithmic news feed"
        //Collections.shuffle(posts);

        Collections.sort(posts, new Comparator<VideoPosts>() {
            @Override
            public int compare(VideoPosts videoPosts, VideoPosts t1) {
                return videoPosts.getId().compareTo(t1.getId());
            }
        });
        Collections.reverse(posts);

        List<String> allUsers = new ArrayList<String>();
        for(User oneuser : userDataFacade.retrieveAllUsers()){
            allUsers.add(oneuser.getUsername());
        }

        List<PrivateLobby> joinedLobbies = new ArrayList<PrivateLobby>();
        if(!privateLobbyFacade.retrieveAll().isEmpty()){
            for(PrivateLobby lobby : privateLobbyFacade.retrieveAll()){
                if(lobby.getJoinedUsers().contains(user.getUsername())){
                    joinedLobbies.add(lobby);
                }
            }
        }
        if(!joinedLobbies.isEmpty()) {
            model.addAttribute("joinedLobbies", joinedLobbies);
            model.addAttribute("myLobbies", true);
        }
        else{
            model.addAttribute("myLobbies", false);
        }

        if(user.getPrivateLobby()) {
            PrivateLobby userLobby = privateLobbyFacade.findByCreator(user.getUsername());
            model.addAttribute("myLobby", userLobby);
            model.addAttribute("hasLobbies", true);
            model.addAttribute("hasOwnLobby", true);
        }
        else{
            model.addAttribute("hasOwnLobby", false);
            model.addAttribute("hasLobbies", false);
        }
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("videoPostForm", new VideoPosts());
        model.addAttribute("posts", posts);

        return "index";
    }

    @RequestMapping(value = "/scores")
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

        return "scores";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/register";
    }

    @RequestMapping(value="/reset-password", method = RequestMethod.POST)
    public String resetPassword (@ModelAttribute("userForm") User userForm, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        User user = userDataFacade.retrieveUser(userForm.getUsername());

        String captcha = request.getParameter("g-recaptcha-response");

        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "secret=6LfG9kYUAAAAAILtfGNQv5x_5DHC5bTI4KryZEFU&response=" + captcha;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters); wr.flush(); wr.close();

            int responseCode = con.getResponseCode();

            if(responseCode != 200) {
                redirectAttributes.addFlashAttribute("error", true);
                redirectAttributes.addFlashAttribute("errorMessage", "Captcha validation failed!");
                return "redirect:/register";
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            String answer = jsonObject.get("success").toString();
            if(!answer.equals("true")) {
                redirectAttributes.addFlashAttribute("errorLogin", true);
                redirectAttributes.addFlashAttribute("errorMessage", "Captcha validation failed!");
                return "redirect:/register";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorLogin", true);
            redirectAttributes.addFlashAttribute("errorMessage", "Captcha validation failed!");
            return "redirect:/register";
        }

        // wrong username
        if(user == null) {
            redirectAttributes.addFlashAttribute("errorLogin", true);
            redirectAttributes.addFlashAttribute("errorMessage", "The details provided are wrong!");
            return "redirect:/register";
        }

        // wrong email
        if(!user.getEmail().equals(userForm.getEmail())) {
            redirectAttributes.addFlashAttribute("errorLogin", true);
            redirectAttributes.addFlashAttribute("errorMessage", "The details provided are wrong!");
            return "redirect:/register";
        }

        if(userForm.getPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("errorLogin", true);
            redirectAttributes.addFlashAttribute("errorMessage", "The password should be of at least 6 characters!");
            return "redirect:/register";
        }

        if(user.getBanned()){
            redirectAttributes.addFlashAttribute("errorLogin", true);
            redirectAttributes.addFlashAttribute("errorMessage", "You are currently banned for unsuitable behaviour! Please wait for an admin to remove your restrictions");
            return "redirect:/register";
        }

        user.setPassword(bCryptPasswordEncoder.encode(userForm.getPassword()));
        userDataFacade.updateUser(user);

        redirectAttributes.addFlashAttribute("successReset", true);
        redirectAttributes.addFlashAttribute("successResetMessage", "Password successfully reset! You can now use it to login!");

        return "redirect:/register";
    }
}
