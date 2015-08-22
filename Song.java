
public class Song {

	String artist;
	String title;
	String album;
	String path;
	
	
	public Song(String title, String artist, String album, String path) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.path = path;
	}

	public Song(Song songXML) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.path = path;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public int compareTo(Object song) {

		String title1 = this.title;
		String title2 = ((Song)song).getTitle();

		return title1.compareTo(title2);
	}


	public String toString() {
		return "Song [title=" + title + ", artist=" + artist + ", album="
				+ album + ", path=" + path + "]";
	}


	
}
