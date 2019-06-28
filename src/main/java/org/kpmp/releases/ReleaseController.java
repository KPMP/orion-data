package org.kpmp.releases;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReleaseController {

	private ReleaseRepository repository;
	private LoggingService logger;

	@Autowired
	public ReleaseController(ReleaseRepository repository, LoggingService logger) {
		this.repository = repository;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/releases", method = RequestMethod.GET)
	public @ResponseBody List<Release> getMetadataRelease(HttpServletRequest request) {
		logger.logInfoMessage(this.getClass(), null, null, request.getRequestURI(), "Getting all release information");
		return this.repository.findAll();
	}

	@RequestMapping(value = "/v1/releases/version/{version}", method = RequestMethod.GET)
	public @ResponseBody Release getMetadataReleaseByVersion(@PathVariable String version, HttpServletRequest request) {
		logger.logInfoMessage(this.getClass(), null, null, request.getRequestURI(),
				"Getting release information for version " + version);
		return this.repository.findByVersion(version);
	}
}