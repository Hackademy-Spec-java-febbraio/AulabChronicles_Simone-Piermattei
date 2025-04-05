package it.aulab.aulabchronicles.services;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.aulabchronicles.dtos.UserDto;
import it.aulab.aulabchronicles.models.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    void saveUser(UserDto userDto, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response);
    User findUserByEmail(String email);
    User find(Long id);
    
}


