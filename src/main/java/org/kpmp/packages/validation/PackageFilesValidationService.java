package org.kpmp.packages.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kpmp.globus.GlobusFileListing;
import org.kpmp.globus.GlobusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PackageFilesValidationService {

	private GlobusService globus;
	private static final String DIRECTORY_TYPE = "dir";

	@Autowired
	public PackageFilesValidationService(GlobusService globus) {
		this.globus = globus;
	}

	public PackageValidationResponse matchFiles(PackageFilesRequest request)
			throws IOException {
		PackageValidationResponse response = new PackageValidationResponse();
		List<GlobusFileListing> objectsInGlobus = null;
		try {
			objectsInGlobus = globus.getFilesAndDirectoriesAtEndpoint(request.getPackageId());
			response.setDirectoryExists(true);
		} catch (IOException e) {
			if (e.getMessage().contains("ClientError.NotFound")) {
				response.setDirectoryExists(false);
			} else {
				throw e;
			}
		}

		if (response.getDirectoryExists()) {
			if (containsOnlyDirectory(objectsInGlobus)) {
				objectsInGlobus = globus.getFilesAndDirectoriesAtEndpoint(request.getPackageId() + "/" + objectsInGlobus.get(0).getName());
			}

			List<String> globusFiles = getGlobusFileNames(objectsInGlobus);
			List<String> globusDirectories = getGlobusDirectoryNames(objectsInGlobus);
			List<String> filesFromMetadata = processIncomingFilenames(request);

			response.setFilesFromMetadata(filesFromMetadata);
			response.setPackageId(request.getPackageId());
			response.setFilesInGlobus(globusFiles);
			response.setDirectoriesInGlobus(globusDirectories);

			Map<String, List<String>> filesInGlobusDirectories = new HashMap<>();
			// put top level files in our map
			filesInGlobusDirectories.put("", globusFiles);
			String currentDirectory = "";
			filesInGlobusDirectories = processGlobusDirectory(filesInGlobusDirectories, globusDirectories, request.getPackageId(), currentDirectory);


			List<String> filesMissingInGlobus = filenamesNotInGlobus(filesInGlobusDirectories, filesFromMetadata);
			response.setMetadataFilesNotFoundInGlobus(filesMissingInGlobus);
			response.setGlobusFileNotFoundInMetadata(filenamesNotInMetadata(filesInGlobusDirectories, filesFromMetadata));

		}

		
		return response;
	}

	protected List<String> filenamesNotInMetadata(Map<String, List<String>> globusListing, List<String> metadataFiles) {
		List<String> missingFiles = new ArrayList<>();
		Set<String> directories = globusListing.keySet();
		for (String directory : directories) {
			List<String> filesInDirectory = globusListing.get(directory);
			for (String file : filesInDirectory) {
				String fileWithPath = directory + "/" + file;
				if (directory == "") {
					fileWithPath = file;
				}
				if (!metadataFiles.contains(fileWithPath) && !file.startsWith("METADATA_")) {
					missingFiles.add(fileWithPath);
				}
			}
		}
		return missingFiles;
	}

	protected List<String> filenamesNotInGlobus(Map<String, List<String>> globusListing, List<String> expectedFiles) {
		List<String> missingFiles = new ArrayList<>();
		List<String> filesInGlobusAtRoot = globusListing.get("");
		for (String filename : expectedFiles) {
			if (filename.contains("/")) {
				int i = filename.lastIndexOf("/");
				String[] fileParts =  {filename.substring(0, i+1), filename.substring(i+1)};
				if (!globusListing.containsKey(fileParts[0]) || !globusListing.get(fileParts[0]).contains(fileParts[1])) {
					missingFiles.add(filename);
				}
			} else {
				if (filesInGlobusAtRoot == null || !filesInGlobusAtRoot.contains(filename)) {
					missingFiles.add(filename);
				}	
			}
		}
		return missingFiles;
	}

	// This is a side-effecty, recursive method. It fills in the filesInGlobusDirectories
	protected Map<String, List<String>> processGlobusDirectory(Map<String, List<String>> globusListing,  
		List<String> globusDirectories, String packageId, String initialDirectory) throws JsonProcessingException, IOException {

		for (String globusDirectory : globusDirectories) {
			String currentDirectory = initialDirectory + "/" + globusDirectory;
			List<GlobusFileListing> globusFilesInSubdirectory = globus.getFilesAndDirectoriesAtEndpoint(packageId + currentDirectory);
			List<String> globusFiles = getGlobusFileNames(globusFilesInSubdirectory);
			globusListing.put(currentDirectory, globusFiles);
			List<String> globusSubDirectories = getGlobusDirectoryNames(globusFilesInSubdirectory);
			if (globusSubDirectories.size() > 0) {
				processGlobusDirectory(globusListing, globusSubDirectories, packageId, currentDirectory);
			}
		}
		return globusListing;
	}


	private boolean containsOnlyDirectory(List<GlobusFileListing> filesInGlobus) {
		if (filesInGlobus.size() == 1 && filesInGlobus.get(0).getType().equals(DIRECTORY_TYPE)) {
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

	private List<String> getGlobusDirectoryNames(List<GlobusFileListing> globusFiles) {
		List<String> globusDirectories = new ArrayList<>();
		for (GlobusFileListing globusFileListing : globusFiles) {
			if (globusFileListing.getType().equals(DIRECTORY_TYPE)) {
				globusDirectories.add(globusFileListing.getName());
			}
		}
		return globusDirectories;
	}

	private List<String> getGlobusFileNames(List<GlobusFileListing> globusFiles) {
		List<String> deliveredFiles = new ArrayList<String>();
		for (GlobusFileListing globusListingResponse : globusFiles) {
			if (!globusListingResponse.getType().equals(DIRECTORY_TYPE)) {
				deliveredFiles.add(globusListingResponse.getName());
			}
		}
		return deliveredFiles;
	}

}
