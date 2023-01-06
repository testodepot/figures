package pl.kurs.figures.security;

import org.springframework.stereotype.Service;
import pl.kurs.figures.exception.EntityNotFoundException;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRole(String roleName){
        return roleRepository.getRoleByName(roleName);
    }

    public Role getRoleById(Integer id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(Long.valueOf(id), "Role"));
    }


    public boolean existsById(Integer id) {
        return roleRepository.existsById(id);
    }
}
