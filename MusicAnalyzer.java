import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MusicAnalyzer {

	public  ArrayList<Song> songs;

	MusicAnalyzer(){
		songs = new ArrayList<Song>(); 
	}

	// -----------------------------------------------------------------------------------------------
	//	The traverse method goes through a directy, folders, etc. until it gets to songs and gets
	//	it's tags for the title, artist, and album and the songs path and creates a new song object
	//	with its tags and is stored in an arraylist(songs).
	// -----------------------------------------------------------------------------------------------

	public void traverse(File dir, int level) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		if (dir.isDirectory()) {
			File [] contents = dir.listFiles();
			for(int i = 0; i < contents.length; i++) {
				traverse(contents[i], level+1);
			}
		}
		else {
			String fileName = dir.getAbsolutePath();
			if(fileName.endsWith("mp3")){
				AudioFile f = AudioFileIO.read(new File(fileName));
				Tag tag = f.getTag();
				String title = tag.getFirst(FieldKey.TITLE);
				String artist = tag.getFirst(FieldKey.ARTIST);
				String album = tag.getFirst(FieldKey.ALBUM);
				String path = dir.getAbsolutePath();
				Song newSong = new Song(title, artist, album, path);
				songs.add(newSong);
			}

		}
	}


	// -----------------------------------------------------------------------------------------------
	//	The saveSongs method creates a new file(mp3s.txt) and writes the song objects into it by 
	//	going the entire arraylist(songs) and printing the attributes for each song.
	// -----------------------------------------------------------------------------------------------

	public void saveSongs(ArrayList<Song> arraylist) throws IOException {

		String file = "mp3s.txt";
		int filesize = songs.size();
		System.out.println("filesize: " + filesize);

		FileWriter fw = new FileWriter(file, false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter outFile = new PrintWriter (bw, false);

		outFile.println(filesize);
		outFile.println();
		for(int i=0; i < filesize; i++){
			outFile.println(songs.get(i).getTitle());
			outFile.println(songs.get(i).getArtist());
			outFile.println(songs.get(i).getAlbum());
			outFile.println(songs.get(i).getPath());
			outFile.println();
		}
		outFile.close();


		System.out.println("Songs are stored in textfile");
	}


	// -----------------------------------------------------------------------------------------------
	//	The loadSongs method uses scanner to read the file that was created with the saveSongs 
	//	method. All the information of each song object as well as the number of songs is stored
	//	into a new song object and stored in the arraylist(songs). This method also calls on the
	//	createXMLFormat method and stores the textfile of songs in XML format.
	// -----------------------------------------------------------------------------------------------

	public void loadSongs(File songFile) throws IOException{

		try{
			Scanner scan = new Scanner(songFile);
			int numSongs = scan.nextInt();
			scan.nextLine();
			for (int i = 0; i < numSongs; i++){
				if (scan.nextLine() != ""){ 
					String title = scan.nextLine();
					String artist = scan.nextLine();
					String album = scan.nextLine();
					String path = scan.nextLine();
					Song newSong = new Song (title, artist, album, path);
					songs.add(newSong);
				}
			}
			scan.close();
			createXMLFormat(songFile);
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	// -----------------------------------------------------------------------------------------------
	//	This method creates an new textfile of songs but in XML format. I used xstream and it's many
	//	.jar files to create the XMl format. I created a new arraylist(songListXML) and wrote a 
	//	textfile named XMLmp3.txt where I'll be writing in the songs in XML format. I created a 
	//	new Song object and set all its attributes and then added it to songListXML. This method
	//  gets called whenever the use clicks on the Load Library list and the song textfile is 
	//	stored in XML format. 
	// -----------------------------------------------------------------------------------------------

	public void createXMLFormat(File mp3file) throws IOException{
		XStream xstream = new XStream();
		ArrayList<Song> songListXML = new ArrayList<Song>();
		String file = "XMLmp3s.txt";
		int filesize = songs.size();
		System.out.println("filesize: " + filesize);

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter outFile = new PrintWriter (bw);

		outFile.println(filesize);
		outFile.println();

		for (int i = 0; i < filesize; i++){
			Song songXML = new Song("title", "artist", "album", "path");
			songXML.setTitle(new String(songs.get(i).getTitle()));
			songXML.setArtist(new String(songs.get(i).getArtist()));
			songXML.setAlbum(new String(songs.get(i).getAlbum()));
			songXML.setPath(new String(songs.get(i).getPath()));
			songListXML.add(songXML);
		}

		String test = xstream.toXML(songListXML);
		outFile.println(test);
		System.out.println("Songs are stored in textfile");

		outFile.close();
	}

	// -----------------------------------------------------------------------------------------------
	//	This method sorts the arraylist by the title of the song object. Referenced from the textbook.
	// -----------------------------------------------------------------------------------------------

	public void selectionSort(ArrayList<Song> arraylist){
		int min;
		Song temp;
		for (int index = 0; index < arraylist.size()-1; index++){
			min = index;
			for (int scan = index + 1; scan < arraylist.size(); scan++)
				if (arraylist.get(scan).getTitle().compareTo(arraylist.get(min).getTitle()) < 0)
					min = scan;
			temp = arraylist.get(min);
			arraylist.set(min, arraylist.get(index));
			arraylist.set(index, temp);
		}
	}


	//	getters and setters for arrayList songs
	public ArrayList<Song> getSongs() {
		return songs;
	}

	public void clear() {
		songs.clear();
	}

}
