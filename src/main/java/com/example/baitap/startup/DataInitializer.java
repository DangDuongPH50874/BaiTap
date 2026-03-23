package com.example.baitap.startup;

import com.example.baitap.domain.RoleName;
import com.example.baitap.entity.RoleEntity;
import com.example.baitap.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        ensureRole(RoleName.USER);
        ensureRole(RoleName.MANAGER);
    }

    private void ensureRole(RoleName name) {
        roleRepository.findByName(name).orElseGet(() -> roleRepository.save(new RoleEntity(name)));
    }
}

