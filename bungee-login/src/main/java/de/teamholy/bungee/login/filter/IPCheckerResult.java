package de.teamholy.bungee.login.filter;

public class IPCheckerResult {

	public String getIP() {
		return IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public String getCompany() {
		return Company;
	}

	public void setCompany(String company) {
		this.Company = company;
	}

	public Integer getASN() {
		return ASN;
	}

	public void setASN(Integer ASN) {
		this.ASN = ASN;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		this.City = city;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		this.Country = country;
	}

	public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String countryCode) {
		this.CountryCode = countryCode;
	}

	public boolean isHosting() {
		return Hosting;
	}

	public void setHosting(boolean hosting) {
		this.Hosting = hosting;
	}

	public boolean isProxy() {
		return Proxy;
	}

	public void setProxy(boolean proxy) {
		this.Proxy = proxy;
	}

	public boolean isVPN() {
		return VPN;
	}

	public void setVPN(boolean VPN) {
		this.VPN = VPN;
	}

	public boolean isTOR() {
		return TOR;
	}

	public void setTOR(boolean TOR) {
		this.TOR = TOR;
	}

	public boolean isResidental() {
		return Residental;
	}

	public void setResidental(boolean residental) {
		this.Residental = residental;
	}

	private String IP;

	private String Company;

	private Integer ASN;

	private String City;

	private String Country;

	private String CountryCode;

	private boolean Hosting;
	
	private boolean Proxy;

	private boolean VPN;

	private boolean TOR;

	private boolean Residental;
}
