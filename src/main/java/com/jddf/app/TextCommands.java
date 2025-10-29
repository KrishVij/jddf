package com.jddf.app;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.nio.file.Paths;

@Command(name = "text", description = "work with texts inside pdfs")
public class TextCommands implements Runnable {

	@Override
	public void run() {}

	@Command(name = "extract", description = "Extracts all the text from a document")
	public void extract(@Parameters(paramLabel = "PDF") File pdf) throws Exception {

		App app = new App();
		try (PDDocument doc = Loader.loadPDF(pdf)) {

			String  text = app.extractText(doc);
			System.out.println(text);
		}catch(Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "color-info", description = "Find the color that is used to write the text in the document")
	public void extractColor(@Parameters(paramLabel = "PDF") File pdf) throws Exception {

		App app = new App();
		try (PDDocument doc = Loader.loadPDF(pdf)) {

			String textColor = app.extractTextColor(doc);
			System.out.println(textColor);
		}catch(Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "color", description = "Changes the color of the text in the document")
	public void setColor(@Parameters(paramLabel = "PDF") File pdf,
						 @Parameters(paramLabel = "R", description = "Red Color value in range 0.0-1.0")String r,
						 @Parameters(paramLabel = "G", description = "Green Color value in range 0.0-1.0")String g,
						 @Parameters(paramLabel = "B", description = "blue Color value in range 0.0-1.0")String b,
						 @Parameters(paramLabel = "PDF", description = "Path where to save the file")String outFile) throws Exception {

		App app = new App();
		try (PDDocument doc = Loader.loadPDF(pdf)) {

			app.setTextColor(doc, r, g, b, Paths.get(outFile));
		}catch(Exception e) {

			e.printStackTrace();
		}
	}
	@Command(name = "search", description  = "searches for a term in a Document and prints all the insatnces of sentences it appears in")
	public void textSearchWordByLine(@Parameters(paramLabel = "PDF")File pdfile,
									 @Parameters(paramLabel = "SearchTerm")String term) throws Exception {

		App app = new App();

		try (PDDocument document = Loader.loadPDF(pdfile)) {

			app.searchWordByLine(document, term);
		}catch(Exception e) {

			e.printStackTrace();
		}
	}
}
																			   
