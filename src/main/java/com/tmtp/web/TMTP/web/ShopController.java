package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ShopController {

    private final UserDataFacade userDataFacade;
    private final ShopItemFacade shopItemFacade;
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    public ShopController(final UserDataFacade userDataFacade,
                          final ShopItemFacade shopItemFacade) {
        this.userDataFacade = userDataFacade;
        this.shopItemFacade = shopItemFacade;
    }

    @RequestMapping("/scores")
    public String scoresPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "livescores";
    }

    @RequestMapping("/shop")
    public String storePage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        List<ShopItem> items = shopItemFacade.retrieveAllItems();
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("items", items);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);

        return "store";
    }

    @RequestMapping("/buy/{id}")
    public String buyWithPoints(@PathVariable("id") String id, Model model){
        ShopItem shopItem = shopItemFacade.retrieveItemById(id);
        User user = userDataFacade.retrieveLoggedUser();
        user.getPoints().setGreen(user.getPoints().getGreen() - shopItem.getPointPrice());
        userDataFacade.updateUser(user);
        return "redirect:/shop";
    }
}
