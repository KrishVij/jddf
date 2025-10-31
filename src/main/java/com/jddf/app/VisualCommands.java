package com.jddf.app;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.File;
import java.nio.file.Paths;

@Command(name = "visual", description = "deals with how your document looks, difference from DocumentsCommand is that this focuses on how every component make your pdf look")
public class VisualCommands implements Runnable {

	public void run() {}

	@Command(name = "--dark-mode", description = "Turns your pdf into a dark mode pdf the \"--\" makes this list of commands different from other commands")
	public void visualDarkMode(@Parameters(paramLabel = "Document to convert to dark mdoe")File pdfile, String outFile) throws Exception {

		if (pdfile == null || !pdfile.getName().toLowerCase().endsWith(".pdf")) {

			throw new IllegalArgumentException("Provided file is not a PDF document.");
		}else if (outFile == null || !outFile.toLowerCase().endsWith(".pdf")) {

			throw new IllegalArgumentException("Provided output file is not a PDF document.");
		}
		
		App app = new App();
		try (PDDocument document = Loader.loadPDF(pdfile)){

			app.darkMode(document, Paths.get(outFile));
		}catch(Exception e) {

			e.printStackTrace();
		}
	}
}
