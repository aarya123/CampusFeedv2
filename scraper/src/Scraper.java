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
			sendData(e);
		}
	}
	
	public static void sendData(Event e) {
		HttpClient httpClient = new DefaultHttpClient();

	    try {
	        HttpPost request = new HttpPost("http://yoururl");
	        StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        // handle response here...
	    }catch (Exception ex) {
	        // handle exception here
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
	}
	
	public static Event parseBoilerEvent(Element event) {
		String page_url = event.text().split("<link />")[0].split(" ")[0];
		Event k = new Event(page_url);
		
		k.setAuthor(event.select("author").text());
		k.setCategory(event.select("category").text());
		if (k.getCategory().length() == 0) { k.setCategory("error"); }
		k.setTitle(event.select("title").text());
		
		String description = event.select("description").text();
		String[] times = description.split("<span class=\"");
		
		String startTime = times[1];
		if (startTime.equals("dtstart\">")) {
			k.setStartTime("error");
		} else {
			startTime = startTime.split("title=\"")[1].split("\">")[0];
			k.setStartTime(startTime);
		}
		k.setEndTime(times[2].split("</span>")[0].split("title=\"")[1].split("\">")[0]);
		k.setLocation(description.split("<span class=\"location\">")[1].split("</span>")[0]);
		
		description = description.split("<div class=\"description\">")[1];
		if (description.equals("</div>")) {
			k.setDescription("error");
		/*} else if (description.contains("<") || description.contains(">")){
			k.setDescription("error");
			System.out.println("PARSING ERROR - " + description);
		*/
		} else {
			description = description.split("</div>")[0];
			description = description.replace("<p>", "");
			description = description.replace("</p>", "");
			description = description.replace("&nbsp;", "");
			String alt[] = description.split("(<)(.+?)(>)");
			for (int i=1; i<alt.length; i++) {
				alt[0] = alt[0] + alt[i];
			}
			description = alt[0];
			k.setDescription(description);
		}
		//TODO DEBUG TESTING
		System.out.println(k.toString() + "\n");
		return k;
	}
	
	public static void main(String[] args) {
		runScanner("BoilerLink");
	}
}
