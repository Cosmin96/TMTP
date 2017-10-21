package com.tmtp.web.TMTP.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.web.ShopItemFacade;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChargeController {

    @Autowired
    private StripeService paymentsService;
    @Autowired
    private UserDataFacade userDataFacade;
    @Autowired
    private ShopItemFacade shopItemFacade;

    @PostMapping("/charge/{id}")
    public String charge(@PathVariable("id") String id, ChargeRequest chargeRequest, Model model, RedirectAttributes redirectAttributes) throws StripeException {
        User user = userDataFacade.retrieveLoggedUser();
        ShopItem shopItem = shopItemFacade.retrieveItemById(id);
        for(ShopItem item : user.getShopItems()) {
            if (item.getName().equals(shopItem.getName())) {
                redirectAttributes.addFlashAttribute("error", true);
                redirectAttributes.addFlashAttribute("message", "You already own this item!");
                return "redirect:/shop";
            }
        }

        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentsService.charge(chargeRequest);


        user.getShopItems().add(shopItem);
        userDataFacade.updateUser(user);

        return "redirect:/shop";
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }


}
