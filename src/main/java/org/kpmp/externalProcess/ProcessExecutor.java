package org.kpmp.externalProcess;

import java.io.IOException;

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
		int exitVal = process.waitFor();
		if (exitVal == 0) {
			processSuccessful = true;
		} else {
			processSuccessful = false;
		}
		return processSuccessful;
	}

}
