package com.jddf.app; 

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;

public class App extends PDFStreamEngine {

	public BufferedImage extractImage(PDDocument document) throws IOException {

		BufferedImage img = null;

		PDPage page = document.getPage(0);

		PDResources pdResources = page.getResources();

		for (COSName c : pdResources.getXObjectNames()) {

			PDXObject o = pdResources.getXObject(c);

			if (o instanceof PDImageXObject) {

				PDImageXObject imageXObject = (PDImageXObject)o;

				img = imageXObject.getImage();
			}
		}

		System.out.format("%s\n", pdResources.getXObjectNames().toString());
		System.out.format("%s\r\n", page.getResources().toString());

		return img;
	
	}

	public BufferedImage extractImageAt(PDDocument document, int imageNumber) throws IOException {

		PDPageTree pageTree = document.getPages();

		ArrayList<BufferedImage> imgArray = new ArrayList<>();

		BufferedImage img = null;

		for (PDPage page : pageTree) {

			PDResources pdResources = page.getResources();

			for (COSName c : pdResources.getXObjectNames()) {

				PDXObject o = pdResources.getXObject(c);

				System.out.format("%s ", pdResources.getXObjectNames().toString());

				if (o instanceof PDImageXObject) {

					PDImageXObject imageXObject = (PDImageXObject)o;

					img = imageXObject.getImage();

					imgArray.add(img);
				}
			}
			
		}

		return imgArray.get(imageNumber - 1);
		
	}

	public ArrayList<BufferedImage> extractAllImages(PDDocument document) throws IOException {

		PDPageTree pageTree = document.getPages();

		ArrayList<BufferedImage> images = new ArrayList<>();

		BufferedImage img = null;

		for (PDPage page : pageTree) {

			PDResources pdResources = page.getResources();

			for (COSName c : pdResources.getXObjectNames()) {

				PDXObject o = pdResources.getXObject(c);

				System.out.format("%s ", pdResources.getXObjectNames().toString());

				if (o instanceof PDImageXObject) {

					PDImageXObject imageXObject = (PDImageXObject)o;

					img = imageXObject.getImage();

					images.add(img);
				}
			}
			
		}
		
		return images;
	}

	public void extractAndStoreImageAsFile(PDDocument document, Path outDir) throws IOException {

		BufferedImage img = extractImage(document);
		
		Files.createDirectories(outDir);
		Path out = outDir.resolve("image_" + System.nanoTime() + ".png").toAbsolutePath().normalize();

		ImageIO.write(img, "PNG", out.toFile());
	}

