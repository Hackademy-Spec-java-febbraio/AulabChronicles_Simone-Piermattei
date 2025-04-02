package it.aulab.aulabchronicles.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.aulab.aulabchronicles.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
