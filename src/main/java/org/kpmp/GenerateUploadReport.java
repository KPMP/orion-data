package org.kpmp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kpmp.packages.CustomPackageRepository;
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

	@SuppressWarnings("rawtypes")
	@Override
	public void run(String... args) throws Exception {
		List<JSONObject> jsons = packageRepository.findAll();
		List<Map> packageDatas = new ArrayList<Map>();
		for (JSONObject packageInfo : jsons) {
			Map<String, String> packageData = new LinkedHashMap<>();
			JSONObject submitter = packageInfo.getJSONObject("submitter");
			String submitterName = submitter.getString("firstName") + " " + submitter.getString("lastName");
			packageData.put("Package ID", packageInfo.getString("_id"));
			packageData.put("Submitter", submitterName);
			packageData.put("TIS", packageInfo.getString("tisName"));
			packageData.put("Specimen ID", packageInfo.getString("subjectId"));
			if (packageInfo.has("tisInternalExperimentID")) {
				packageData.put("TIS Internal Experiment ID", packageInfo.getString("tisInternalExperimentID"));
			} else {
				packageData.put("TIS Internal Experiment ID", "N/A");
			}
			if (packageInfo.has("dataGenerators")) {
				packageData.put("Data Generator(s)", packageInfo.getString("dataGenerators"));
			} else {
				packageData.put("Data Generator(s)", "N/A");
			}
			packageData.put("Package Type", packageInfo.getString("packageType"));
			packageData.put("Protocol", packageInfo.getString("protocol"));
			packageData.put("Dataset Description", packageInfo.getString("description"));
			packageData.put("Created At", packageInfo.getString("createdAt"));
			JSONArray files = packageInfo.getJSONArray("files");
			StringBuilder fileNames = new StringBuilder();
			for (int i = 0; i < files.length(); i++) {
				JSONObject file = files.getJSONObject(i);
				if (fileNames.length() != 0) {
					fileNames.append(", ");
				} else {
					fileNames.append("\"");
				}
				fileNames.append(file.get("fileName"));
			}
			fileNames.append("\"");
			packageData.put("Files", fileNames.toString());
			packageDatas.add(packageData);
		}

		// Now write the results out to a csv?
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
	}

}
