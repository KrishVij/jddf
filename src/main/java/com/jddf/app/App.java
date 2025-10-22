 package com.jddf.app; 

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
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
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.util.Matrix;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public void storeImageAsFile(BufferedImage img) throws IOException {

		File file = new File("C:/Users/Krish Vij/OneDrive/Documents/" + System.nanoTime() + ".png");

		ImageIO.write(img, "PNG", file);
	}

	public void storeImagesAsFiles(ArrayList<BufferedImage> images) throws IOException {

		File directory = new File ("C:/Users/Krish Vij/testImages");

		if (!directory.exists()) {

			directory.mkdir();
		}

		for (BufferedImage image : images) {

			File file = new File(directory, System.nanoTime() + ".png");

			try {

				file.createNewFile();

				ImageIO.write(image, "PNG", file);
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}
	}

	public void convertAllToPNG(PDDocument document) throws IOException{

		PDPageTree pages = document.getPages();

		for (PDPage page : pages) {

			PDResources pdResources = page.getResources();

			for (COSName c : pdResources.getXObjectNames()) {

				PDXObject o =  pdResources.getXObject(c);

				if (o instanceof PDImageXObject) {

					PDImageXObject imageXObject = (PDImageXObject)o;

					BufferedImage img = imageXObject.getImage();

				    File outFile = new File("C:/Users/Krish Vij/OneDrive/Pictures/" + System.nanoTime() + ".png");

					ImageIO.write(img, "PNG", outFile);

					PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);

					processPage(page);

					try(PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {

						contentStream.drawImage(pdImage, coordinates.get(0).floatValue(), coordinates.get(1).floatValue(), coordinates.get(2).floatValue(), coordinates.get(3).floatValue());
					}
				}
			}
		}

		String outputFile = new File("output_" + System.nanoTime() + ".pdf").getAbsolutePath();
		document.save(outputFile);
	}

	ArrayList<Double> coordinates = new ArrayList<Double>();
	
	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {

		String operation = operator.getName();

		//ArrayList<Double> coordinates = new ArrayList<>();

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

		//PDFStreamEngine engine = new PDFStreamEngine();

		//App app = new App();

		for (PDPage page : pages) {

			processPage(page);

			PDColor color = getGraphicsState().getNonStrokingColor();

			nameOfColor = color.toString();
		}

		return nameOfColor;
	}

	public void setTextColor(PDDocument document, File file, String r, String g, String b) throws IOException {

		float	RED	  = Float.parseFloat(r);
		float	GREEN = Float.parseFloat(g);
		float	BLUE  = Float.parseFloat(b);

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

		document.save(new File("C:/Users/Krish Vij/OneDrive/Documents/output-redume.pdf"));
		System.out.println(streamCounter);
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
	
	public void saveDocument(PDDocument document, File file) throws IOException {

		document.save(file);
	}

	public static void main(String[] args) {

		App app = new App();

		//BufferedImage image = null;
		
		//ArrayList<BufferedImage> imageArray = new ArrayList<>();

		File file = new File("C:/Users/Krish Vij/OneDrive/Documents/file-sample_150kB.pdf");

		try (PDDocument document = Loader.loadPDF(file)) {

			//image = app.extractImageAt(document, 1);

			//app.storeImageAsFile(image);

			//imageArray = app.extractAllImages(document);

			//app.storeImagesAsFiles(imageArray);

			// String text = app.extractText(document);

			// System.out.println(text);

			// app.extractTextColor(document);

			// System.out.println("Text in pdf: " + text);

			//app.setTextColor(document, file, "1.0", "0.0", "0.0");
			app.searchWordByLine(document, "lorem");

			//app.setTextColor(document, file, "1.0", "0.0", "0.0");

			//app.saveDocument(document, file);

			//app.convertAllToPNG(document);

			//System.out.println(result);

			//document.save("C:/Users/Krish Vij/OneDrive/Documents/output.pdf");
				
		}catch (IOException io) {

			System.out.println("Exception Ocuured: " + io.getMessage());
		}

	}
}
