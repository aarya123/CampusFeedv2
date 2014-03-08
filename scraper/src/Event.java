import java.net.URLEncoder;

public class Event {

	private String url;
	private String title;
	private String author;
	private String organization;
	private String category;
	private String location;
	private String time_start;
	private String time_end;
	private String description;
	
	public Event(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		return location;
	}

	public void setStartTime(String startTime) {
		this.time_start = startTime;
	}
	
	public String getStartTime() {
		return time_start;
	}
	
	public void setEndTime(String endTime) {
		this.time_end = endTime;
	}
	
	public String getEndTime() {
		return time_end;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public String getOrganization() {
		return organization;
	}
	
	public String getSendFormat() {
		String k = "{\"title\":\"" + title +
					"\",\"desc\":\"" + description +
					"\",\"location\":\"" + location +
					"\",\"visibility\":1,"
					+ "\"date_time\":\"" + time_start +
					"\",\"url\":\"" + URLEncoder.encode(url) + "\"}";
		return k;
	}
	
	public String toString() {
		return url + "\n"
				+ title + "\n"
				+ author + "\n"
				// TODO
				// + organization + "\n"
				+ category + "\n"
				+ location + "\n"
				+ time_start + "\n"
				+ time_end + "\n"
				+ description;
	}
}
