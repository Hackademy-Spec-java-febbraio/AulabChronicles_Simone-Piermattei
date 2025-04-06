package it.aulab.aulabchronicles.services;

import it.aulab.aulabchronicles.models.CareerRequest;
import it.aulab.aulabchronicles.models.User;

public interface CareerRequestService {

    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);

    void save(CareerRequest careerRequest, User user);

    void careerAccept(Long requestId);

    CareerRequest find(Long id);

}
