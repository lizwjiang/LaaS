package org.g6.laas.server.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.g6.laas.server.database.entity.user.User;
import org.g6.laas.server.database.repository.IUserRepository;
import org.g6.laas.server.exception.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
public class LoginController {

    @Autowired
    private IUserRepository userRepository;

    @RequestMapping(value = "/controllers/login")
    @JsonView(User.UserDTO.class)
    User login(@RequestBody User request) {
        User loginingUser = userRepository.findByName(request.getName());
        if (loginingUser == null || !loginingUser.getPassword().equals(request.getPassword()))
            throw new InvalidUserException("user name or password is wrong.");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(loginingUser, loginingUser.getPassword(), loginingUser.getAuthorities());
        auth.setDetails(loginingUser.getId());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
        return loginingUser;
    }

    @RequestMapping(value = "/controllers/logout", method = RequestMethod.GET)
    public void logout(HttpSession session) {
        if (session != null)
            session.invalidate();
    }

    @RequestMapping(value = "/controllers/users/current", method = RequestMethod.GET)
    @JsonView(User.UserDTO.class)
    public User loginedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!(principal instanceof User))
            throw new InvalidUserException("login required");
        return (User)principal;
    }
}
