package org.kpmp.packages.validation;

import java.io.IOException;
import java.util.ArrayList;
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
			List<String> globusFiles = getGlobusFileNames(objectsInGlobus);
			List<String> globusDirectories = getGlobusDirectoryNames(objectsInGlobus);
			List<String> filesFromMetadata = processIncomingFilenames(request);

			String baseDirectory = "";
			// if we only have a top level folder, ignore it and go down one to start validation
			if (globusFiles.size() == 0 && globusDirectories.size() == 1) {
				baseDirectory = globusDirectories.get(0);
				objectsInGlobus = globus.getFilesAndDirectoriesAtEndpoint(request.getPackageId() + "/" +baseDirectory);
				globusFiles = getGlobusFileNames(objectsInGlobus);
				globusDirectories = getGlobusDirectoryNames(objectsInGlobus);
			}

			response.setFilesFromMetadata(filesFromMetadata);
			response.setPackageId(request.getPackageId());

			Map<String, List<String>> filesInGlobusDirectories = new HashMap<>();
			// put top level files in our map
			
			filesInGlobusDirectories.put("", globusFiles);
			filesInGlobusDirectories = processGlobusDirectory(filesInGlobusDirectories, globusDirectories, request.getPackageId(), baseDirectory);
			List<String> filePathsInGlobus = getGlobusFilePaths(filesInGlobusDirectories, baseDirectory);

			response.setFilesInGlobus(filePathsInGlobus);

			List<String> filesMissingInGlobus = filenamesNotInGlobus(filePathsInGlobus, filesFromMetadata);
			if (filesMissingInGlobus.size() > 0) {
				response.setMetadataFilesNotFoundInGlobus(filesMissingInGlobus);
			}
			List<String> filesMissingInMetadata = filenamesNotInMetadata(filePathsInGlobus, filesFromMetadata);
			if (filesMissingInMetadata.size() > 0) {
				response.setGlobusFileNotFoundInMetadata(filesMissingInMetadata);
			}
			

		}

		
		return response;
	}

	protected List<String> getGlobusFilePaths(Map<String, List<String>> filesInGlobusDirectories, String baseDirectory) {
		List<String> filePaths = new ArrayList<>();
		Set<String> directories = filesInGlobusDirectories.keySet();
		for (String directory : directories) {
			List<String> filesInDirectory = filesInGlobusDirectories.get(directory);
			if (filesInDirectory.size() == 0 && directory != baseDirectory) {
				directory = directory.replaceAll(baseDirectory + "/$", "");
				filePaths.add(directory);
			}
			for (String file : filesInDirectory) {
				directory = directory.replaceAll(baseDirectory + "/$", "");
				String fileWithPath = directory + "/" + file;
				if (directory == "") {
					fileWithPath = file;
				}
				filePaths.add(fileWithPath);
			}
			
		}
		return filePaths;
	}

	protected List<String> filenamesNotInMetadata(List<String> globusListing, List<String> metadataFiles) {
		List<String> missingFiles = new ArrayList<>();

		for (String globusFile : globusListing) {
			if (!metadataFiles.contains(globusFile) && !globusFile.startsWith("METADATA_")) {
				missingFiles.add(globusFile);
			}
		}

		return missingFiles;
	}

	protected List<String> filenamesNotInGlobus(List<String> globusListing, List<String> expectedFiles) {
		List<String> missingFiles = new ArrayList<>();
		for (String fileFromMetadata : expectedFiles) {
			if (!globusListing.contains(fileFromMetadata)) {
				missingFiles.add(fileFromMetadata);
			}
		}
		return missingFiles;
	}

	// This is a side-effecty, recursive method. It fills in the filesInGlobusDirectories
	protected Map<String, List<String>> processGlobusDirectory(Map<String, List<String>> globusListing,  
		List<String> globusDirectories, String packageId, String initialDirectory) throws JsonProcessingException, IOException {

		for (String globusDirectory : globusDirectories) {
			String prefix = "";
			if (initialDirectory != "") {
				prefix = initialDirectory + "/";
			}
			String currentDirectory = prefix + globusDirectory;

			List<GlobusFileListing> globusFilesInSubdirectory = globus.getFilesAndDirectoriesAtEndpoint(packageId + "/" + currentDirectory);
			List<String> globusFiles = getGlobusFileNames(globusFilesInSubdirectory);
			globusListing.put(currentDirectory, globusFiles);
			List<String> globusSubDirectories = getGlobusDirectoryNames(globusFilesInSubdirectory);
			if (globusSubDirectories.size() > 0) {
				processGlobusDirectory(globusListing, globusSubDirectories, packageId, currentDirectory);
			}
		}
		return globusListing;
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

	protected List<String> getGlobusDirectoryNames(List<GlobusFileListing> globusFiles) {
		List<String> globusDirectories = new ArrayList<>();
		for (GlobusFileListing globusFileListing : globusFiles) {
			if (globusFileListing.getType().equals(DIRECTORY_TYPE)) {
				globusDirectories.add(globusFileListing.getName());
			}
		}
		return globusDirectories;
	}

	protected List<String> getGlobusFileNames(List<GlobusFileListing> globusFiles) {
		List<String> deliveredFiles = new ArrayList<String>();
		for (GlobusFileListing globusListingResponse : globusFiles) {
			if (!globusListingResponse.getType().equals(DIRECTORY_TYPE)) {
				deliveredFiles.add(globusListingResponse.getName());
			}
		}
		return deliveredFiles;
	}

}
