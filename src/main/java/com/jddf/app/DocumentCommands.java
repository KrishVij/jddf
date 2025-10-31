package com.jddf.app;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

@Command(name = "doc", description = "Used for high-level operations on the document itself note For ex: merge,save")
public class DocumentCommands implements Runnable {

	public void run() {}

	@Command(name = "merge", description = "merge two or more documents")
	public void mergeDoc(@Parameters(paramLabel = "Multiple Pdf documents")File... pdfiles) throws Exception {

		if (pdfiles == null) {

			throw new IllegalArgumentException("At least two PDF documents must be provided for merging.");
		}

		try {

			App app = new App();

			PDDocument[] documents = new PDDocument[pdfiles.length];

			for (int i = 0; i < pdfiles.length; i++) {

				if (!pdfiles[i].isFile()) {

					throw new IllegalArgumentException("Provided file " + pdfiles[i].getName() + " is not a valid PDF document.");
				}else if (!pdfiles[i].getName().toLowerCase().endsWith(".pdf")) {

					throw new IllegalArgumentException("Provided file " + pdfiles[i].getName() + " is not a PDF document.");
				}

				documents[i] = Loader.loadPDF(pdfiles[i]);
			}

			app.mergePDFS(documents);
		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Command(name = "save", description = "save the pdf document")
	public void savedoc(@Parameters(paramLabel = "Pdf document") File sourceFile, File destinationFile) {

		if (sourceFile == null || !sourceFile.getName().toLowerCase().endsWith(".pdf")) {

			throw new IllegalArgumentException("Provided source file is not a PDF document.");
		}else if (destinationFile == null || !destinationFile.getName().toLowerCase().endsWith(".pdf")) {

			throw new IllegalArgumentException("Provided destination file is not a PDF document.");
		}
		App app = new App();

		try (PDDocument document = Loader.loadPDF(sourceFile)) {

			app.saveDocument(document, destinationFile);
		}catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "list-fonts", description = "lists all the fonts in the video")
	public void docListFonts(@Parameters(paramLabel = "Document You want to list fonts of") File pdfile) throws Exception {

		if (pdfile == null || !pdfile.getName().toLowerCase().endsWith(".pdf")) {

			throw new IllegalArgumentException("Provided file is not a PDF document.");
		}
		
		App app = new App();
		try (PDDocument document = Loader.loadPDF(pdfile)) {

			app.listFonts(document);
		}catch (Exception e) {

			e.printStackTrace();
		}

	}
}
