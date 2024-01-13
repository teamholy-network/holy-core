package de.teamholy.bungee.login.api;

import lombok.Getter;

public class RestAPIResponse {

	@Getter
    private String text;

	private boolean failed;

	@Getter
    private String url;
	
	public RestAPIResponse(String text,boolean failed, String url) {
		this.url = url;
		this.text = text;
		this.failed = failed;
	}


    public boolean getFailed() {
		return failed;
	}


}
