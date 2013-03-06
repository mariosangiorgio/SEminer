package it.polimi.webClient;

public class DownloadException extends Exception {

	private static final long serialVersionUID = 7531442970679656093L;
	private String resource;
	private int statusCode;

	public DownloadException(String resource, int statusCode) {
		this.resource = resource;
		this.statusCode = statusCode;
	}

	public DownloadException(String resource) {
		this.resource = resource;
		statusCode = -1;
		
	}
	
	@Override
	public String getMessage() {
		String message;
		if (statusCode != -1) {
			message = "Download Exception. Status code: " + statusCode;
		} else {
			message = "Download Exception. Response OK returned a null entity";
		}
		return message + " while downloading " + resource;
	}
}
