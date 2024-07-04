package academy.mischok.modules.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    // the /auth/microsoft endpoint should redirect to the frontend
    @RequestMapping("/microsoft")
    public String microsoft() {
        return "redirect:https://academy-u202309-031-2febaeeb9a88.herokuapp.com/";
    }
}
