package it.polimi.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class TextStripper {
	private static final Pattern nonAlphabetic = Pattern.compile("[^a-zA-Z]");
	private static final Pattern dash = Pattern.compile("\\-\\s*");
	private static final Pattern multipleWhitespaces = Pattern.compile("\\s+");

	public TextStripper() {
	}

	public String getContent(File document) throws IOException, PDFEncryptedException {
		String content;
		content = getFullText(document);
		Matcher matcher;
		// Merging multiple whitespaces
		matcher = multipleWhitespaces.matcher(content);
		content = matcher.replaceAll(" ");
		// Removing dashes to rebuild hyphenated words
		matcher = dash.matcher(content);
		content = matcher.replaceAll("");

		return content;
	}

	public String cleanContent(String content) {
		Matcher matcher;
		// Dropping non alphabetic characters
		matcher = nonAlphabetic.matcher(content);
		content = matcher.replaceAll(" ");
		return content;
	}

	private String getFullText(PDDocument fullTextDocument) throws IOException, PDFEncryptedException {
		if (!fullTextDocument.isEncrypted()) {
			String fullText;

			StringWriter writer = new StringWriter();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSuppressDuplicateOverlappingText(false);
			stripper.setSpacingTolerance(0.5f);
			try {
				stripper.writeText(fullTextDocument, writer);
			} catch (RuntimeException e) {
				throw new IOException();
			}
			writer.close();

			fullText = writer.toString();

			return fullText;
		} else {
			throw new PDFEncryptedException();
		}
	}

	private String getFullText(File document) throws IOException, PDFEncryptedException {
		PDDocument fullTextDocument = PDDocument.load(document);
		String fullText;
		try {
			fullText = getFullText(fullTextDocument);
		} finally {
			if (fullTextDocument != null) {
				fullTextDocument.close();
			}
		}
		return fullText;
	}

	public String getFullText(InputStream document) throws IOException, PDFEncryptedException {
		PDDocument fullTextDocument = PDDocument.load(document);
		String fullText = getFullText(fullTextDocument);
		fullTextDocument.close();
		return fullText;
	}
}
