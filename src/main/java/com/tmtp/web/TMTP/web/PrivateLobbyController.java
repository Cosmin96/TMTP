package com.tmtp.web.TMTP.web;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import com.tmtp.web.TMTP.payment.StripeService;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
public class PrivateLobbyController {

    private final PrivateLobbyFacade privateLobbyFacade;
    private final UserDataFacade userDataFacade;
    private final StripeService paymentService;
    private final ChatMessageRepository chatMessageRepository;
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    public PrivateLobbyController(final PrivateLobbyFacade privateLobbyFacade,
                                  final UserDataFacade userDataFacade,
                                  final StripeService paymentService,
                                  final ChatMessageRepository chatMessageRepository) {
        this.privateLobbyFacade = privateLobbyFacade;
        this.userDataFacade = userDataFacade;
        this.paymentService = paymentService;
        this.chatMessageRepository = chatMessageRepository;
    }

    @RequestMapping("/privateLobby/{id}")
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
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
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
        return "privatelobby";
    }

    @RequestMapping("/privateLobby/join/{id}")
    public String joinLobby(@PathVariable("id") String id, ChargeRequest chargeRequest) throws StripeException{
        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentService.charge(chargeRequest);

        PrivateLobby privateLobby = privateLobbyFacade.findById(id);
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }
        List<String> joinedUsers = privateLobby.getJoinedUsers();
        joinedUsers.add(user.getUsername());
        privateLobby.setJoinedUsers(joinedUsers);
        privateLobbyFacade.updateLobby(privateLobby);

        return "redirect:/privateLobby/" + privateLobby.getId();
    }

    @RequestMapping("/lobby/create/{paymentType}")
    public String createLobby(@PathVariable("paymentType") String paymentType, ChargeRequest chargeRequest, RedirectAttributes redirectAttributes) throws StripeException{
        User user = userDataFacade.retrieveLoggedUser();
        if(paymentType.equals("card")){
            chargeRequest.setDescription("Payment");
            chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
            Charge charge = paymentService.charge(chargeRequest);
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

        return "redirect:/privateLobby/" + privateLobby.getId();
    }
}
