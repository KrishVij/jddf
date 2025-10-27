package com.jddf.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

@Command(name = "image", description = "Deals with images in pdfs")
public class ImageCommands implements Runnable {

	public void run() {}

	@Command(name = "extrac-all", description = "Extracts all images from a pdf file")
	public void imageStoreImageAsFiles(@Parameters(paramLabel = "Docuemnt to extract images from")File pdfile) throws Exception {

		try {

			App app = new App();
			PDDocument document  = Loader.loadPDF(pdfile);

			app.extarctAndStoreImagesAsFiles(document);
		}catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "extarct", description = "Extracts the first image from the document")
	public void imageStoreAsFile(@Parameters(paramLabel = "Document to extarct image from") File pdfile) throws Exception {

		try {

			App app = new App();
			PDDocument document  = Loader.loadPDF(pdfile);

			app.extarctAndStoreImageAsFile(document);
		}catch (Exception e) {

			e.printStackTrace();
		}
	}
}
