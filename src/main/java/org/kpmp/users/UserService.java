package org.kpmp.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kpmp.packages.Package;
import org.kpmp.packages.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private PackageRepository packageRepository;

    @Autowired
    public UserService(UserRepository userRepository, PackageRepository packageRepository) {
        this.userRepository = userRepository;
        this.packageRepository = packageRepository;
    }

    public List<User> findAllWithPackages() {
        List<User> users = userRepository.findAll();
        List<Package> packages = packageRepository.findAll();
        Iterator it = users.iterator();
        List<String> userIds = new ArrayList<>();

        for (Package aPackage : packages) {
            userIds.add(aPackage.getSubmitter().getId());
        }

        while(it.hasNext()) {
            User thisUser = (User) it.next();
            if (!userIds.contains(thisUser.id)) {
                it.remove();
            }
        }
        return users;
    }

}
