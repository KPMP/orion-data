package org.kpmp.packages.validation;

import java.io.IOException;
import java.util.ArrayList;
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
		List<String> globusFiles = getGlobusFileNames(request);
		List<String> filesFromMetadata = processIncomingFilenames(request);
		response.setFilesFromMetadata(filesFromMetadata);
		response.setFilesInGlobus(globusFiles);

		for (String filename : filesFromMetadata) {
			if (!globusFiles.contains(filename)) {
				response.addMetadataFileNotFoundInGlobus(filename);
			}
		}

		for (String fileInGlobus : globusFiles) {
			if (!filesFromMetadata.contains(fileInGlobus) && !fileInGlobus.startsWith("METADATA")) {
				response.addGlobusFileNotFoundInMetadata(fileInGlobus);
			}
		}
		response.setPackageId(request.getPackageId());
		return response;
	}

	private List<String> processIncomingFilenames(PackageFilesRequest request) {
		List<String> incomingFiles = new ArrayList<>();
		String filenameString = request.getFilenames();
		String[] filenames = filenameString.split(",");
		for (String filename : filenames) {
			String[] newlineSplit = filename.split("\n");
			for (String file : newlineSplit) {
				incomingFiles.add(file.trim());
			}
		}
		return incomingFiles;
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
