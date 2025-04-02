package it.aulab.aulabchronicles.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository rolesRepository;
    


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

    }

}
