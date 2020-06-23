package org.kpmp.packages.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kpmp.globus.GlobusFileListing;
import org.kpmp.globus.GlobusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PackageFilesValidationService {

	private GlobusService globus;

	@Autowired
	public PackageFilesValidationService(GlobusService globus) {
		this.globus = globus;
	}

	public PackageValidationResponse matchFiles(PackageFilesRequest request)
			throws JsonProcessingException, IOException {
		PackageValidationResponse response = new PackageValidationResponse();
		List<String> deliveredFiles = getGlobusFileNames(request);
		List<String> givenFilenames = processIncomingFilenames(request);
		for (String filename : givenFilenames) {
			if (!deliveredFiles.contains(filename)) {
				response.addMetadataFileNotFoundInGlobus(filename);
			}
		}
		for (String fileInGlobus : deliveredFiles) {
			if (!givenFilenames.contains(fileInGlobus)) {
				response.addGlobusFileNotFoundInMetadata(fileInGlobus);
			}
		}
		response.setPackageId(request.getPackageId());
		return response;
	}

	private List<String> processIncomingFilenames(PackageFilesRequest request) {
		String filenameString = request.getFilenames();
		filenameString.replaceAll(",", "\n");
		String[] filenames = filenameString.split("\n");

		return Arrays.asList(filenames);
	}

	private List<String> getGlobusFileNames(PackageFilesRequest request) throws JsonProcessingException, IOException {
		List<GlobusFileListing> filesAtEndpoint = globus.getFilesAtEndpoint(request.getPackageId());
		List<String> deliveredFiles = new ArrayList<String>();
		for (GlobusFileListing globusListingResponse : filesAtEndpoint) {
			deliveredFiles.add(globusListingResponse.getName());
		}
		return deliveredFiles;
	}

}
