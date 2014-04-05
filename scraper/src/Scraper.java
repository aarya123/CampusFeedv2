import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@SuppressWarnings("deprecation")

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
			Event e = parseBoilerEvent(event);
			// sendData(e);
		}
	}
	
	public static void sendData(Event e) {
		HttpClient httpClient = new DefaultHttpClient();
	    try {
	        HttpPost request = new HttpPost("http://54.213.17.69:9000/scraper_handle");
	        StringEntity params = new StringEntity(e.getSendFormat());
	        request.addHeader("content-type", "application/json");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        // handle response here...
	    }catch (Exception ex) {
	        // handle exception here
	    	ex.printStackTrace();
	    	System.exit(2);
	    } finally {
	        httpClient.getConnectionManager().shutdown();
			System.out.println("SENT!");
	    }
	}
	
	public static Event parseBoilerEvent(Element event) {
		// Create new event from parsed url
		String page_url = event.text().split("<link />")[0].split(" ")[0];
		Event k = new Event(page_url);
		// Get author
		String auth = event.select("author").text();
		k.setAuthor(auth);
		// TODO Implement ORGS
		//k.setOrganization(org);
		// Get category
		String[] categories = event.select("category").text().split("/");
		if (categories[0].length() == 0) {
			String[] tmp = new String[1];
			tmp[0] = "error";
			k.setCategories(tmp);
		} else {
			k.setCategories(categories);
		}
		k.setTitle(event.select("title").text());
		// Prepare body parser
		String description = event.select("description").text();
		String[] times = description.split("<span class=\"");
		// Get start time
		String startTime = times[1];
		if (startTime.equals("dtstart\">")) {
			k.setStartTime("error");
		} else {
			startTime = startTime.split("title=\"")[1].split("\">")[0];
			if (!startTime.contains("T")) {
				startTime = startTime + "T00:00:00";
			}
			k.setStartTime(startTime);
		}
		// Get end time
		k.setEndTime(times[2].split("</span>")[0].split("title=\"")[1].split("\">")[0]);
		// Add T constraint to the end of times
		if (!k.getEndTime().contains("T") && !k.getEndTime().contains("error")) {
			k.setEndTime(k.getEndTime() + "T00:00:00");
		}
		if (k.getStartTime() == "error" && k.getEndTime() != "error") {
			k.setStartTime(k.getEndTime());
		}
		
		// Remove "T" from times
		if (!k.getStartTime().contains("error")) {
			k.setStartTime(k.getStartTime().replace("T", " "));
		}
		if (!k.getEndTime().contains("error")) {
			k.setEndTime(k.getEndTime().replace("T", " "));
		}
		// MONTH DAY YEAR
		if (!k.getStartTime().contains("error")) {
			String[] sta = k.getStartTime().split("-");
			String time = sta[2].split(" ")[1];
			sta[2] = sta[2].split(" ")[0];
			k.setStartTime(sta[1]+"-"+sta[2]+"-"+sta[0]+" "+time);
		}
		if (!k.getEndTime().contains("error")) {
			String[] en = k.getEndTime().split("-");
			String time = en[2].split(" ")[1];
			en[2] = en[2].split(" ")[0];
			k.setEndTime(en[1]+"-"+en[2]+"-"+en[0]+" "+time);
		}
		// Parse Location
		k.setLocation(description.split("<span class=\"location\">")[1].split("</span>")[0]);
		// Parse Description
		description = description.split("<div class=\"description\">")[1];
		if (description.equals("</div>")) {
			k.setDescription("error");
		} else {
			description = description.split("</div>")[0];
			description = description.replace("&nbsp;", "");
			description = description.replace("&amp;", "");
			// Strip stray tags from description
			String alt[] = description.split("(<)(.+?)(>)");
			for (int i=1; i<alt.length; i++) {
				alt[0] = alt[0] + alt[i];
			}
			description = alt[0];
			k.setDescription(description);
		}
		//TODO DEBUG TESTING
		//System.out.println(k.toString() + "\n");
		System.out.println(k.getSendFormat());
		return k;
	}
	
	public static void main(String[] args) throws InterruptedException {
		boolean run = true;
		int delay = 10; // Seconds
		while (run) {
			runScanner("BoilerLink");
			// Thread.sleep(delay*1000);
			// TODO DEBUG
			run = false;
		}
	}
}
