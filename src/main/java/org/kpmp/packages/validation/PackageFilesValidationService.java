package org.kpmp.packages.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

			// now we have a full Globus listing and the full set of files we expect to find
			for (String filename : filesFromMetadata) {
				String[] fileParts = filename.split("/");
				if (fileParts.length == 1) {
					// if this file is not at the top level AND
					if (!filesInGlobusDirectories.get("").contains(filename) && !filesInGlobusDirectories.containsKey(filename)) {
						response.addMetadataFileNotFoundInGlobus(filename);
					}
				} else {
					String directoryPath = "";
					for (String filePart : fileParts) {
						directoryPath = directoryPath + "/" + filePart;
						if (!filesInGlobusDirectories.containsKey(directoryPath)) { 
							// if we are missing the directory for this file, we def don't have the file
							response.addMetadataFileNotFoundInGlobus(filename);
						}
					}

				}


			}




			// for (String filename : filesFromMetadata) {
			// 	String[] fileParts = filename.split("/");
				
			// 	if (fileParts.length == 1) {
			// 		// did the split on the filename, and there were no slashes in it, so either a top level directory, or a top level file
			// 		if (!globusFiles.contains(filename) && !globusDirectories.contains(filename)) {
			// 			// we don't have this file or directory in globus, mark it as missing
			// 			response.addMetadataFileNotFoundInGlobus(filename);
			// 		}
				
			// 	} else if (fileParts.length > 1) {
			// 		// did the split and there was at least one slash in the filename (meaning there was a directory and filename at least)
			// 		int depth = 1;
			// 		String additionalPath = "";
			// 		for (String filePart : fileParts) {
			// 			additionalPath = "/" + filePart;
			// 			// take the directory name and get the file listing from Globus for this path
			// 			if (!filesInGlobusDirectories.containsKey(additionalPath)) {
			// 				List<GlobusFileListing> globusFilesInSubdirectory = globus.getFilesAndDirectoriesAtEndpoint(request.getPackageId() + "/" + additionalPath);
			// 				filesInGlobusDirectories.put(additionalPath, globusFilesInSubdirectory);
			// 			}
			// 			if (fileParts.length == depth) {
			// 				// we are at the last part (either filename or empty directory), do we have this in Globus
			// 				List<GlobusFileListing> globusFilesInSubdirectory = filesInGlobusDirectories.get(currentDirectory);
			// 				List<String> globusFileNames = getGlobusFileNames(globusFilesInSubdirectory);
			// 				if (!globusFileNames.contains(fileParts[depth])) {
			// 					response.addMetadataFileNotFoundInGlobus(filename);
			// 				}
			// 			} 
			// 			depth++;		
			// 		}
					
			// 	}
			// }


			// now we have an ALMOST full listing of what is in Globus

			for (String fileInGlobus : globusFiles) {
				if (!filesFromMetadata.contains(fileInGlobus) && !fileInGlobus.startsWith("METADATA")) {
					response.addGlobusFileNotFoundInMetadata(fileInGlobus);
				}
			}
		}

		
		return response;
	}

	protected List<String> filenamesNotInGlobus(Map<String, List<String>> globusListing, List<String> expectedFiles) {
		List<String> missingFiles = new ArrayList<>();
		missingFiles = filenamesNotInGlobus(missingFiles, globusListing, expectedFiles, "");
		return missingFiles;
	}

	private List<String> filenamesNotInGlobus(List<String> missingFiles, Map<String, List<String>> globusListing, List<String> expectedFiles, String initialDirectory) {
		List<String> filesInGlobus = globusListing.get(initialDirectory);
		for (String filename : expectedFiles) {
			if (filename.contains("/")) {
				int i = filename.lastIndexOf("/");
				String[] fileParts =  {filename.substring(0, i+1), filename.substring(i+1)};
				if (!globusListing.containsKey(fileParts[0]) && !globusListing.get(fileParts[0]).contains(fileParts[1])) {
					missingFiles.add(filename);
				}
			} else {
				if (filesInGlobus == null || !filesInGlobus.contains(filename)) {
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
