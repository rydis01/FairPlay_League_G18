package controller;

import org.springframework.web.bind.annotation.*;
import service.UserService;

@RestController
@RequestMapping("/api")
public class LoginController {

    UserService userService;

    LoginController(UserService userService){
        this.userService = userService;

    }

    @PostMapping("/login")
    public boolean login(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password);
    }
}
