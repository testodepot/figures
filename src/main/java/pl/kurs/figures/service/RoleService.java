package pl.kurs.figures.service;

import org.springframework.stereotype.Service;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.repository.RoleRepository;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRole(String roleName){
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadEntityException("role of this name not found!"));
    }

}
