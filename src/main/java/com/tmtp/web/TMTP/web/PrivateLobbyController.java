package com.tmtp.web.TMTP.web;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import com.tmtp.web.TMTP.payment.StripeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class PrivateLobbyController {

    private final PrivateLobbyFacade privateLobbyFacade;
    private final UserDataFacade userDataFacade;
    private final StripeService paymentService;

    public PrivateLobbyController(final PrivateLobbyFacade privateLobbyFacade,
                                  final UserDataFacade userDataFacade,
                                  final StripeService paymentService) {
        this.privateLobbyFacade = privateLobbyFacade;
        this.userDataFacade = userDataFacade;
        this.paymentService = paymentService;
    }

    @RequestMapping("/privateLobby/{id}")
    public String openLobby(@PathVariable("id") String id, Model model){
        PrivateLobby privateLobby = privateLobbyFacade.findById(id);
        User user = userDataFacade.retrieveLoggedUser();
        boolean owner = false;
        boolean joined = false;
        model.addAttribute("lobby", privateLobby);
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
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
        return "privatelobby";
    }

    @RequestMapping("/privateLobby/join/{id}")
    public String joinLobby(@PathVariable("id") String id, ChargeRequest chargeRequest) throws StripeException{
        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentService.charge(chargeRequest);

        PrivateLobby privateLobby = privateLobbyFacade.findById(id);
        User user = userDataFacade.retrieveLoggedUser();
        List<String> joinedUsers = privateLobby.getJoinedUsers();
        joinedUsers.add(user.getUsername());
        privateLobby.setJoinedUsers(joinedUsers);
        privateLobbyFacade.updateLobby(privateLobby);

        return "redirect:/privateLobby/" + privateLobby.getId();
    }

    @RequestMapping("/lobby/create")
    public String createLobby(ChargeRequest chargeRequest) throws StripeException{
        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentService.charge(chargeRequest);

        User user = userDataFacade.retrieveLoggedUser();
        user.setPrivateLobby(true);
        userDataFacade.updateUser(user);

        PrivateLobby privateLobby = new PrivateLobby();
        privateLobby.setCreator(user.getUsername());
        privateLobby.setJoinedUsers(Collections.emptyList());
        privateLobbyFacade.createLobby(privateLobby);
        privateLobby = privateLobbyFacade.findByCreator(user.getUsername());

        return "redirect:/privateLobby/" + privateLobby.getId();
    }
}
