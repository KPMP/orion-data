package org.kpmp.packages.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kpmp.globus.GlobusFileListing;
import org.kpmp.globus.GlobusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageFilesValidationService {

	private GlobusService globus;

	@Autowired
	public PackageFilesValidationService(GlobusService globus) {
		this.globus = globus;
	}

	public PackageValidationResponse matchFiles(PackageFilesRequest request)
			throws IOException {
		PackageValidationResponse response = new PackageValidationResponse();
		List<GlobusFileListing> filesInGlobus = null;
		try {
			filesInGlobus = globus.getFilesAtEndpoint(request.getPackageId());
			response.setDirectoryExists(true);
		} catch (IOException e) {
			if (e.getMessage().contains("ClientError.NotFound")) {
				response.setDirectoryExists(false);
			} else {
				throw e;
			}
		}

		if (response.getDirectoryExists()) {
			if (containsOnlyDirectory(filesInGlobus)) {
				filesInGlobus = globus.getFilesAtEndpoint(request.getPackageId() + "/" + filesInGlobus.get(0).getName());
			}

			List<String> globusFiles = getGlobusFileNames(filesInGlobus);
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
		}

		response.setPackageId(request.getPackageId());
		return response;
	}

	private boolean containsOnlyDirectory(List<GlobusFileListing> filesInGlobus) {
		if (filesInGlobus.size() == 1 && filesInGlobus.get(0).getType().equals("dir")) {
			return true;
		}
		return false;
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

	private List<String> getGlobusFileNames(List<GlobusFileListing> globusFiles) {
		List<String> deliveredFiles = new ArrayList<String>();
		for (GlobusFileListing globusListingResponse : globusFiles) {
			deliveredFiles.add(globusListingResponse.getName());
		}
		return deliveredFiles;
	}

}