	public void extractAndStoreImagesAsFiles(PDDocument document, Path outDir) throws IOException {

		ArrayList<BufferedImage> images = extractAllImages(document);


	    Files.createDirectories(outDir);
		
		for (BufferedImage image : images) {

			File file = outDir.resolve("image_" + System.nanoTime() + ".png").toFile();

			try {

				file.createNewFile();

				ImageIO.write(image, "PNG", file);
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}
	}

	public void convertAllToPNG(PDDocument document, Path outDir) throws IOException{

		PDPageTree pages = document.getPages();

		for (PDPage page : pages) {

			PDResources pdResources = page.getResources();

			for (COSName c : pdResources.getXObjectNames()) {

				PDXObject o =  pdResources.getXObject(c);

				if (o instanceof PDImageXObject) {

					PDImageXObject imageXObject = (PDImageXObject)o;

					BufferedImage img = imageXObject.getImage();

					
					File file = outDir.resolve("image_" + System.nanoTime() + ".png").toFile();;

					ImageIO.write(img, "PNG", file);

					PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);

					processPage(page);

					try(PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {

						contentStream.drawImage(pdImage, coordinates.get(0).floatValue(), coordinates.get(1).floatValue(), coordinates.get(2).floatValue(), coordinates.get(3).floatValue());
					}
				}
			}
		}

		document.save(outDir.resolve("converted_" + System.nanoTime() + ".pdf").toFile());
	}

	ArrayList<Double> coordinates = new ArrayList<Double>();
	
	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {

		String operation = operator.getName();

		if ("Do".equals(operation)) {

			COSName objectName = (COSName) operands.get( 0 );
			PDXObject xobject = getResources().getXObject( objectName );

			if (xobject instanceof PDImageXObject) {

				PDImageXObject imageXObject = (PDImageXObject) xobject;

				Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();

				coordinates = getCoordinates(ctmNew);
			}
		}

		return;
	}
	
	public ArrayList<Double> getCoordinates(Matrix ctmNew) {
		
		coordinates.add((double)ctmNew.getTranslateX());

		coordinates.add((double)ctmNew.getTranslateY());
		
		coordinates.add((double)ctmNew.getScaleX());
		
		coordinates.add((double)ctmNew.getScaleY());
		
		return coordinates;
	}
	
	public String extractText(PDDocument document) throws IOException {

			PDFTextStripper stripper = new PDFTextStripper();

			return stripper.getText(document);
		
		}

		public String extractTextColor(PDDocument document) throws IOException {

			PDPageTree pages = document.getPages();

		    String nameOfColor = "";
			
			for (PDPage page : pages) {

			processPage(page);

			PDColor color = getGraphicsState().getNonStrokingColor();

			nameOfColor = color.toString();
		
		}

		return nameOfColor;
	}

	public void setTextColor(PDDocument document, String r, String g, String b, Path outFile) throws IOException {

		float	RED	  = Float.parseFloat(r)/ 255;
		float	GREEN = Float.parseFloat(g)/ 255;
		float	BLUE  = Float.parseFloat(b)/ 255;

		PDPageTree pages = document.getPages();
		int streamCounter = 0;

		for (PDPage page : pages) {

			Iterator<PDStream> streams = page.getContentStreams();
			
			PDFStreamParser parser = new PDFStreamParser(page);
			
			List<PDStream> newStreams =  new ArrayList<>();
			while (streams.hasNext()) {

				List<Object> tokens = parser.parse();

				List<Object> newTokens =  new ArrayList<>();

				for (Object token : tokens) {

					if (token instanceof Operator) {

						Operator op = (Operator)token;
						String name = op.getName();
						
						if ("BT".equals(name)) {

							newTokens.add(new COSFloat(RED));
							newTokens.add(new COSFloat(GREEN));
							newTokens.add(new COSFloat(BLUE));
							newTokens.add(Operator.getOperator("rg"));
							newTokens.add(new COSFloat(RED));
							newTokens.add(new COSFloat(GREEN));
							newTokens.add(new COSFloat(BLUE));
							newTokens.add(Operator.getOperator("RG"));
						}
					}

					newTokens.add(token);
				}

				streamCounter += 1;

				PDStream newStream = new PDStream(document);

				try (OutputStream out = newStream.createOutputStream()) {

					ContentStreamWriter writer = new ContentStreamWriter(out);
					writer.writeTokens(newTokens);
				}

				newStreams.add(newStream);
				streams.next();

			}
			
			page.setContents(newStreams);
		}

		// Path path = Paths.get(outFile);
		//document.save(new File("C:/Users/Krish Vij/OneDrive/Documents/output-resume.pdf"));
		document.save(outFile.toFile());
		System.out.println(streamCounter);
	}

	public void listFonts(PDDocument document) throws IOException {

		PDPageTree pages = document.getPages();

		for (PDPage page : pages) {

			PDResources resources = page.getResources();

			Iterable<COSName> fontnames =  resources.getFontNames();
			for (COSName fontname : fontnames) {

				PDFont font = resources.getFont(fontname);
				System.out.println("Font: " + font.getFontDescriptor().getFontName());
			}
		}
	}
	
	public void searchWordByLine(PDDocument document, String searchTerm) throws IOException {

		String text = extractText(document);

		String[] sentences = text.split("\n");

		boolean isFound = false;
		for (String sentence : sentences) {

			String[] words = sentence.split("\\s+");

			for (String word : words) {

				if (word.equals(searchTerm)) {

					isFound = true;
					System.out.println(sentence);
				}
			}
		}

		if (!isFound) {

			System.out.println("[Error]Word does not exist");
		}
	}

	public void darkMode(PDDocument document, Path outFile) throws IOException {

		PDPageTree pages = document.getPages();

		for (PDPage page : pages) {

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.PREPEND, true, true)) {

				contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f);

				contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());

