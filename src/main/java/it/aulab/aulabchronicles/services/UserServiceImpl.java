package it.aulab.aulabchronicles.services;

import java.util.List;

import org.springframework.security.core.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.aulab.aulabchronicles.dtos.UserDto;
import it.aulab.aulabchronicles.models.Role;
import it.aulab.aulabchronicles.models.User;
import it.aulab.aulabchronicles.repositories.RoleRepository;
import it.aulab.aulabchronicles.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;




@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository rolesRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    


    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(UserDto userDto, RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response) {
User user = new User();
user.setUsername(userDto.getFirstName() + "" + userDto.getLastName());
user.setEmail(userDto.getEmail());
user.setPassword(passwordEncoder.encode(userDto.getPassword()));

Role role = rolesRepository.findByName("ROLE_USER");
user.setRoles(List.of(role));

userRepository.save(user);

authenticateUserAndSetSession(user, userDto, request);

    }

    public void authenticateUserAndSetSession(User user, UserDto userDto, HttpServletRequest request) {

        try {
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        }catch (AuthenticationException e){
            e.printStackTrace();
        }
    }



}
