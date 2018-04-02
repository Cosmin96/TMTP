package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LobbiesController {

    private final UserDataFacade userDataFacade;
    private final ChatMessageRepository chatMessageRepository;

    public LobbiesController(final UserDataFacade userDataFacade, final  ChatMessageRepository chatMessageRepository) {
        this.userDataFacade = userDataFacade;
        this.chatMessageRepository = chatMessageRepository;
    }

    @RequestMapping("/lobbies/{type}")
    public String scorePage(@PathVariable ("type") String type, Model model){
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
        model.addAttribute("lobbyType", type);
        return "lobbies";
    }

    @RequestMapping("/lobby/{league}")
    public String lobbyPage(@PathVariable("league") String league, Model model){
        int n = 0;
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "redirect:/scores";
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
        return "league";
    }
}
