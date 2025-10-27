package com.jddf.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

@Command(name = "doc", description = "Used for high-level operations on the document itself note teh contents For ex: merge,save")
public class DocumentCommands implements Runnable {

	public void run() {}

	@Command(name = "merge", description = "merge two or more documents")
	public void mergeDoc(@Parameters(paramLabel = "Multiple Pdf documents")File... pdfiles) throws Exception {

		try {

			App app = new App();

			PDDocument[] documents = new PDDocument[pdfiles.length];

			for (int i = 0; i < pdfiles.length; i++) {

				documents[i] = Loader.loadPDF(pdfiles[i]);
			}

			app.mergePDFS(documents);
		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Command(name = "save", description = "save the pdf document")
	public void savedoc(@Parameters(paramLabel = "Pdf document") File sourceFile, File destinationFile) {

		try {

			App app = new App();

			PDDocument document = Loader.loadPDF(sourceFile);

			app.saveDocument(document, destinationFile);
		}catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "list-fonts", description = "lists all the fonts in the video")
	public void docListFonts(@Parameters(paramLabel = "Document You want to list fonts of") File pdfile) throws Exception {

		try {

			App app = new App();

			PDDocument document = Loader.loadPDF(pdfile);

			app.listFonts(document);
		}catch (Exception e) {

			e.printStackTrace();
		}

	}
}
