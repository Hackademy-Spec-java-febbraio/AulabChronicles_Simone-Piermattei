package it.aulab.aulabchronicles.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.aulab.aulabchronicles.models.CareerRequest;
import it.aulab.aulabchronicles.models.Role;
import it.aulab.aulabchronicles.models.User;
import it.aulab.aulabchronicles.repositories.CareerRequestRepository;
import it.aulab.aulabchronicles.repositories.RoleRepository;
import it.aulab.aulabchronicles.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    

    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserId();

        if (!allUserIds.contains(user.getId())) {
            return false;
        }

        List<Long> requests = careerRequestRepository.findByUserId(user.getId());
        return requests.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    @Override
    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        // Invio email di richiesta del ruolo all'admin
        emailService.sendSimpleEmail("adminAulabPost@admin.com",
                "Richiesta per ruolo: " + careerRequest.getRole().getName(),
                "C'é una nuova Richiesta di Collaborazione da parte di " + user.getUsername());
    }

    @Override
    public void careerAccept(Long requestId) {
        // Recupero della richiesta
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        // Dalla richiesta estraggo l'utente richiedente ed il ruolo richiesto
        User user = request.getUser();
        Role role = request.getRole();

        // Recupero tutti i ruoli che l'utente giá possiede ed aggiungo quello nuovo
        List<Role> roleUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        roleUser.add(newRole);

        // Salvo tutte le nuove modifiche
        user.setRoles(roleUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Ruolo abilitato", "Ciao, la tua richiesta di collaborazione é stata accettata!");
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }

}
