package com.jddf.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;


@Command(name = "jddf", version = "jddf-1.0", mixinStandardHelpOptions = true, subcommands = {

		ImageCommands.class,
		TextCommands.class,
		VisualCommands.class,
		DocumentCommands.class
	})
public class UserInterface implements Runnable {
}

@Command(name = "text", description = "work with texts inside pdfs")
public class TextCommands implements Runnable {

	@Command(name = "extract", description = "Extracts all the text from a document")
	public void extract(@Parameters(paramLabel = "PDF") File pdf) throws Exception {

		App app = new App();
		try (PDDocument doc = Loader.loadPDF(pdf)) {

			String  text = app.extractText(doc);
			System.out.println(text);
		}catch(Exception e) {

			e.printStacktrace();
		}
	}

	@Command(name = "color-info", description = "Find the color that is used to write the text in the document")
	public void extractColor(@Parameters(paramLabel = "PDF") File pdf) throws Exception {

		App app = new App();
		try (PDDocument doc = Loader.loadPDF(pdf)) {

			String textColor = app.extractTextColor(doc);
			System.out.println(textColor);
		}catch(Exception e) {

			e.printStacktrace();
		}
	}

	@Command(name = "color", description = "Changes the color of the text in the document")
	public void setColor(@Parameters(index = 0, paramLabel = "PDF") File	pdf,
						 @Parameters(index = 1, paramLabel = "color")String r,
						 @Parameters(index = 2, paramLabel = "color")String b,
						 @Parameters(index = 3, paramLabel = "color")String b) throws Exception {

		App app = new App();
		try (PDDocument doc = Loader.loadPDF(pdf)) {

			String	textColor = app.setTextColor(doc);
		}catch(Exception e) {

			e.printStacktrace();
		}
	}
}
																			   
