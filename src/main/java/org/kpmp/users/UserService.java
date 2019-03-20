package org.kpmp.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kpmp.packages.Package;
import org.kpmp.packages.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private PackageRepository packageRepository;

    @Autowired
    public UserService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<User> findAllWithPackages() {
        List<Package> packages = packageRepository.findAll();
        Map<String, User> users = new HashMap<>();

        for (Package aPackage : packages) {
            users.put(aPackage.getSubmitter().getId(), aPackage.getSubmitter());
        }

        return new ArrayList<>(users.values());
    }

}
