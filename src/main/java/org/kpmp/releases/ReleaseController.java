package org.kpmp.releases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.List;

@Controller
public class ReleaseController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final MessageFormat releaseRequest = new MessageFormat("Request|{0}");
    private ReleaseRepository repository;

    @Autowired
    public ReleaseController(ReleaseRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/v1/releases", method = RequestMethod.GET)
    public @ResponseBody
    List<Release> getMetadataRelease() {
        log.info(releaseRequest.format(new Object[]{"getMetadataRelease"}));
        return this.repository.findAll();
    }

    @RequestMapping(value = "/v1/releases/version/{version}", method = RequestMethod.GET)
    public @ResponseBody Release getMetadataReleaseByVersion(@PathVariable String version) {
        log.info(releaseRequest.format(new Object[]{"getMetadataReleaseByVersion"}));
        return this.repository.findByVersion(version);
    }
}