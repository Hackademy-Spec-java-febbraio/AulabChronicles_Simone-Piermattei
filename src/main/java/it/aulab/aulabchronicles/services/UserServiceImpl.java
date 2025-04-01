package it.aulab.aulabchronicles.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.aulab.aulabchronicles.dtos.UserDto;
import it.aulab.aulabchronicles.models.User;
import it.aulab.aulabchronicles.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
user.setPassword(passwordEncoder().encode(userDto.getPassword()));
userRepository.save(user);

    }

}
