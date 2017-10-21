package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.payment.ChargeRequest;
import com.tmtp.web.TMTP.repository.ShopItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShopController {

    private final UserDataFacade userDataFacade;
    private final ShopItemFacade shopItemFacade;
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;
    @Autowired
    private ShopItemRepository shopItemRepository;

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
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.GBP);
        model.addAttribute("inventory", items);
        model.addAttribute("category", "jacket");
        return "store";
    }

    @RequestMapping("/shop/shorts")
    public String storePageShorts(Model model){
        User user = userDataFacade.retrieveLoggedUser();
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

    @RequestMapping("/shop/overlays")
    public String storePageOverlays(Model model){
        User user = userDataFacade.retrieveLoggedUser();
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
        model.addAttribute("category", "overlay");
        return "store";
    }

    @RequestMapping("/shop/titles")
    public String storePageTitles(Model model){
        User user = userDataFacade.retrieveLoggedUser();
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
        return "redirect:/shop";
    }
}

/*
    ShopItem shopItem = new ShopItem();
        shopItem.setName("If you are not in the top 4 don't talk to me!");
                shopItem.setType("title");
                shopItem.setPointPrice(10);
                shopItem.setGbpPrice(99);
                shopItem.setGbpPriceFormatted("£0.99");
                shopItem.setImgPath("/img/shop/title/1.png");
                shopItemRepository.save(shopItem);

                ShopItem shopItem2 = new ShopItem();
                shopItem2.setName("If you're re not in the championship please don't talk to me!");
                shopItem2.setType("title");
                shopItem2.setPointPrice(10);
                shopItem2.setGbpPrice(99);
                shopItem2.setGbpPriceFormatted("£0.99");
                shopItem2.setImgPath("/img/shop/title/2.png");
                shopItemRepository.save(shopItem2);

                ShopItem shopItem3 = new ShopItem();
                shopItem3.setName("What is wrong with the ref is he dizzy?");
                shopItem3.setType("title");
                shopItem3.setPointPrice(10);
                shopItem3.setGbpPrice(99);
                shopItem3.setGbpPriceFormatted("£0.99");
                shopItem3.setImgPath("/img/shop/title/3.png");
                shopItemRepository.save(shopItem3);

                ShopItem shopItem4 = new ShopItem();
                shopItem4.setName("10 out of 10 for delivery");
                shopItem4.setType("title");
                shopItem4.setPointPrice(10);
                shopItem4.setGbpPrice(99);
                shopItem4.setGbpPriceFormatted("£0.99");
                shopItem4.setImgPath("/img/shop/title/4.png");
                shopItemRepository.save(shopItem4);

                ShopItem shopItem5 = new ShopItem();
                shopItem5.setName("Love the ticky tacky tackle!");
                shopItem5.setType("title");
                shopItem5.setPointPrice(10);
                shopItem5.setGbpPrice(99);
                shopItem5.setGbpPriceFormatted("£0.99");
                shopItem5.setImgPath("/img/shop/title/5.png");
                shopItemRepository.save(shopItem5);

                ShopItem shopItem6 = new ShopItem();
                shopItem6.setName("Great ball control");
                shopItem6.setType("title");
                shopItem6.setPointPrice(10);
                shopItem6.setGbpPrice(99);
                shopItem6.setGbpPriceFormatted("£0.99");
                shopItem6.setImgPath("/img/shop/title/6.png");
                shopItemRepository.save(shopItem6);

                ShopItem shopItem7 = new ShopItem();
                shopItem7.setName("That is not a pen!");
                shopItem7.setType("title");
                shopItem7.setPointPrice(10);
                shopItem7.setGbpPrice(99);
                shopItem7.setGbpPriceFormatted("£0.99");
                shopItem7.setImgPath("/img/shop/title/7.png");
                shopItemRepository.save(shopItem7);

                ShopItem shopItem8 = new ShopItem();
                shopItem8.setName("That is a pen!");
                shopItem8.setType("title");
                shopItem8.setPointPrice(10);
                shopItem8.setGbpPrice(99);
                shopItem8.setGbpPriceFormatted("£0.99");
                shopItem8.setImgPath("/img/shop/title/8.png");
                shopItemRepository.save(shopItem8);

                ShopItem shopItem9 = new ShopItem();
                shopItem9.setName("He needs to be sub");
                shopItem9.setType("title");
                shopItem9.setPointPrice(10);
                shopItem9.setGbpPrice(99);
                shopItem9.setGbpPriceFormatted("£0.99");
                shopItem9.setImgPath("/img/shop/title/9.png");
                shopItemRepository.save(shopItem9);

                ShopItem shopItem10 = new ShopItem();
                shopItem10.setName("You need to get him off the bench");
                shopItem10.setType("title");
                shopItem10.setPointPrice(10);
                shopItem10.setGbpPrice(99);
                shopItem10.setGbpPriceFormatted("£0.99");
                shopItem10.setImgPath("/img/shop/title/10.png");
                shopItemRepository.save(shopItem10);

                ShopItem shopItem11 = new ShopItem();
                shopItem11.setName("Get up you wimp!");
                shopItem11.setType("title");
                shopItem11.setPointPrice(10);
                shopItem11.setGbpPrice(99);
                shopItem11.setGbpPriceFormatted("£0.99");
                shopItem11.setImgPath("/img/shop/title/11.png");
                shopItemRepository.save(shopItem11);

                ShopItem shopItem12 = new ShopItem();
                shopItem12.setName("Change the formation!");
                shopItem12.setType("title");
                shopItem12.setPointPrice(10);
                shopItem12.setGbpPrice(99);
                shopItem12.setGbpPriceFormatted("£0.99");
                shopItem12.setImgPath("/img/shop/title/12.png");
                shopItemRepository.save(shopItem12);

                ShopItem shopItem13 = new ShopItem();
                shopItem13.setName("Get the manager out!");
                shopItem13.setType("title");
                shopItem13.setPointPrice(10);
                shopItem13.setGbpPrice(99);
                shopItem13.setGbpPriceFormatted("£0.99");
                shopItem13.setImgPath("/img/shop/title/13.png");
                shopItemRepository.save(shopItem13);

                ShopItem shopItem14 = new ShopItem();
                shopItem14.setName("We need a new board room!");
                shopItem14.setType("title");
                shopItem14.setPointPrice(10);
                shopItem14.setGbpPrice(99);
                shopItem14.setGbpPriceFormatted("£0.99");
                shopItem14.setImgPath("/img/shop/title/14.png");
                shopItemRepository.save(shopItem14);

                ShopItem shopItem15 = new ShopItem();
                shopItem15.setName("CSKAMoscow");
                shopItem15.setType("title");
                shopItem15.setPointPrice(10);
                shopItem15.setGbpPrice(99);
                shopItem15.setGbpPriceFormatted("£0.99");
                shopItem15.setImgPath("/img/shop/title/15.png");
                shopItemRepository.save(shopItem15);

                ShopItem shopItem16 = new ShopItem();
                shopItem16.setName("You need a new boardroom!");
                shopItem16.setType("title");
                shopItem16.setPointPrice(10);
                shopItem16.setGbpPrice(99);
                shopItem16.setGbpPriceFormatted("£0.99");
                shopItem16.setImgPath("/img/shop/title/16.png");
                shopItemRepository.save(shopItem16);

                ShopItem shopItem17 = new ShopItem();
                shopItem17.setName("That is a foul!");
                shopItem17.setType("title");
                shopItem17.setPointPrice(10);
                shopItem17.setGbpPrice(99);
                shopItem17.setGbpPriceFormatted("£0.99");
                shopItem17.setImgPath("/img/shop/title/17.png");
                shopItemRepository.save(shopItem17);

                ShopItem shopItem18 = new ShopItem();
                shopItem18.setName("That is not a foul!");
                shopItem18.setType("title");
                shopItem18.setPointPrice(10);
                shopItem18.setGbpPrice(99);
                shopItem18.setGbpPriceFormatted("£0.99");
                shopItem18.setImgPath("/img/shop/title/18.png");
                shopItemRepository.save(shopItem18);

                ShopItem shopItem19 = new ShopItem();
                shopItem19.setName("He is not worth that");
                shopItem19.setType("title");
                shopItem19.setPointPrice(10);
                shopItem19.setGbpPrice(99);
                shopItem19.setGbpPriceFormatted("£0.99");
                shopItem19.setImgPath("/img/shop/title/19.png");
                shopItemRepository.save(shopItem19);

                ShopItem shopItem20 = new ShopItem();
                shopItem20.setName("He is worth that");
                shopItem20.setType("title");
                shopItem20.setPointPrice(10);
                shopItem20.setGbpPrice(99);
                shopItem20.setGbpPriceFormatted("£0.99");
                shopItem20.setImgPath("/img/shop/title/20.png");
                shopItemRepository.save(shopItem20);

                ShopItem shopItem21 = new ShopItem();
                shopItem21.setName("We need to sell him");
                shopItem21.setType("title");
                shopItem21.setPointPrice(10);
                shopItem21.setGbpPrice(99);
                shopItem21.setGbpPriceFormatted("£0.99");
                shopItem21.setImgPath("/img/shop/title/21.png");
                shopItemRepository.save(shopItem21);

                ShopItem shopItem22 = new ShopItem();
                shopItem22.setName("Keep him!");
                shopItem22.setType("title");
                shopItem22.setPointPrice(10);
                shopItem22.setGbpPrice(99);
                shopItem22.setGbpPriceFormatted("£0.99");
                shopItem22.setImgPath("/img/shop/title/22.png");
                shopItemRepository.save(shopItem22);

                ShopItem shopItem23 = new ShopItem();
                shopItem23.setName("We need to buy him");
                shopItem23.setType("title");
                shopItem23.setPointPrice(10);
                shopItem23.setGbpPrice(99);
                shopItem23.setGbpPriceFormatted("£0.99");
                shopItem23.setImgPath("/img/shop/title/23.png");
                shopItemRepository.save(shopItem23);

                ShopItem shopItem24 = new ShopItem();
                shopItem24.setName("Give him the contract!");
                shopItem24.setType("title");
                shopItem24.setPointPrice(10);
                shopItem24.setGbpPrice(99);
                shopItem24.setGbpPriceFormatted("£0.99");
                shopItem24.setImgPath("/img/shop/title/24.png");
                shopItemRepository.save(shopItem24);

                ShopItem shopItem25 = new ShopItem();
                shopItem25.setName("Don’t give him a contract");
                shopItem25.setType("title");
                shopItem25.setPointPrice(10);
                shopItem25.setGbpPrice(99);
                shopItem25.setGbpPriceFormatted("£0.99");
                shopItem25.setImgPath("/img/shop/title/25.png");
                shopItemRepository.save(shopItem25);

                ShopItem shopItem26 = new ShopItem();
                shopItem26.setName("Look at our striker he is on fire!");
                shopItem26.setType("title");
                shopItem26.setPointPrice(10);
                shopItem26.setGbpPrice(99);
                shopItem26.setGbpPriceFormatted("£0.99");
                shopItem26.setImgPath("/img/shop/title/26.png");
                shopItemRepository.save(shopItem19);

                ShopItem shopItem27 = new ShopItem();
                shopItem27.setName("That player needs to get his muscles up");
                shopItem27.setType("title");
                shopItem27.setPointPrice(10);
                shopItem27.setGbpPrice(99);
                shopItem27.setGbpPriceFormatted("£0.99");
                shopItem27.setImgPath("/img/shop/title/27.png");
                shopItemRepository.save(shopItem19);

                ShopItem shopItem28 = new ShopItem();
                shopItem28.setName("Keep your position!");
                shopItem28.setType("title");
                shopItem28.setPointPrice(10);
                shopItem28.setGbpPrice(99);
                shopItem28.setGbpPriceFormatted("£0.99");
                shopItem28.setImgPath("/img/shop/title/28.png");
                shopItemRepository.save(shopItem28);

                ShopItem shopItem29 = new ShopItem();
                shopItem29.setName("You are having a laugh");
                shopItem29.setType("title");
                shopItem29.setPointPrice(10);
                shopItem29.setGbpPrice(99);
                shopItem29.setGbpPriceFormatted("£0.99");
                shopItem29.setImgPath("/img/shop/title/29.png");
                shopItemRepository.save(shopItem29);

                ShopItem shopItem30 = new ShopItem();
                shopItem30.setName("He done you like a kipper");
                shopItem30.setType("title");
                shopItem30.setPointPrice(10);
                shopItem30.setGbpPrice(99);
                shopItem30.setGbpPriceFormatted("£0.99");
                shopItem30.setImgPath("/img/shop/title/30.png");
                shopItemRepository.save(shopItem19);

                ShopItem shopItem31 = new ShopItem();
                shopItem31.setName("You're just standard");
                shopItem31.setType("title");
                shopItem31.setPointPrice(10);
                shopItem31.setGbpPrice(99);
                shopItem31.setGbpPriceFormatted("£0.99");
                shopItem31.setImgPath("/img/shop/title/31.png");
                shopItemRepository.save(shopItem31);

                ShopItem shopItem32 = new ShopItem();
                shopItem32.setName("The ref needs glasses");
                shopItem32.setType("title");
                shopItem32.setPointPrice(10);
                shopItem32.setGbpPrice(99);
                shopItem32.setGbpPriceFormatted("£0.99");
                shopItem32.setImgPath("/img/shop/title/32.png");
                shopItemRepository.save(shopItem32);

                ShopItem shopItem33 = new ShopItem();
                shopItem33.setName("What is this guy doing");
                shopItem33.setType("title");
                shopItem33.setPointPrice(10);
                shopItem33.setGbpPrice(99);
                shopItem33.setGbpPriceFormatted("£0.99");
                shopItem33.setImgPath("/img/shop/title/33.png");
                shopItemRepository.save(shopItem33);

                ShopItem shopItem34 = new ShopItem();
                shopItem34.setName("He is not a footballer");
                shopItem34.setType("title");
                shopItem34.setPointPrice(10);
                shopItem34.setGbpPrice(99);
                shopItem34.setGbpPriceFormatted("£0.99");
                shopItem34.setImgPath("/img/shop/title/34.png");
                shopItemRepository.save(shopItem34);

                ShopItem shopItem35 = new ShopItem();
                shopItem35.setName("Why didn’t you support my team?");
                shopItem35.setType("title");
                shopItem35.setPointPrice(10);
                shopItem35.setGbpPrice(99);
                shopItem35.setGbpPriceFormatted("£0.99");
                shopItem35.setImgPath("/img/shop/title/35.png");
                shopItemRepository.save(shopItem35);

                ShopItem shopItem36 = new ShopItem();
                shopItem36.setName("Your team is dead!");
                shopItem36.setType("title");
                shopItem36.setPointPrice(10);
                shopItem36.setGbpPrice(99);
                shopItem36.setGbpPriceFormatted("£0.99");
                shopItem36.setImgPath("/img/shop/title/36.png");
                shopItemRepository.save(shopItem36);

                ShopItem shopItem37 = new ShopItem();
                shopItem37.setName("Told you to come to a winning team!");
                shopItem37.setType("title");
                shopItem37.setPointPrice(10);
                shopItem37.setGbpPrice(99);
                shopItem37.setGbpPriceFormatted("£0.99");
                shopItem37.setImgPath("/img/shop/title/37.png");
                shopItemRepository.save(shopItem37);

                ShopItem shopItem38 = new ShopItem();
                shopItem38.setName("Love my team till I die");
                shopItem38.setType("title");
                shopItem38.setPointPrice(10);
                shopItem38.setGbpPrice(99);
                shopItem38.setGbpPriceFormatted("£0.99");
                shopItem38.setImgPath("/img/shop/title/38.png");
                shopItemRepository.save(shopItem38);

                ShopItem shopItem39 = new ShopItem();
                shopItem39.setName("I live eat and sleep about my team");
                shopItem39.setType("title");
                shopItem39.setPointPrice(10);
                shopItem39.setGbpPrice(99);
                shopItem39.setGbpPriceFormatted("£0.99");
                shopItem39.setImgPath("/img/shop/title/39.png");
                shopItemRepository.save(shopItem39);

                ShopItem shopItem40 = new ShopItem();
                shopItem40.setName("I give up on my team they are a joke!");
                shopItem40.setType("title");
                shopItem40.setPointPrice(10);
                shopItem40.setGbpPrice(99);
                shopItem40.setGbpPriceFormatted("£0.99");
                shopItem40.setImgPath("/img/shop/title/40.png");
                shopItemRepository.save(shopItem40);

                ShopItem shopItem41 = new ShopItem();
                shopItem41.setName("What is wrong with them?");
                shopItem41.setType("title");
                shopItem41.setPointPrice(10);
                shopItem41.setGbpPrice(99);
                shopItem41.setGbpPriceFormatted("£0.99");
                shopItem41.setImgPath("/img/shop/title/41.png");
                shopItemRepository.save(shopItem41);

                ShopItem shopItem42 = new ShopItem();
                shopItem42.setName("What is wrong with the goal keeper?");
                shopItem42.setType("title");
                shopItem42.setPointPrice(10);
                shopItem42.setGbpPrice(99);
                shopItem42.setGbpPriceFormatted("£0.99");
                shopItem42.setImgPath("/img/shop/title/42.png");
                shopItemRepository.save(shopItem42);

                ShopItem shopItem43 = new ShopItem();
                shopItem43.setName("What is wrong with this player?");
                shopItem43.setType("title");
                shopItem43.setPointPrice(10);
                shopItem43.setGbpPrice(99);
                shopItem43.setGbpPriceFormatted("£0.99");
                shopItem43.setImgPath("/img/shop/title/43.png");
                shopItemRepository.save(shopItem43);

                ShopItem shopItem44 = new ShopItem();
                shopItem44.setName("That player needs to change his hairstyle");
                shopItem44.setType("title");
                shopItem44.setPointPrice(10);
                shopItem44.setGbpPrice(99);
                shopItem44.setGbpPriceFormatted("£0.99");
                shopItem44.setImgPath("/img/shop/title/44.png");
                shopItemRepository.save(shopItem44);

                ShopItem shopItem45 = new ShopItem();
                shopItem45.setName("Pass the ball!");
                shopItem45.setType("title");
                shopItem45.setPointPrice(10);
                shopItem45.setGbpPrice(99);
                shopItem45.setGbpPriceFormatted("£0.99");
                shopItem45.setImgPath("/img/shop/title/45.png");
                shopItemRepository.save(shopItem45);

                ShopItem shopItem46 = new ShopItem();
                shopItem46.setName("What a clown!");
                shopItem46.setType("title");
                shopItem46.setPointPrice(10);
                shopItem46.setGbpPrice(99);
                shopItem46.setGbpPriceFormatted("£0.99");
                shopItem46.setImgPath("/img/shop/title/46.png");
                shopItemRepository.save(shopItem45);

                ShopItem shopItem47 = new ShopItem();
                shopItem47.setName("What a legend!");
                shopItem47.setType("title");
                shopItem47.setPointPrice(10);
                shopItem47.setGbpPrice(99);
                shopItem47.setGbpPriceFormatted("£0.99");
                shopItem47.setImgPath("/img/shop/title/47.png");
                shopItemRepository.save(shopItem47);

                ShopItem shopItem48 = new ShopItem();
                shopItem48.setName("That is not offside!");
                shopItem48.setType("title");
                shopItem48.setPointPrice(10);
                shopItem48.setGbpPrice(99);
                shopItem48.setGbpPriceFormatted("£0.99");
                shopItem48.setImgPath("/img/shop/title/48.png");
                shopItemRepository.save(shopItem48);

                ShopItem shopItem49 = new ShopItem();
                shopItem49.setName("What is the offside rule because this is a joke?");
                shopItem49.setType("title");
                shopItem49.setPointPrice(10);
                shopItem49.setGbpPrice(99);
                shopItem49.setGbpPriceFormatted("£0.99");
                shopItem49.setImgPath("/img/shop/title/49.png");
                shopItemRepository.save(shopItem49);

                ShopItem shopItem50 = new ShopItem();
                shopItem50.setName("That is a red card");
                shopItem50.setType("title");
                shopItem50.setPointPrice(10);
                shopItem50.setGbpPrice(99);
                shopItem50.setGbpPriceFormatted("£0.99");
                shopItem50.setImgPath("/img/shop/title/50.png");
                shopItemRepository.save(shopItem50);

                ShopItem shopItem51 = new ShopItem();
                shopItem51.setName("That is a yellow card");
                shopItem51.setType("title");
                shopItem51.setPointPrice(10);
                shopItem51.setGbpPrice(99);
                shopItem51.setGbpPriceFormatted("£0.99");
                shopItem51.setImgPath("/img/shop/title/51.png");
                shopItemRepository.save(shopItem51);

                ShopItem shopItem52 = new ShopItem();
                shopItem52.setName("Ref call the game off!");
                shopItem52.setType("title");
                shopItem52.setPointPrice(10);
                shopItem52.setGbpPrice(99);
                shopItem52.setGbpPriceFormatted("£0.99");
                shopItem52.setImgPath("/img/shop/title/52.png");
                shopItemRepository.save(shopItem52);

                ShopItem shopItem53 = new ShopItem();
                shopItem53.setName("That was great banter");
                shopItem53.setType("title");
                shopItem53.setPointPrice(10);
                shopItem53.setGbpPrice(99);
                shopItem53.setGbpPriceFormatted("£0.99");
                shopItem53.setImgPath("/img/shop/title/53.png");
                shopItemRepository.save(shopItem53);

                ShopItem shopItem54 = new ShopItem();
                shopItem54.setName("Too funny lol");
                shopItem54.setType("title");
                shopItem54.setPointPrice(10);
                shopItem54.setGbpPrice(99);
                shopItem54.setGbpPriceFormatted("£0.99");
                shopItem54.setImgPath("/img/shop/title/54.png");
                shopItemRepository.save(shopItem54);

                ShopItem shopItem55 = new ShopItem();
                shopItem55.setName("Don’t piss me off!");
                shopItem55.setType("title");
                shopItem55.setPointPrice(10);
                shopItem55.setGbpPrice(99);
                shopItem55.setGbpPriceFormatted("£0.99");
                shopItem55.setImgPath("/img/shop/title/55.png");
                shopItemRepository.save(shopItem55);

                ShopItem shopItem56 = new ShopItem();
                shopItem56.setName("What a goal!");
                shopItem56.setType("title");
                shopItem56.setPointPrice(10);
                shopItem56.setGbpPrice(99);
                shopItem56.setGbpPriceFormatted("£0.99");
                shopItem56.setImgPath("/img/shop/title/56.png");
                shopItemRepository.save(shopItem56);

                ShopItem shopItem57 = new ShopItem();
                shopItem57.setName("What a miss!");
                shopItem57.setType("title");
                shopItem57.setPointPrice(10);
                shopItem57.setGbpPrice(99);
                shopItem57.setGbpPriceFormatted("£0.99");
                shopItem57.setImgPath("/img/shop/title/57.png");
                shopItemRepository.save(shopItem57);

                ShopItem shopItem58 = new ShopItem();
                shopItem58.setName("That ball went over the moon");
                shopItem58.setType("title");
                shopItem58.setPointPrice(10);
                shopItem58.setGbpPrice(99);
                shopItem58.setGbpPriceFormatted("£0.99");
                shopItem58.setImgPath("/img/shop/title/58.png");
                shopItemRepository.save(shopItem58);

                ShopItem shopItem59 = new ShopItem();
                shopItem59.setName("That ball is still travelling");
                shopItem59.setType("title");
                shopItem59.setPointPrice(10);
                shopItem59.setGbpPrice(99);
                shopItem59.setGbpPriceFormatted("£0.99");
                shopItem59.setImgPath("/img/shop/title/59.png");
                shopItemRepository.save(shopItem59);

                ShopItem shopItem60 = new ShopItem();
                shopItem60.setName("Get off the pitch you joker!");
                shopItem60.setType("title");
                shopItem60.setPointPrice(10);
                shopItem60.setGbpPrice(99);
                shopItem60.setGbpPriceFormatted("£0.99");
                shopItem60.setImgPath("/img/shop/title/60.png");
                shopItemRepository.save(shopItem60);

                ShopItem shopItem61 = new ShopItem();
                shopItem61.setName("He is injured");
                shopItem61.setType("title");
                shopItem61.setPointPrice(10);
                shopItem61.setGbpPrice(99);
                shopItem61.setGbpPriceFormatted("£0.99");
                shopItem61.setImgPath("/img/shop/title/61.png");
                shopItemRepository.save(shopItem61);

                ShopItem shopItem62 = new ShopItem();
                shopItem62.setName("He is back on the pitch");
                shopItem62.setType("title");
                shopItem62.setPointPrice(10);
                shopItem62.setGbpPrice(99);
                shopItem62.setGbpPriceFormatted("£0.99");
                shopItem62.setImgPath("/img/shop/title/62.png");
                shopItemRepository.save(shopItem62);

                ShopItem shopItem63 = new ShopItem();
                shopItem63.setName("Give the player a chance");
                shopItem63.setType("title");
                shopItem63.setPointPrice(10);
                shopItem63.setGbpPrice(99);
                shopItem63.setGbpPriceFormatted("£0.99");
                shopItem63.setImgPath("/img/shop/title/63.png");
                shopItemRepository.save(shopItem63);

                ShopItem shopItem64 = new ShopItem();
                shopItem64.setName("Do you know any great football chants?");
                shopItem64.setType("title");
                shopItem64.setPointPrice(10);
                shopItem64.setGbpPrice(99);
                shopItem64.setGbpPriceFormatted("£0.99");
                shopItem64.setImgPath("/img/shop/title/64.png");
                shopItemRepository.save(shopItem64);

                ShopItem shopItem65 = new ShopItem();
                shopItem65.setName("He is a beast!");
                shopItem65.setType("title");
                shopItem65.setPointPrice(10);
                shopItem65.setGbpPrice(99);
                shopItem65.setGbpPriceFormatted("£0.99");
                shopItem65.setImgPath("/img/shop/title/65.png");
                shopItemRepository.save(shopItem65);

                ShopItem shopItem66 = new ShopItem();
                shopItem66.setName("He looks like the Hulk");
                shopItem66.setType("title");
                shopItem66.setPointPrice(10);
                shopItem66.setGbpPrice(99);
                shopItem66.setGbpPriceFormatted("£0.99");
                shopItem66.setImgPath("/img/shop/title/66.png");
                shopItemRepository.save(shopItem66);

                ShopItem shopItem67 = new ShopItem();
                shopItem67.setName("We are champions!");
                shopItem67.setType("title");
                shopItem67.setPointPrice(10);
                shopItem67.setGbpPrice(99);
                shopItem67.setGbpPriceFormatted("£0.99");
                shopItem67.setImgPath("/img/shop/title/67.png");
                shopItemRepository.save(shopItem67);

                ShopItem shopItem68 = new ShopItem();
                shopItem68.setName("We are winners do not talk to me!");
                shopItem68.setType("title");
                shopItem68.setPointPrice(10);
                shopItem68.setGbpPrice(99);
                shopItem68.setGbpPriceFormatted("£0.99");
                shopItem68.setImgPath("/img/shop/title/68.png");
                shopItemRepository.save(shopItem68);

                ShopItem shopItem69 = new ShopItem();
                shopItem69.setName("We’ve won trophies, have you?");
                shopItem69.setType("title");
                shopItem69.setPointPrice(10);
                shopItem69.setGbpPrice(99);
                shopItem69.setGbpPriceFormatted("£0.99");
                shopItem69.setImgPath("/img/shop/title/69.png");
                shopItemRepository.save(shopItem69);

                ShopItem shopItem70 = new ShopItem();
                shopItem70.setName("What have you won this season?");
                shopItem70.setType("title");
                shopItem70.setPointPrice(10);
                shopItem70.setGbpPrice(99);
                shopItem70.setGbpPriceFormatted("£0.99");
                shopItem70.setImgPath("/img/shop/title/70.png");
                shopItemRepository.save(shopItem70);

                ShopItem shopItem71 = new ShopItem();
                shopItem71.setName("When you have won something then talk to me!");
                shopItem71.setType("title");
                shopItem71.setPointPrice(10);
                shopItem71.setGbpPrice(99);
                shopItem71.setGbpPriceFormatted("£0.99");
                shopItem71.setImgPath("/img/shop/title/71.png");
                shopItemRepository.save(shopItem65);

                ShopItem shopItem72 = new ShopItem();
                shopItem72.setName("I don't watch football on a Thursday");
                shopItem72.setType("title");
                shopItem72.setPointPrice(10);
                shopItem72.setGbpPrice(99);
                shopItem72.setGbpPriceFormatted("£0.99");
                shopItem72.setImgPath("/img/shop/title/72.png");
                shopItemRepository.save(shopItem72);*/