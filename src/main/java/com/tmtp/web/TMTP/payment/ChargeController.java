package com.tmtp.web.TMTP.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChargeController {

    @Autowired
    private StripeService paymentsService;
    @Autowired
    private UserDataFacade userDataFacade;

    @PostMapping("/charge/{id}")
    public String charge(@PathVariable("id") String id, ChargeRequest chargeRequest, Model model) throws StripeException {
        chargeRequest.setDescription("Payment");
        chargeRequest.setCurrency(ChargeRequest.Currency.GBP);
        Charge charge = paymentsService.charge(chargeRequest);

        User user = userDataFacade.retrieveLoggedUser();
        updateInventory(user, id);
        userDataFacade.updateUser(user);

        return "redirect:/shop";
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }

    private void updateInventory(User user, String id){
        switch(id){
            case "s":
                break;
            default:
                break;
        }
    }

}
