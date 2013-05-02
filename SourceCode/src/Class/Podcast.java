package Class;

public class Podcast {

	String Baslik;
	String YuklenmeTarih;
	String URL;
	String AudioURL;
	double DosyaBoyut;
	

	public double getDosyaBoyut() {
		return DosyaBoyut;
	}
	public void setDosyaBoyut(double dosyaBoyut) {
		DosyaBoyut = dosyaBoyut;
	}
	public String getAudioURL() {
		return AudioURL;
	}
	public void setAudioURL(String audioURL) {
		AudioURL = audioURL;
	}
	public String getBaslik() {
		return Baslik;
	}
	public void setBaslik(String baslik) {
		Baslik = baslik;
	}
	public String getYuklenmeTarih() {
		return YuklenmeTarih;
	}
	public void setYuklenmeTarih(String yuklenmeTarih) {
		YuklenmeTarih = yuklenmeTarih;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	
}
