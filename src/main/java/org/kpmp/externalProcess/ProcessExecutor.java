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
		processBuilder.command(command);
		Process process = processBuilder.start();
		int exitVal = process.waitFor();
		return (exitVal == 0);
	}

	public CommandResult executeProcessWithOutput(String[] command) throws IOException, InterruptedException {
		CommandResult commandResult = new CommandResult();
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.command(command);
		Process process = processBuilder.start();
		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
		String commandOutput = null;
		StringBuilder sb = new StringBuilder();
		while((commandOutput = br.readLine()) != null) {
			sb.append(commandOutput);
		}
		int exitVal = process.waitFor();
		commandResult.setResult(exitVal == 0);
		commandResult.setOutput(sb.toString());
		return commandResult;
	}

}
