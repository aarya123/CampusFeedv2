import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class scraper {
		
	private static void runScanner(String website) {
		String webURL = "the internet.";
		try {
			if (website.equals("BoilerLink")) {
				webURL = "https://boilerlink.purdue.edu/Events";
				scanBoilerLink(webURL);
			}
		} catch (IOException e) {
			System.out.println("ERROR: Unable to connect to "+website+" at "+webURL);
			e.printStackTrace();
		}
	}
	
	private static void scanBoilerLink(String url) throws IOException {
		Connection com = Jsoup.connect(url);
		Document scan_homepage = com.get();
		Elements links = scan_homepage.select("a[href]");
		for (Element link : links) {
			if (link.hasClass("modal") && link.hasAttr("target")) {
				String page_url = link.absUrl("href");
				//TODO DEBUG
				System.out.println("Grabbing data from: " + page_url);
				getBoilerPageData(page_url);
			}
		}
	}
	
	private static void getBoilerPageData(String url) throws IOException {
		Connection com_page = Jsoup.connect(url);
		Document scan_page = com_page.get();
		Elements links = scan_page.select("h2");
		System.out.println(links.get(0).select("span").text());
	}
	
	public static void main(String[] args) {
		runScanner("BoilerLink");
	}
}
