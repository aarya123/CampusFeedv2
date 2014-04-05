import java.net.URLEncoder;

public class Event {

	private String url;
	private String title;
	private String author;
	private String organization;
	private String[] categories;
	private String location;
	private String time_start;
	private String time_end;
	private String description;
	private int error;
	
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
	
	public void setCategories(String categories[]) {
		this.categories = categories;
	}
	
	public String[] getCategories() {
		return categories;
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

	public int getError() {
		return error;
	}
	
	public void setError() {
		error = 1;
	}

	/*
	 * Returns a json strong parsable by the database
	 */
	public String getSendFormat() {
		// Format categories array to json string object
		String cat = "[";
		for (int i = 0; i < categories.length; i++) {
			cat += categories[i];
			if (i != categories.length - 1) { cat += ", "; }
		}
		cat += "]";
		
		String k = "{\"title\":\"" + title +
					"\",\"categories\":\"" + cat +
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
				+ categories + "\n"
				+ location + "\n"
				+ time_start + "\n"
				+ time_end + "\n"
				+ description;
	}
}
