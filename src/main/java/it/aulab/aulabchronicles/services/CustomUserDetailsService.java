//*Da notare che abbiamo utilizzato anche una funzione getAuthorities() dove viene restituita una collezione di autorizzazioni ( GrantedAuthority ). Queste autorizzazioni rappresentano i permessi assegnati a un utente autenticato.


package it.aulab.aulabchronicles.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import it.aulab.aulabchronicles.models.Role;
import it.aulab.aulabchronicles.models.User;
import it.aulab.aulabchronicles.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }


    //* Questa funzione come dice la parola stessa mappera i ruoli collegati al nostro utente.
    
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles;
    if (roles.size() != 0) {
        mapRoles = roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());
        
    } else {
        mapRoles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        
    }
    return mapRoles;
    }


}
