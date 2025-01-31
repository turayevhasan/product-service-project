package uz.pdp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.pdp.dto.RegisterUserDTO;
import uz.pdp.service.AuthUserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthUserService userService;

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(required = false, name = "error") String error) {
        ModelAndView modelAndView = new ModelAndView("/auth/login");
        modelAndView.addObject("error", error);
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/auth/registration");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView postRegister(@ModelAttribute RegisterUserDTO dto) {
        ModelAndView view = new ModelAndView("/auth/registration");
        try {
            userService.save(dto, false);
            view.addObject("error", "We have sent confirmation link to your email. Please check your email box and confirm your account !!!");
        } catch (RuntimeException e) {
            view.addObject("error", e.getMessage());
        }
        return view;
    }

    @GetMapping("/register/confirm/account/{confirmationCode}")
    public ModelAndView confirmAccount(@PathVariable(name = "confirmationCode") String confirmationCode) {
        boolean activated = userService.activateAccount(confirmationCode);
        ModelAndView view = new ModelAndView();
        if (activated) {
            view.setViewName("/auth/login");
            view.addObject("success", "Your account is successfully activated. Now, you can login");
        } else {
            view.setViewName("/auth/registration");
            view.addObject("error", "Link is expired !!!");
        }
        return view;
    }
}