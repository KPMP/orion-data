package org.kpmp.externalProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Component;

@Component
public class ProcessExecutor {

	/*
	 * Developer note: This method is intended as a wrapper around ProcessBuilder to
	 * enable easier testing of calling methods. Since the implementation here is
	 * very straightforward, and ProcessBuilder is final (making it very difficult
	 * to mock), we are going to forego any unit testing of this method
	 */
	public boolean executeProcess(String[] command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		boolean processSuccessful = false;
		processBuilder.command(command);
		Process process = processBuilder.start();
		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while((line=br.readLine())!=null) System.out.println(line);
		int exitVal = process.waitFor();
		if (exitVal == 0) {
			processSuccessful = true;
		} else {
			processSuccessful = false;
		}
		return processSuccessful;
	}

	public String executeProcessWithOutput(String[] command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		boolean processSuccessful = false;
		processBuilder.command(command);
		Process process = processBuilder.start();
		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line=br.readLine())!=null) sb.append(line);
		int exitVal = process.waitFor();
		return sb.toString();
	}

}
