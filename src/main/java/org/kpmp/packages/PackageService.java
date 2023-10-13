package org.kpmp.packages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.dmd.DmdService;
import org.kpmp.logging.LoggingService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class PackageService {
	private CustomPackageRepository packageRepository;
	private DmdService dmdService;
	private StateHandlerService stateHandler;
	@Value("#{'${packageType.exclusions}'.split(',')}")
	private List<String> packageTypesToExclude;

	@Autowired
	public PackageService(CustomPackageRepository packageRepository, StateHandlerService stateHandler, DmdService dmdService, LoggingService logger) {
		this.packageRepository = packageRepository;
		this.stateHandler = stateHandler;
		this.dmdService = dmdService;
	}
	public List<PackageView> findAllPackages() throws JSONException, IOException {
		List<JSONObject> jsons = packageRepository.findAll();
		List<PackageView> packageViews = new ArrayList<>();
		Map<String, State> stateMap = stateHandler.getState();
		for (JSONObject packageToCheck : jsons) {
			PackageView packageView = new PackageView(packageToCheck);
			String packageId = packageToCheck.getString("_id");
			packageView.setState(stateMap.get(packageId));
			packageViews.add(packageView);
		}
		return packageViews;
	}

	public List<PackageView> findMostPackages() throws JSONException, IOException {
		List<JSONObject> jsons = packageRepository.findAll();
		List<PackageView> packageViews = new ArrayList<>();
		Map<String, State> stateMap = stateHandler.getState();
		for (JSONObject packageToCheck : jsons) {
			String packageType = packageToCheck.getString("packageType");
			if (!packageTypesToExclude.contains(packageType)) {
				PackageView packageView = new PackageView(packageToCheck);
				String packageId = packageToCheck.getString("_id");
				packageView.setState(stateMap.get(packageId));
				packageViews.add(packageView);
			}
		}
		return packageViews;
	}

	public String savePackageInformation(JSONObject packageMetadata, User user, String packageId) throws JSONException {
		packageRepository.saveDynamicForm(packageMetadata, user, packageId);
		Package myPackage = packageRepository.findByPackageId(packageId);
		dmdService.convertAndSendNewPackage(myPackage);
		return packageId;
	}

	public Package findPackage(String packageId) {
		return packageRepository.findByPackageId(packageId);
	}
	@CacheEvict(value = "packages", allEntries = true)
	public void sendStateChangeEvent(String packageId, String stateString, String largeFilesChecked, String origin) {
		stateHandler.sendStateChange(packageId, stateString, largeFilesChecked, null, origin);
	}

	@CacheEvict(value = "packages", allEntries = true)
	public void sendStateChangeEvent(String packageId, String stateString, String largeFilesChecked, String codicil,
			String origin) {
		stateHandler.sendStateChange(packageId, stateString, largeFilesChecked, codicil, origin);
	}

}
