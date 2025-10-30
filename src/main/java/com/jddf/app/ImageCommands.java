package com.jddf.app;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.nio.file.Paths;

@Command(name = "image", description = "Deals with images in pdfs")
public class ImageCommands implements Runnable {

	public void run() {}

	@Command(name = "extract-all", description = "Extracts all images from a pdf file")
	public void imageStoreImageAsFiles(@Parameters(paramLabel = "Document to extract images from")File pdfile, String outDir) throws Exception {

		App app = new App();
		try (PDDocument document  = Loader.loadPDF(pdfile)){

			app.extractAndStoreImagesAsFiles(document, Paths.get(outDir));
		}catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "extract", description = "Extracts the first image from the document")
	public void imageStoreAsFile(@Parameters(paramLabel = "Document to extract image from") File pdfile, String outDir) throws Exception {

		App app = new App();
		try (PDDocument document  = Loader.loadPDF(pdfile)){

			app.extractAndStoreImageAsFile(document, Paths.get(outDir));
		}catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "convert-all", description = "converts all images inside the docuemnt to png")
	public void imageConvertToPNG(@Parameters(paramLabel = "Document with images to convert")File pdfile, String outFile) throws Exception {

		App app = new App();

		try (PDDocument document  = Loader.loadPDF(pdfile)) {

			app.convertAllToPNG(document, Paths.get(outFile));
		}catch(Exception e) {

			e.printStackTrace();
		}
	}

	@Command(name = "dir-to-pdf", description = "converts a given directory of images to pdf")
	public void imageDirToPdf(@Parameters(paramLabel = "Directory of images to be converted to pdf")String imageDirPath, String outDir) throws Exception {

		App app = new App();

		app.imagesToPDF(Paths.get(imageDirPath), Paths.get(outDir));
	}

	@Command(name = "to-pdf", description = "Take any number of images and convert each to pdf")
	public void imageMultiToPdf(@Parameters(paramLabel = "Output Directory",description = "Output Directory to save the PDF'S (default: current dir)")String outDir, 
	                            @Parameters(paramLabel = "image files", description = "Paths to your selected images")String... imagePath) throws Exception {

		if (imagePath.length == 0) {

			throw new IllegalArgumentException("No images provided.");
		}

		App app = new App();
		
		app.imagesToSeparatePDFS(Paths.get(outDir), imagePath);
	}

	@Command(name = "merge-images", description = "Takes multiple images and merges them into a single PDF")
    public void mergeImagesToPDF(@Parameters(paramLabel = "images", description = "Image files to convert and merge into one PDF") String outFile, String... imagePaths) throws Exception {

		App app = new App();
        app.convertImagesToPDFSAndMergeThem(Paths.get(outFile), imagePaths);
    }
}
