package org.kpmp.packages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageTypeIconController {

    private PackageTypeIconRepository packageTypeIconRepository;

    @Autowired
    public PackageTypeIconController(PackageTypeIconRepository packageTypeIconRepository) {
        this.packageTypeIconRepository = packageTypeIconRepository;
    }

    @RequestMapping(value = "/v1/packageTypeIcons", method = RequestMethod.GET)
    public @ResponseBody
    List<PackageTypeIcon> getAllPackageTypeIcons() {
        return packageTypeIconRepository.findAll();
    }

}