				contentStream.fill();
			}

			setTextColor(document, "1.0", "1.0", "1.0", outFile);
		}
	}

	public void mergePDFS(PDDocument... documents) throws IOException {

		if (documents.length == 0) {

			throw new IllegalArgumentException("No documents provided to merge.");
		}

		PDFMergerUtility merger = new PDFMergerUtility();
		PDDocument destination = documents[0];

		for (int i = 1; i < documents.length; i++) {

			merger.appendDocument(destination, documents[i]);
		}

		destination.save(new File("C:/Users/Krish Vij/OneDrive/Documents/merged-resume.pdf"));
		destination.close();

		for (int i = 1; i < documents.length; i++) {
			documents[i].close();
		}
	}

	public void mergePDFS(File... pdFiles) throws IOException {

		if (pdFiles.length == 0) {

			throw new IllegalArgumentException("No documents provided to merge.");
		}

		PDFMergerUtility merge = new PDFMergerUtility();
		File dest = pdFiles[0];

		try (PDDocument empty = new PDDocument()) {

			PDPage page = new PDPage();
			empty.addPage(page);
			empty.save(dest);
		}
		
		PDDocument destination = Loader.loadPDF(pdFiles[0]);
		for (int i = 1; i < pdFiles.length; i++) {

			PDDocument current = Loader.loadPDF(pdFiles[i]);
			merge.appendDocument(destination, current);
		}

		destination.save(pdFiles[0]);
		destination.close();
	}

	public void imagesToPDF(Path imagesDirectoryPath, Path outFile) throws IOException {

		File imageDir = imagesDirectoryPath.toFile();

		File file = outFile.toFile();

		try (PDDocument doc = new PDDocument()) {

			for (File image : imageDir.listFiles()) {

				PDPage page = new PDPage();
				doc.addPage(page);

				PDImageXObject pdImage = PDImageXObject.createFromFile(image.getAbsolutePath(), doc);

				try (PDPageContentStream contents = new PDPageContentStream(doc, page))
					{
						
						contents.drawImage(pdImage, 20, 20);
					}
				//doc.save(new File("C:/Users/Krish Vij/OneDrive/Documents/image-resume.pdf"));
				
			}

			doc.save(file);
		}
	}

	public void imagesToSeparatePDFS(Path outDir, String... imagepath) throws IOException {
		
		Files.createDirectories(outDir);

		for (int i = 0; i < imagepath.length; i++) {

			File imgFile = new File(imagepath[i]);

			System.out.println("Processing: " + imgFile.getAbsolutePath());

			String name = imgFile.getName().toLowerCase();
				
			if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif"))) {
					
				System.out.println("Skipping unsupported file format: " + name);
				continue;
			}

			try (PDDocument doc = new PDDocument()) {

				PDPage page = new PDPage();
				doc.addPage(page);

				PDImageXObject pdImage = PDImageXObject.createFromFile(imagepath[i], doc);

				try (PDPageContentStream contents = new PDPageContentStream(doc, page))
					{
						
						contents.drawImage(pdImage, 20, 20);
					}
				
				Path outFile = outDir.resolve("image_converted_" + System.nanoTime() + ".pdf").toAbsolutePath().normalize();
				doc.save(outFile.toFile());

				System.out.println("Saved: " + outFile.toString());
			}
		}
	}

    public void convertImagesToPDFSAndMergeThem(Path outputPath, Path... imagePaths) throws IOException {
		
		var pdFiles = new ArrayList<File>();
		
		for (Path imagePath : imagePaths) {

			File imgFile = imagePath.toFile();

			String name = imgFile.getName().toLowerCase();

			if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif"))) {
					
				System.out.println("Skipping unsupported file format: " + name);
				continue;
			}

			try (PDDocument doc =  new PDDocument()) {
			    PDPage page = new PDPage();
				doc.addPage(page);

				PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath.toString(), doc);

				try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
					contents.drawImage(pdImage, 20, 20);
				}

				File tempFile = File.createTempFile("tempFile", ".pdf");
				tempFile.deleteOnExit(); 
				doc.save(tempFile);
				pdFiles.add(tempFile);

			}
		}

		pdFiles.add(0, outputPath.toFile());
		File[] pdFilesArray = pdFiles.toArray(new File[0]);
		mergePDFS(pdFilesArray);
	}

	public void renderView(PDDocument document) throws IOException {

		int numberOfpages = document.getNumberOfPages();

		PDFRenderer renderer = new PDFRenderer(document);

		for (int i = 0; i < numberOfpages; i++) {

			BufferedImage img = renderer.renderImage(i);

			File f = File.createTempFile("img", "png");

			ImageIO.write(img, "PNG", f);

			String path = f.getAbsolutePath();

			try {

				ProcessBuilder proc = new ProcessBuilder("wezterm", "imgcat", path);
				Process process = proc.start();

				int exitCode = process.waitFor();
				System.out.println("Process exited with code: " + exitCode);
			}catch (IOException | InterruptedException e) {

				e.printStackTrace();
			}
		}
	}
	
	public void saveDocument(PDDocument document, File file) throws IOException {

		document.save(file);
	}
}
