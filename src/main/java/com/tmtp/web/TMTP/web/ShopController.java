package com.tmtp.web.TMTP.web;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import com.tmtp.web.TMTP.payment.StripeService;
import com.tmtp.web.TMTP.repository.ShopItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class ShopController {

    private final UserDataFacade userDataFacade;
    private final ShopItemFacade shopItemFacade;
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;
    @Autowired
    private ShopItemRepository shopItemRepository;
    @Autowired
    private StripeService paymentsService;

    public ShopController(final UserDataFacade userDataFacade,
                          final ShopItemFacade shopItemFacade) {
        this.userDataFacade = userDataFacade;
        this.shopItemFacade = shopItemFacade;
    }

    @RequestMapping("/shop")
    public String storePage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/shop/shorts")
    public String storePageShorts(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/shop/socks")
    public String storePageSocks(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/shop/footballs")
    public String storePageFootballs(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/shop/trophies")
    public String storePageTrophies(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/shop/overlays")
    public String storePageOverlays(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/shop/titles")
    public String storePageTitles(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "store";
    }

    @RequestMapping("/buy/{id}")
    public String buyWithPoints(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes){
        ShopItem shopItem = shopItemFacade.retrieveItemById(id);
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
        }
        for(ShopItem item : user.getShopItems()) {
            if (item.getName().equals(shopItem.getName())) {
                redirectAttributes.addFlashAttribute("error", true);
                redirectAttributes.addFlashAttribute("message", "You already own this item!");
                return "redirect:/shop";
            }
        }
        user.getPoints().setGreen(user.getPoints().getGreen() - shopItem.getPointPrice());
        user.getShopItems().add(shopItem);
        userDataFacade.updateUser(user);
        redirectAttributes.addFlashAttribute("confirmation", true);
        redirectAttributes.addFlashAttribute("boughtItem", shopItem.getName());
        return "redirect:/shop";
    }

    @RequestMapping("/buy/chest/{type}")
    public String buyChest(@PathVariable("type") String type, ChargeRequest chargeRequest,
                           Model model, RedirectAttributes redirectAttributes) throws StripeException{
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "redirect:/shop";
    }
}