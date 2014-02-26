import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate; 

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sun.net.www.protocol.https.HttpsURLConnectionImpl;

public class scraper {	
	private static void runScanner(String website) {
		String webURL = "the internet.";
		try {
			if (website.equals("BoilerLink")) {
				webURL = "https://boilerlink.purdue.edu/Events";
				scanBoilerLink(webURL);
			}
		} catch (IOException e) {
			System.out.println("ERROR: Unable to connect to " + webURL);
			e.printStackTrace();
		}
	}
	private static void scanBoilerLink(String url) throws IOException {
		Connection com = Jsoup.connect(url)
			.userAgent("Mozilla")
			.timeout(3000);
		Document scan_homepage = com.get();
		Elements eventBlocks = scan_homepage.select("a[class='modal' href]");
	}
	public static void main(String[] args) {
		runScanner("BoilerLink");
	}
}
