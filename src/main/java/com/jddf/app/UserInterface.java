package com.jddf.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "jddf", version = "jddf-1.0", mixinStandardHelpOptions = true, subcommands = {

		ImageCommands.class,
		TextCommands.class,
		VisualCommands.class,
		DocumentCommands.class
	})
public class UserInterface implements Runnable {

	@Override
	public void run() {}
	
	public static void main(String[] args) {

		int exitCode = new CommandLine(new UserInterface()).execute(args); 
		System.exit(exitCode); 
	}
}

