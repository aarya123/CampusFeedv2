import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Scraper {
		
	private static void runScanner(String website) {
		String webURL = "the internet.";
		try {
			if (website.equals("BoilerLink")) {
				webURL = "https://boilerlink.purdue.edu/EventRss/EventsRss";
				scanBoilerLink(webURL);
			}
		} catch (IOException e) {
			System.out.println("ERROR: Unable to connect to "+website+" at "+webURL+
					"\nTry again in a few seconds.");
			e.printStackTrace();
		}
	}
	
	private static void scanBoilerLink(String url) throws IOException {
		Connection com = Jsoup.connect(url);
		Document scan_homepage = com.ignoreContentType(true).get();
		Elements events = scan_homepage.select("item");
		for (Element event : events) {
			parseBoilerEvent(event);
			//TODO DEBUG TESTING
			//System.out.println("-");
		}
	}
	
	public static void parseBoilerEvent(Element event) {
		String page_url = event.text().split("<link />")[0].split(" ")[0];
		Event k = new Event(page_url);
		k.setAuthor(event.select("author").text());
		k.setCategory(event.select("category").text());
		k.setTitle(event.select("title").text());
		
		String description = event.select("description").text();
		String[] times = description.split("<span class=\"");
		
		String startTime = times[1];
		if (startTime.equals("dtstart\">")) {
			k.setStartTime("");
		} else {
			startTime = startTime.split("title=\"")[1].split("\">")[0];
			k.setStartTime(startTime);
		}
		k.setEndTime(times[2].split("</span>")[0].split("title=\"")[1].split("\">")[0]);
		k.setLocation(description.split("<span class=\"location\">")[1].split("</span>")[0]);
		
		description = description.split("<div class=\"description\">")[1];
		if (description.equals("</div>")) {
			k.setDescription("");
		} else {
			description = description.split("</div>")[0];
			description = description.replace("<p>", "");
			description = description.replace("</p>", "");
			description = description.replace("&nbsp;", "");
			k.setDescription(description);
		}
		//TODO DEBUG TESTING
		//System.out.println(k.toString());
	}
	
	public static void main(String[] args) {
		runScanner("BoilerLink");
	}
}
