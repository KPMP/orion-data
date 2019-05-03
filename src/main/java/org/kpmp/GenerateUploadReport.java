package org.kpmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kpmp.packages.CustomPackageRepository;
import org.kpmp.packages.PackageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.kpmp" })
public class GenerateUploadReport implements CommandLineRunner {

	private CustomPackageRepository packageRepository;

	@Autowired
	public GenerateUploadReport(CustomPackageRepository packageRepository) {
		this.packageRepository = packageRepository;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GenerateUploadReport.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void run(String... args) throws Exception {
		List<JSONObject> jsons = packageRepository.findAll();
		List<Map> packageDatas = new ArrayList<Map>();
		for (JSONObject packageInfo : jsons) {
			Map<String, String> packageData = new LinkedHashMap<>();
			JSONObject submitter = packageInfo.getJSONObject(PackageKeys.SUBMITTER.getKey());
			String submitterName = submitter.getString(PackageKeys.FIRST_NAME.getKey()) + " "
					+ submitter.getString(PackageKeys.LAST_NAME.getKey());
			packageData.put("Package ID", packageInfo.getString(PackageKeys.ID.getKey()));
			packageData.put("Submitter", submitterName);
			packageData.put("TIS Name", packageInfo.getString(PackageKeys.TIS_NAME.getKey()));
			packageData.put("Specimen ID", packageInfo.getString(PackageKeys.SUBJECT_ID.getKey()));
			if (packageInfo.has(PackageKeys.TIS_INTERNAL_EXPERIMENT_ID.getKey())) {
				packageData.put("TIS Internal Experiment ID",
						packageInfo.getString(PackageKeys.TIS_INTERNAL_EXPERIMENT_ID.getKey()));
			} else {
				packageData.put("TIS Internal Experiment ID", "N/A");
			}
			if (packageInfo.has(PackageKeys.DATA_GENERATORS.getKey())) {
				packageData.put("Data Generator(s)", packageInfo.getString(PackageKeys.DATA_GENERATORS.getKey()));
			} else {
				packageData.put("Data Generator(s)", "N/A");
			}
			packageData.put("Package Type", packageInfo.getString(PackageKeys.PACKAGE_TYPE.getKey()));
			packageData.put("Protocol", packageInfo.getString(PackageKeys.PROTOCOL.getKey()));
			String description = packageInfo.getString(PackageKeys.DESCRIPTION.getKey());
			description = description.replace("\n", " ");
			description = description.replace("\r", " ");
			description = description.replace("\r\n", " ");
			packageData.put("Dataset Description", description);
			packageData.put("Created At", packageInfo.getString(PackageKeys.CREATED_AT.getKey()));
			JSONArray files = packageInfo.getJSONArray(PackageKeys.FILES.getKey());
			StringBuilder fileNames = new StringBuilder();
			for (int i = 0; i < files.length(); i++) {
				JSONObject file = files.getJSONObject(i);
				if (fileNames.length() != 0) {
					fileNames.append(", ");
				} else {
					fileNames.append("\"");
				}
				fileNames.append(file.get(PackageKeys.FILE_NAME.getKey()));
			}
			fileNames.append("\"");
			packageData.put("Files", fileNames.toString());
			packageDatas.add(packageData);
		}

		writeToCSV(packageDatas);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeToCSV(List<Map> packageDatas) throws FileNotFoundException, IOException {
		FileOutputStream report = new FileOutputStream(new File("report.csv"));
		Map<String, Object> firstPackage = packageDatas.get(0);
		Set<String> headers = firstPackage.keySet();

		int count = 0;
		for (String header : headers) {
			if (count > 0) {
				report.write(",".getBytes());
			}
			report.write(header.getBytes());
			count++;
		}
		report.write(System.lineSeparator().getBytes());

		count = 0;
		for (Map packageData : packageDatas) {
			for (String key : headers) {
				if (count > 0) {
					report.write(",".getBytes());
				}
				if (packageData.get(key) == null) {
					report.write("N/A".getBytes());
				} else {
					report.write(packageData.get(key).toString().getBytes());
				}
				count++;
			}
			report.write(System.lineSeparator().getBytes());
			count = 0;
		}
		report.close();
	}

}
