package org.kpmp.packages;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.dmd.DmdResponse;
import org.kpmp.dmd.DmdService;
import org.kpmp.globus.GlobusService;
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

@Controller
public class PackageController {

	@Value("${package.state.upload.started}")
	private String uploadStartedState;
	@Value("${package.state.metadata.received}")
	private String metadataReceivedState;
	@Value("${package.state.upload.failed}")
	private String uploadFailedState;
	@Value("${package.state.upload.recalled}")
	private String uploadRecalledState;
    @Value("${user.auth.host}")
	private String userAuthHost;
    @Value("${user.auth.endpoint}")
	private String userAuthEndpoint;
    @Value("#{'${user.auth.allowed.groups}'.split(',')}")
	private List<String> allowedGroups;
	@Value("${user.auth.kpmp.group}")
	private String kpmpGroup;
    private static final String CLIENT_ID_PROPERTY = "CLIENT_ID";
    private static final String GROUPS_KEY = "groups";
	private LoggingService logger;
	private PackageService packageService;
	private ShibbolethUserService shibUserService;
	private UniversalIdGenerator universalIdGenerator;
	private GlobusService globusService;
    private Environment env;

	private DmdService dmdService;

	@Autowired
	public PackageController(PackageService packageService, LoggingService logger,
			ShibbolethUserService shibUserService, UniversalIdGenerator universalIdGenerator,
			GlobusService globusService, DmdService dmdService, Environment env) {
		this.packageService = packageService;
		this.logger = logger;
		this.shibUserService = shibUserService;
		this.universalIdGenerator = universalIdGenerator;
		this.globusService = globusService;
		this.dmdService = dmdService;
        this.env = env;
	}

	@RequestMapping(value = "/v1/packages", params="shouldExclude", method = RequestMethod.GET)
	public @ResponseBody List<PackageView> getAllPackages( @RequestParam("shouldExclude") boolean shouldExclude, HttpServletRequest request)
			throws JSONException, IOException {
		
		if (shouldExclude) {
			logger.logInfoMessage(this.getClass(), null, "Request for filtered packages", request);
			return packageService.findMostPackages();
		} else {
			logger.logInfoMessage(this.getClass(), null, "Request for all packages", request);
			return packageService.findAllPackages();
		}
		
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody PackageResponse postPackageInformation(@RequestBody String packageInfoString,
			@RequestParam("hostname") String hostname, HttpServletRequest request) {
		String cleanHostName = hostname.replace("=", "");
		PackageResponse packageResponse = new PackageResponse();
		String packageId = universalIdGenerator.generateUniversalId();
		packageResponse.setPackageId(packageId);
		packageService.sendStateChangeEvent(packageId, uploadStartedState, null, cleanHostName);
		JSONObject packageInfo;
		try {
			packageInfo = new JSONObject(packageInfoString);
			logger.logInfoMessage(this.getClass(), packageId, "Posting package info: " + packageInfo, request);
			packageInfo.put("largeFilesChecked", true);
			User user = shibUserService.getUserNoHeaders(request, packageInfo);
			packageService.savePackageInformation(packageInfo, user, packageId);
			String largeFilesChecked = packageInfo.optBoolean("largeFilesChecked") ? "true" : "false";
			if ("true".equals(largeFilesChecked)) {
				packageResponse.setGlobusURL(globusService.createDirectory(packageId));
			}
			packageService.sendStateChangeEvent(packageId, metadataReceivedState, largeFilesChecked,
					packageResponse.getGlobusURL(), cleanHostName);
		} catch (Exception e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			packageService.sendStateChangeEvent(packageId, uploadFailedState, null, e.getMessage(), cleanHostName);
		}
		return packageResponse;
	}

    public boolean isAllowed(JSONArray userGroups) throws JSONException {
		for (int i = 0; i < userGroups.length(); i++) {
			String group = userGroups.getString(i);
			if (allowedGroups.contains(group)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "/v1/packages/{packageId}/recall", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity recallPackage(@PathVariable String packageId,
			@RequestParam("hostname") String hostname, @RequestParam("shibId") String shibId, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        String clientId = env.getProperty(CLIENT_ID_PROPERTY);
		String uri = userAuthHost + userAuthEndpoint + "/" + clientId + "/" + shibId;
		String cleanHostName = hostname.replace("=", "");
        String cleanShibId = shibId.replace("=", "");
		ResponseEntity responseEntity;
		PackageResponse packageResponse = new PackageResponse();
		try {
            ResponseEntity<String> userInfoResponse = restTemplate.getForEntity(uri, String.class);
            String userInfo = userInfoResponse.getBody();
            JSONObject userJson = new JSONObject(userInfo);
            JSONArray userGroups = userJson.getJSONArray(GROUPS_KEY);
            if (isAllowed(userGroups) && userJson.getBoolean("active")) {

                packageResponse.setPackageId(packageId);
                logger.logInfoMessage(this.getClass(), packageId, "Recalling package: " + packageId, request);
                packageResponse.setGlobusURL(globusService.getTopDirectory(packageId));
                dmdService.recallPackage(packageId, packageResponse.getGlobusURL());
                packageService.sendStateChangeEvent(packageId, uploadRecalledState, "true", packageResponse.getGlobusURL(), cleanHostName);
                String successMessage = "Sucessfully recalled package " + packageId;
                logger.logInfoMessage(this.getClass(), packageId, successMessage, request);
                responseEntity = ResponseEntity.ok().body(successMessage);

            }else {
                String errorMessage = "User " + cleanShibId + " is not allowed to recall package " + packageId;
                logger.logErrorMessage(this.getClass(), packageId, errorMessage, request);
                responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
            }
		} catch (Exception e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			responseEntity = ResponseEntity.status(INTERNAL_SERVER_ERROR).body("An error occurred while recalling the package.");
		}
		return responseEntity;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/v1/packages/{packageId}/files/move", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity movePackageFiles(@PathVariable String packageId, HttpServletRequest request) {
		ResponseEntity responseEntity;
		DmdResponse dmdResponse;
		try {
			logger.logInfoMessage(this.getClass(), packageId, "Moving files for package " + packageId, request);
			dmdResponse = dmdService.moveFiles(packageId);
			if (dmdResponse.isSuccess()) {
				String successMessage = "The following files were moved successfully: " + String.join(",", dmdResponse.getFileNameList());
				logger.logInfoMessage(this.getClass(), packageId, successMessage, request);
				responseEntity = ResponseEntity.ok().body(successMessage);
			} else {
				logger.logErrorMessage(this.getClass(), packageId, dmdResponse.getMessage(), request);
				responseEntity = ResponseEntity.status(INTERNAL_SERVER_ERROR).body("The following problem occurred while moving the files: " + dmdResponse.getMessage());
			}
		} catch (IOException e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			responseEntity = ResponseEntity.status(INTERNAL_SERVER_ERROR).body("There was a server error while moving the files.");
		}
		return responseEntity;
	}
}
