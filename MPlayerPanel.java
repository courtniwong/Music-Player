import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class MPlayerPanel extends JPanel {
	MusicAnalyzer newMusicAnalyzer = new MusicAnalyzer();
	ArrayList<Song> searchSongs = new ArrayList<Song>();
	int numElements;
	PlayerThread newPlayerThread;
	boolean songPlaying = false;
	private JButton playButton, stopButton, exitButton, loadMp3Button, saveButton, openButton, searchButton;
	private JTable table = null;
	private JTextField searchTextField;
	private JLabel search; 
	private JFileChooser chooser;
	//	static private JTextArea ta;

	MPlayerPanel() {

		this.setLayout(new BorderLayout());

		search = new JLabel("Search Songs:");

		searchTextField = new JTextField(10);
		searchTextField.addActionListener(new MyButtonListener());
		//		Uncomment this part to use the JFileChooser
		//		ta = new JTextArea (20,30);
		//		chooser = new JFileChooser();
		//
		//		int status = chooser.showOpenDialog (null);
		//		if (status != JFileChooser.APPROVE_OPTION)
		//			ta.setText ("No File Chosen");
		//		else
		//		{
		//			File file = chooser.getSelectedFile();
		//			Scanner scanChooser;
		//			try {
		//				scanChooser = new Scanner(file);
		//				String info = "";
		//				while (scanChooser.hasNext())
		//					info += scanChooser.nextLine() + "\n";
		//				ta.setText (info);
		//			} catch (FileNotFoundException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//
		//		}
		JPanel buttonPanelTop = new JPanel();
		buttonPanelTop.setLayout(new GridLayout(1,3));
		loadMp3Button = new JButton("Load mp3");
		saveButton = new JButton("Save Library");
		openButton = new JButton("Load Library");

		loadMp3Button.addActionListener(new MyButtonListener());
		saveButton.addActionListener(new MyButtonListener());
		openButton.addActionListener(new MyButtonListener());

		buttonPanelTop.add(loadMp3Button);
		buttonPanelTop.add(saveButton);
		buttonPanelTop.add(openButton);
		this.add(buttonPanelTop, BorderLayout.NORTH);


		// Bottom panel with panels: Play, Stop, Exit buttons
		JPanel buttonPanelBottom = new JPanel();
		buttonPanelBottom.setLayout(new GridLayout(2,3));
		playButton = new JButton("Play");
		stopButton = new JButton("Stop");
		exitButton = new JButton("Exit");
		searchButton = new JButton("Search");


		playButton.addActionListener(new MyButtonListener());
		stopButton.addActionListener(new MyButtonListener());
		exitButton.addActionListener(new MyButtonListener());
		searchButton.addActionListener(new MyButtonListener());


		buttonPanelBottom.add(playButton);
		buttonPanelBottom.add(stopButton);
		buttonPanelBottom.add(exitButton);
		buttonPanelBottom.add(search);
		buttonPanelBottom.add(searchTextField);
		buttonPanelBottom.add(searchButton);
		//		buttonPanelBottom.add(ta);
		this.add(buttonPanelBottom, BorderLayout.SOUTH);

	}

	class MyButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// -----------------------------------------------------------------------------------------------
			//	The loadMP3 button calls the traverse method and traverses through the music directory(dir)
			//	which retrives the tags and info from the songs then creates a song object with all its 
			//	attributes and stores them in the arraylist(songs). Then displays the song and its information
			//	and updates the GUI.
			// -----------------------------------------------------------------------------------------------


			if (e.getSource() == loadMp3Button) {
				System.out.println("Load mp3 button");
				File dir = new File("/Users/courtniwong/Desktop/Music/");
				newMusicAnalyzer.clear();
				try {
					newMusicAnalyzer.traverse(dir, 0) ;
				} catch (CannotReadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TagException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ReadOnlyFileException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidAudioFrameException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}



				numElements = newMusicAnalyzer.getSongs().size();
				String[][] tableElems = new String[numElements][3];
				String[] columnNames = {"Title", "Artist", "Album"};

				for (int i = 0; i<numElements; i++){
					tableElems[i][0] = newMusicAnalyzer.getSongs().get(i).getTitle();
					tableElems[i][1] = newMusicAnalyzer.getSongs().get(i).getArtist();
					tableElems[i][2] = newMusicAnalyzer.getSongs().get(i).getAlbum();
				}

				if (table == null) {
					table = new JTable(tableElems, columnNames );
					JScrollPane scrollPane = new JScrollPane( table );
					add(scrollPane, BorderLayout.CENTER );
				}
				else {
					clearGUI();
					for (int i = 0; i < newMusicAnalyzer.getSongs().size(); i++){
						table.setValueAt(newMusicAnalyzer.getSongs().get(i).getTitle(), i, 0); 
						table.setValueAt(newMusicAnalyzer.getSongs().get(i).getArtist(), i, 1);
						table.setValueAt(newMusicAnalyzer.getSongs().get(i).getAlbum(), i, 2); 
					}

				}
				updateUI();

			}

			// -----------------------------------------------------------------------------------------------
			//	The save button checks to see if table is null or not, if it is an error message is displayed
			//	because no songs have been loaded so there's nothing to be saved into a textfile. If the
			//	songs have been loaded, then the saveSongs method is called on the arraylist(songs) and it
			//	goes through songs and writes all it's information into a textfile.
			// -----------------------------------------------------------------------------------------------

			else if (e.getSource() == saveButton) {
				System.out.println("Save mp3 button");
				if (table==null){
					System.out.println("Whatchu tryna save?");
				}
				else{
					try {
						newMusicAnalyzer.saveSongs(newMusicAnalyzer.getSongs());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				updateUI();
			}


			// -----------------------------------------------------------------------------------------------
			//	The open button reads in from the file created when the saveSongs method was called when the
			//	song button was pressed. Then clears the object of MusicAnalyzer and calls the method 
			//	loadSongs on the file which reads in the info from the file and stores it as a new song 
			//	object which gets added to the arraylist(songs). The method selectionSort is then called to sort
			//	the songs by title. If the table is null, meaning that the user loaded the songs, saved
			//	the songs in a file and has exited the program but then runs the program again and presses
			//	this open button right away, then it will set the columns and rows and then display the
			//	information of the song objects from songs. The GUI is updated to properly display
			//	the information. 
			//
			//	If open button was pressed without exiting the program, the information of the songs will be
			//	displayed without having to set the columns and rows because once GUI elements are created, 
			// 	they can't be overwritten, they are just set as some other information. 
			// -----------------------------------------------------------------------------------------------

			else if (e.getSource() == openButton) {
				System.out.println("Load mp3 from textfile button");
				File songTextFile = new File("/Users/courtniwong/Documents/workspace/Project4/mp3s.txt/");
				try {
					System.out.println("Before clear: " + newMusicAnalyzer.getSongs());
					newMusicAnalyzer.clear();
					System.out.println("After clear: " + newMusicAnalyzer.getSongs());
					newMusicAnalyzer.loadSongs(songTextFile);
					System.out.println("After loadSongs: " + newMusicAnalyzer.getSongs());
					newMusicAnalyzer.selectionSort(newMusicAnalyzer.getSongs());
					System.out.println("After sort: " + newMusicAnalyzer.getSongs());
					System.out.println("Num of songs: " + newMusicAnalyzer.getSongs().size());

					if (table == null){
						numElements = newMusicAnalyzer.getSongs().size();
						String[][] tableElems = new String[numElements][3];
						String[] columnNames = {"Title", "Artist", "Album"};

						for (int i = 0; i<numElements; i++){
							tableElems[i][0] = newMusicAnalyzer.getSongs().get(i).getTitle();
							tableElems[i][1] = newMusicAnalyzer.getSongs().get(i).getArtist();
							tableElems[i][2] = newMusicAnalyzer.getSongs().get(i).getAlbum();
						}

						table = new JTable(tableElems, columnNames );
						JScrollPane scrollPane = new JScrollPane( table );
						add(scrollPane, BorderLayout.CENTER );

						updateUI();
					}

					else{
						for (int i = 0; i < newMusicAnalyzer.getSongs().size(); i++){
							table.setValueAt(newMusicAnalyzer.getSongs().get(i).getTitle(), i, 0); 
							table.setValueAt(newMusicAnalyzer.getSongs().get(i).getArtist(), i, 1);
							table.setValueAt(newMusicAnalyzer.getSongs().get(i).getAlbum(), i, 2); 
						}
					}

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			// -----------------------------------------------------------------------------------------------
			//	The play button checks to see if the table is null, if it is an error message is printed. If
			//	the table is not null, it checks if the boolean(songPlaying) is true or not, which means that
			//	a song is currently playing, if songPlaying is true, we call stop to stop the current song 
			//	from playing and then get the path of the new song that the user wants to play and calls
			// 	start which will play the newly selected song. songPlaying is set to true because a song is
			//	playing and an error message will be printed if the play button is called before loading the
			//	music. 
			// -----------------------------------------------------------------------------------------------

			else if (e.getSource() == playButton) {
				System.out.println("Play mp3 button");
				if (table == null){
					System.out.println("Whatchu tryna play?");
				}
				else {
					if (songPlaying == true){
						newPlayerThread.stop();
					}
					int currentSong = table.getSelectedRow();
					if (currentSong == -1){
						System.out.println("Pick a song homie!");
					}

					else{ 
						String songFilePath = newMusicAnalyzer.getSongs().get(currentSong).getPath();
						newPlayerThread = new PlayerThread(songFilePath);
						newPlayerThread.start();
						songPlaying = true;
					}
				}
			}

			// -----------------------------------------------------------------------------------------------
			//	The stopButton returns an error message if the user presses on the button and the table is
			//	null which is when there are no songs loaded. Then checks if the boolean songPlaying is
			//	true, if it is that means a song is currently playing. Then I call stop to stop the song 
			//	playing and I set the boolean songPlaying back to false. Just a warning though, the stop 
			//	button lags for about 2 seconds before the song stops playing. 
			// -----------------------------------------------------------------------------------------------

			else if (e.getSource() == stopButton) {
				System.out.println("Stop mp3 button");
				if (table == null){
					System.out.println("Whatchu tryna stop?");
				}
				if (songPlaying == true){
					newPlayerThread.stop();
					songPlaying = false;
				}
			}

			// -----------------------------------------------------------------------------------------------
			//	This button simple exits out of the GUI/program.
			// -----------------------------------------------------------------------------------------------

			else if (e.getSource() == exitButton) {
				// Exit the program
				System.exit(0);
			}

			// -----------------------------------------------------------------------------------------------
			//	This button returns an error message if the user presses the button before loading songs.
			//	If the user has loaded the songs before clicking the search button then it grabs the info
			//	from the text field(searchText) and makes it lowercase(lcUserChoice). Then I check if any 
			//	of the song objects in the arraylist contains lcUserChoice, if it does, that song object
			//	gets stored into a new arraylist(searchSongs) and then gets displayed in the table. If no
			//  song objects contained lcUserChoice an error message will be printed. searchText is resetted.
			// -----------------------------------------------------------------------------------------------

			else if (e.getSource() == searchButton) {
				if (table == null){
					System.out.println("You have to load MP3s before searching.");
				}
				else{
					System.out.println("Search button");
					clearGUI();
					String searchText = searchTextField.getText();
					System.out.println("searchtext: " + searchText);
					String lcUserChoice = searchText.toLowerCase();
					System.out.println("lcUserChoice: " + lcUserChoice);
					System.out.println("First arraylist: " + newMusicAnalyzer.getSongs());
					System.out.println("Second arraylist: " + searchSongs);

					for (int i = 0; i < newMusicAnalyzer.getSongs().size(); i++){
						if (newMusicAnalyzer.getSongs().get(i).getTitle().toLowerCase().contains(lcUserChoice) || newMusicAnalyzer.getSongs().get(i).getArtist().toLowerCase().contains(lcUserChoice)){
							searchSongs.add(newMusicAnalyzer.getSongs().get(i));
							System.out.println("Found" + newMusicAnalyzer.getSongs().get(i));
							System.out.println("SearchSongs size: " + searchSongs.size());
						}

					}
					if (searchSongs.size() != 0){
						for (int j = 0; j < searchSongs.size(); j++){
							table.setValueAt(searchSongs.get(j).getTitle(), j, 0);
							table.setValueAt(searchSongs.get(j).getArtist(), j, 1);
							table.setValueAt(searchSongs.get(j).getAlbum(), j, 2);
						}
					}
					else{
						System.out.println("Song not found");
						table.setValueAt("Song not found", 0, 0); 
					}

				}
				searchTextField.setText("");
				searchSongs.clear();

			}
		}

	}

	// -----------------------------------------------------------------------------------------------
	//	This method checks if the table isn't null, if it isn't then it sets the elements of the 
	//	table to an empty string.
	// -----------------------------------------------------------------------------------------------

	public void clearGUI() {
		if(table != null){
			for (int i = 0; i<newMusicAnalyzer.getSongs().size(); i++){
				table.setValueAt("", i, 0); 
				table.setValueAt("", i, 1);
				table.setValueAt("", i, 2); 
			}
		}

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame ("Mp3 player");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		MPlayerPanel panel  = new MPlayerPanel();
		panel.setPreferredSize(new Dimension(400,400));
		frame.getContentPane().add (panel);
		//		frame.getContentPane().add(ta);


		frame.pack();
		frame.setVisible(true);
	}


}