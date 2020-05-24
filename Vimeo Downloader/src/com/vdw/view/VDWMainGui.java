package com.vdw.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;
import org.apache.commons.io.*;

import com.vdw.model.*;
import com.vdw.controller.*;

import com.phill.libs.AlertDialog;
import com.phill.libs.FileChooserHelper;
import com.phill.libs.FileFilters;
import com.phill.libs.GraphicsHelper;
import com.phill.libs.JPaintedPanel;
import com.phill.libs.KeyboardAdapter;
import com.phill.libs.PhillFileUtils;
import com.phill.libs.ResourceManager;

/** Implements the main User Interface and all its functionalities.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.4 - 24/05/2020 */
public class VDWMainGui extends JFrame {
	
	// Serial
	private static final long serialVersionUID = 1L;
	
	// Graphical attributes
	private final JTextField textJSONURL;
	private final JButton buttonJSONClipboard;
	private final JButton buttonJSONClear;
	private final JButton buttonJSONParse;
	
	private final JPanel panelVideoInfo, panelAudioInfo;
	private final JComboBox<String> comboVideo, comboAudio;
	private final JLabel textVideoRes, textVideoBitrate, textVideoFPS, textVideoDuration, textVideoSize;
	private final JLabel textAudioBitrate, textAudioSample, textAudioChannel, textAudioDuration, textAudioSize;
	private final JLabel textProgressVideo, textProgressAudio;
	private final JProgressBar progressVideo, progressAudio;
	
	private final JTextField textOutputFile;
	private final JLabel textOutputSize;
	private final JButton buttonOutputSelect, buttonOutputClear;
	
	private final ImageIcon loading = new ImageIcon(ResourceManager.getResource("icon/loading.gif"));
	private final JLabel textLog;
	
	// Creating custom colors
	private final Color gr_dk = new Color(0x0D6B12);
	private final Color rd_dk = new Color(0xBC1742);
	private final Color blue  = new Color(0x1F60CB);
	
	// Dynamic attributes
	private JSONObject json;
	
	private ArrayList<Video> videoList;
	private ArrayList<Audio> audioList;
	
	private Video selectedVideo;
	private Audio selectedAudio;
	
	private File outputFile;

	/** Builds the graphical interface and its functionalities */
	public VDWMainGui() {
		super("VDW - build 20200524");
		
		// Recovering graphical elements from 'res' directory
		GraphicsHelper helper = GraphicsHelper.getInstance();
		Font   font = helper.getFont ();
		Color color = helper.getColor();
		
		Icon pasteIcon = ResourceManager.getResizedIcon("icon/clipboard_past.png",20,20);
		Icon clearIcon = ResourceManager.getResizedIcon("icon/clear.png",20,20);
		Icon parseIcon = ResourceManager.getResizedIcon("icon/cog.png",20,20);
		
		Icon selectIcon = ResourceManager.getResizedIcon("icon/zoom.png",20,20);
		
		Icon exitIcon     = ResourceManager.getResizedIcon("icon/shutdown.png",20,20);
		Icon downloadIcon = ResourceManager.getResizedIcon("icon/save.png",20,20);
		
		// Building UI
		Dimension dimension = new Dimension(1024,640);
		JPanel mainFrame = new JPaintedPanel("img/background.png",dimension);
		setContentPane(mainFrame);
		
		setSize(dimension);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainFrame.setLayout(null);
		
		JPanel panelJSON = new JPanel();
		panelJSON.setOpaque(false);
		panelJSON.setBorder(helper.getTitledBorder("Master.JSON URL"));
		panelJSON.setBounds(12, 12, 1000, 75);
		mainFrame.add(panelJSON);
		panelJSON.setLayout(null);
		
		textJSONURL = new JTextField();
		textJSONURL.setToolTipText("Here goes the 'master.json' URL");
		textJSONURL.requestFocus();
		textJSONURL.setFont(font);
		textJSONURL.setForeground(color);
		textJSONURL.setColumns(10);
		textJSONURL.setBounds(12, 30, 850, 25);
		panelJSON.add(textJSONURL);
		
		buttonJSONClipboard = new JButton(pasteIcon);
		buttonJSONClipboard.addActionListener((event) -> textJSONURL.setText(AlertDialog.copyFromClipboard()));
		buttonJSONClipboard.setToolTipText("Get link from clipboard");
		buttonJSONClipboard.setBounds(875, 30, 30, 25);
		panelJSON.add(buttonJSONClipboard);
		
		buttonJSONClear = new JButton(clearIcon);
		buttonJSONClear.addActionListener((event) -> actionJSONClear());
		buttonJSONClear.setToolTipText("Clear");
		buttonJSONClear.setBounds(915, 30, 30, 25);
		panelJSON.add(buttonJSONClear);
		
		buttonJSONParse = new JButton(parseIcon);
		buttonJSONParse.addActionListener((event) -> actionJSONParse());
		buttonJSONParse.setToolTipText("Parse");
		buttonJSONParse.setBounds(955, 30, 30, 25);
		panelJSON.add(buttonJSONParse);
		
		// Adds 'Enter' event to the input URL textfield
		KeyListener listener = (KeyboardAdapter) (event) -> { if (event.getKeyCode() == KeyEvent.VK_ENTER) buttonJSONParse.doClick(); };
		textJSONURL.addKeyListener(listener);
		
		JPanel panelMedia = new JPanel();
		panelMedia.setOpaque(false);
		panelMedia.setBorder(helper.getTitledBorder("Media Selection"));
		panelMedia.setBounds(12, 95, 1000, 350);
		mainFrame.add(panelMedia);
		panelMedia.setLayout(null);
		
		JPanel panelVideo = new JPanel();
		panelVideo.setOpaque(false);
		panelVideo.setBorder(helper.getTitledBorder("Video"));
		panelVideo.setBounds(12, 25, 480, 314);
		panelVideo.setLayout(null);
		panelMedia.add(panelVideo);
		
		comboVideo = new JComboBox<String>();
		comboVideo.addActionListener((event) -> listenerComboVideo());
		comboVideo.setFont(font);
		comboVideo.setForeground(color);
		comboVideo.setBounds(12, 30, 456, 25);
		panelVideo.add(comboVideo);
		
		panelVideoInfo = new JPanel();
		panelVideoInfo.setOpaque(false);
		panelVideoInfo.setVisible(false);
		panelVideoInfo.setBorder(helper.getTitledBorder("Video Info"));
		panelVideoInfo.setBounds(12, 70, 456, 185);
		panelVideoInfo.setLayout(null);
		panelVideo.add(panelVideoInfo);
		
		JLabel labelVideoRes = new JLabel("Resolution:");
		labelVideoRes.setFont(font);
		labelVideoRes.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoRes.setBounds(12, 30, 95, 20);
		panelVideoInfo.add(labelVideoRes);
		
		textVideoRes = new JLabel();
		textVideoRes.setFont(font);
		textVideoRes.setForeground(color);
		textVideoRes.setBounds(115, 30, 329, 20);
		panelVideoInfo.add(textVideoRes);
		
		JLabel labelVideoBitrate = new JLabel("AVG Bitrate:");
		labelVideoBitrate.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoBitrate.setFont(font);
		labelVideoBitrate.setBounds(12, 60, 95, 20);
		panelVideoInfo.add(labelVideoBitrate);
		
		textVideoBitrate = new JLabel();
		textVideoBitrate.setForeground(color);
		textVideoBitrate.setFont(font);
		textVideoBitrate.setBounds(115, 60, 329, 20);
		panelVideoInfo.add(textVideoBitrate);
		
		JLabel labelVideoFPS = new JLabel("Framerate:");
		labelVideoFPS.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoFPS.setFont(font);
		labelVideoFPS.setBounds(12, 90, 95, 20);
		panelVideoInfo.add(labelVideoFPS);
		
		textVideoFPS = new JLabel();
		textVideoFPS.setForeground(color);
		textVideoFPS.setFont(font);
		textVideoFPS.setBounds(115, 90, 329, 20);
		panelVideoInfo.add(textVideoFPS);
		
		JLabel labelVideoDuration = new JLabel("Duration:");
		labelVideoDuration.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoDuration.setFont(font);
		labelVideoDuration.setBounds(12, 120, 95, 20);
		panelVideoInfo.add(labelVideoDuration);
		
		textVideoDuration = new JLabel();
		textVideoDuration.setForeground(color);
		textVideoDuration.setFont(font);
		textVideoDuration.setBounds(115, 120, 329, 20);
		panelVideoInfo.add(textVideoDuration);
		
		JLabel labelVideoSize = new JLabel("Size (aprox):");
		labelVideoSize.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoSize.setFont(font);
		labelVideoSize.setBounds(12, 150, 95, 20);
		panelVideoInfo.add(labelVideoSize);
		
		textVideoSize = new JLabel();
		textVideoSize.setForeground(color);
		textVideoSize.setFont(font);
		textVideoSize.setBounds(115, 150, 329, 20);
		panelVideoInfo.add(textVideoSize);
		
		textProgressVideo = new JLabel();
		textProgressVideo.setVisible(false);
		textProgressVideo.setHorizontalAlignment(JLabel.CENTER);
		textProgressVideo.setFont(font);
		textProgressVideo.setForeground(color);
		textProgressVideo.setBounds(12, 260, 456, 20);
		panelVideo.add(textProgressVideo);
		
		progressVideo = new JProgressBar();
		progressVideo.setVisible(false);
		progressVideo.setBounds(12, 283, 456, 20);
		panelVideo.add(progressVideo);
		
		JPanel panelAudio = new JPanel();
		panelAudio.setOpaque(false);
		panelAudio.setBorder(helper.getTitledBorder("Audio"));
		panelAudio.setLayout(null);
		panelAudio.setBounds(505, 25, 480, 314);
		panelMedia.add(panelAudio);
		
		comboAudio = new JComboBox<String>();
		comboAudio.addActionListener((event) -> listenerComboAudio());
		comboAudio.setFont(font);
		comboAudio.setForeground(color);
		comboAudio.setBounds(12, 30, 456, 25);
		panelAudio.add(comboAudio);
		
		panelAudioInfo = new JPanel();
		panelAudioInfo.setOpaque(false);
		panelAudioInfo.setVisible(false);
		panelAudioInfo.setBorder(helper.getTitledBorder("Audio Info"));
		panelAudioInfo.setLayout(null);
		panelAudioInfo.setBounds(12, 70, 456, 185);
		panelAudio.add(panelAudioInfo);
		
		JLabel labelAudioBitrate = new JLabel("Bitrate:");
		labelAudioBitrate.setHorizontalAlignment(JLabel.RIGHT);
		labelAudioBitrate.setFont(font);
		labelAudioBitrate.setBounds(12, 30, 95, 20);
		panelAudioInfo.add(labelAudioBitrate);
		
		textAudioBitrate = new JLabel();
		textAudioBitrate.setForeground(color);
		textAudioBitrate.setFont(font);
		textAudioBitrate.setBounds(115, 30, 329, 20);
		panelAudioInfo.add(textAudioBitrate);
		
		JLabel labelAudioSample = new JLabel("Sample Rate:");
		labelAudioSample.setHorizontalAlignment(JLabel.RIGHT);
		labelAudioSample.setFont(font);
		labelAudioSample.setBounds(12, 60, 95, 20);
		panelAudioInfo.add(labelAudioSample);
		
		textAudioSample = new JLabel();
		textAudioSample.setForeground(color);
		textAudioSample.setFont(font);
		textAudioSample.setBounds(115, 60, 329, 20);
		panelAudioInfo.add(textAudioSample);
		
		JLabel labelAudioChannel = new JLabel("Channels:");
		labelAudioChannel.setHorizontalAlignment(JLabel.RIGHT);
		labelAudioChannel.setFont(font);
		labelAudioChannel.setBounds(12, 90, 95, 20);
		panelAudioInfo.add(labelAudioChannel);
		
		textAudioChannel = new JLabel();
		textAudioChannel.setForeground(color);
		textAudioChannel.setFont(font);
		textAudioChannel.setBounds(115, 90, 329, 20);
		panelAudioInfo.add(textAudioChannel);
		
		JLabel labelAudioDuration = new JLabel("Duration:");
		labelAudioDuration.setHorizontalAlignment(JLabel.RIGHT);
		labelAudioDuration.setFont(font);
		labelAudioDuration.setBounds(12, 120, 95, 20);
		panelAudioInfo.add(labelAudioDuration);
		
		textAudioDuration = new JLabel();
		textAudioDuration.setForeground(color);
		textAudioDuration.setFont(font);
		textAudioDuration.setBounds(115, 120, 329, 20);
		panelAudioInfo.add(textAudioDuration);
		
		JLabel labelAudioSize = new JLabel("Size (aprox):");
		labelAudioSize.setHorizontalAlignment(SwingConstants.RIGHT);
		labelAudioSize.setFont(font);
		labelAudioSize.setBounds(12, 150, 95, 20);
		panelAudioInfo.add(labelAudioSize);
		
		textAudioSize = new JLabel();
		textAudioSize.setForeground(color);
		textAudioSize.setFont(font);
		textAudioSize.setBounds(115, 150, 329, 20);
		panelAudioInfo.add(textAudioSize);
		
		textProgressAudio = new JLabel();
		textProgressAudio.setVisible(false);
		textProgressAudio.setHorizontalAlignment(JLabel.CENTER);
		textProgressAudio.setFont(font);
		textProgressAudio.setForeground(color);
		textProgressAudio.setBounds(12, 260, 456, 20);
		panelAudio.add(textProgressAudio);
		
		progressAudio = new JProgressBar();
		progressAudio.setVisible(false);
		progressAudio.setBounds(10, 283, 456, 20);
		panelAudio.add(progressAudio);
		
		JPanel panelOutput = new JPanel();
		panelOutput.setOpaque(false);
		panelOutput.setBorder(helper.getTitledBorder("Output"));
		panelOutput.setBounds(12, 455, 1000, 90);
		mainFrame.add(panelOutput);
		panelOutput.setLayout(null);
		
		textOutputFile = new JTextField();
		textOutputFile.setEditable(false);
		textOutputFile.setForeground(color);
		textOutputFile.setFont(font);
		textOutputFile.setColumns(10);
		textOutputFile.setBounds(12, 30, 890, 25);
		panelOutput.add(textOutputFile);
		
		buttonOutputSelect = new JButton(selectIcon);
		buttonOutputSelect.addActionListener((event) -> actionOutputSelect());
		buttonOutputSelect.setToolTipText("Select file");
		buttonOutputSelect.setBounds(915, 30, 30, 25);
		panelOutput.add(buttonOutputSelect);
		
		buttonOutputClear = new JButton(clearIcon);
		buttonOutputClear.addActionListener((event) -> actionOutputClear());
		buttonOutputClear.setToolTipText("Clear");
		buttonOutputClear.setBounds(955, 30, 30, 25);
		panelOutput.add(buttonOutputClear);
		
		JLabel labelOutputSize = new JLabel("Aprox. Size:");
		labelOutputSize.setFont(font);
		labelOutputSize.setBounds(12, 60, 90, 20);
		panelOutput.add(labelOutputSize);
		
		textOutputSize = new JLabel("0 B");
		textOutputSize.setFont(font);
		textOutputSize.setForeground(color);
		textOutputSize.setBounds(105, 60, 100, 20);
		panelOutput.add(textOutputSize);
		
		textLog = new JLabel();
		textLog.setFont(font);
		textLog.setBounds(12, 565, 912, 25);
		mainFrame.add(textLog);
		
		JButton buttonExit = new JButton(exitIcon);
		buttonExit.addActionListener((event) -> dispose());
		buttonExit.setToolTipText("Exit");
		buttonExit.setBounds(942, 565, 30, 25);
		mainFrame.add(buttonExit);
		
		JButton buttonDownload = new JButton(downloadIcon);
		buttonDownload.addActionListener((event) -> actionDownload());
		buttonDownload.setToolTipText("Download media");
		buttonDownload.setBounds(982, 565, 30, 25);
		mainFrame.add(buttonDownload);
		
		utilResetCombos();
		
		setVisible(true);
		
	}
	
	/**************************************************************************************/
	/************************** Listener Methods Section **********************************/
	
	/** Keeps the UI updated with the current video selection.
	 *  When the user selects a video using the combo box, it's properties are shown inside
	 *  a proper panel. Also the internal references to this video are updated. */
	private void listenerComboVideo() {
		
		// Recovering the selected video index
		final int videoIndex = this.comboVideo.getSelectedIndex();
		
		// Setting panel's visibility depending on the selection (selected = show, otherwise, hide)
		panelVideoInfo.setVisible(videoIndex > 0);
		
		// If a video is selected, the internal reference and graphical labels are updated
		if (videoIndex > 0) {
			
			this.selectedVideo = this.videoList.get(videoIndex - 1);
			
			textVideoRes     .setText(this.selectedVideo.getResolution    ());
			textVideoBitrate .setText(this.selectedVideo.getLabelBitrate  ());
			textVideoFPS     .setText(this.selectedVideo.getLabelFramerate());
			textVideoDuration.setText(this.selectedVideo.getLabelDuration ());
			textVideoSize    .setText(this.selectedVideo.getLabelSize     ());
			
		}
		
		// Otherwise, the internal reference is cleared
		else
			this.selectedVideo = null;
		
		// Updating the media size info
		utilCalculateMediaSize();
		
	}
	
	/** Keeps the UI updated with the current audio selection.
	 *  When the user selects a audio using the combo box, it's properties are shown inside
	 *  a proper panel. Also the internal references to audio video are updated. */
	private void listenerComboAudio() {
		
		// Recovering the selected audio index
		final int audioIndex = this.comboAudio.getSelectedIndex();
		
		// Setting panel's visibility depending on the selection (selected = show, otherwise, hide)
		panelAudioInfo.setVisible(audioIndex > 0);
		
		// If an audio is selected, the internal reference and graphical labels are updated
		if (audioIndex > 0) {
			
			this.selectedAudio = this.audioList.get(audioIndex - 1);
			
			textAudioBitrate .setText(this.selectedAudio.getLabelBitrate   ());
			textAudioSample  .setText(this.selectedAudio.getLabelSamplerate());
			textAudioChannel .setText(this.selectedAudio.getLabelChannels  ());
			textAudioDuration.setText(this.selectedAudio.getLabelDuration  ());
			textAudioSize    .setText(this.selectedAudio.getLabelSize      ());
			
		}
		
		// Otherwise, the internal reference is cleared
		else
			this.selectedAudio = null;
		
		// Updating the media size info
		utilCalculateMediaSize();
		
	}
	
	/************************ Button Event Methods Section ********************************/
	
	/** Resets the entire screen and its internal references. */
	private void actionJSONClear() {
		
		// If a JSON was previously downloaded, a clear dialog is shown
		if (this.json != null) {
			
			String message = ResourceManager.getText(this,"json-clear-confirm.txt",0);
			int choice     = AlertDialog.dialog(message);
			
			// Breaks here when EXIT or CANCEL is selected
			if (choice != AlertDialog.OK_OPTION)
				return;
			
		}
		
		// Resetting parameters and unlocking panels, buttons, etc... 
		this.json = null;
		this.videoList = null;
		this.audioList = null;
		this.outputFile = null;
		
		textOutputFile.setText(null);
		
		utilResetCombos();
		utilLockMasterPanel(false);
		
		textJSONURL.setText(null);
		textJSONURL.requestFocus();
		
	}
	
	/** Downloads the 'master.json' and parse its data. */
	private void actionJSONParse() {
		
		// Getting URL from text field
		final String website = textJSONURL.getText().trim();
		
		// This job needs to be run inside a thread, since it connects to the Internet
		Runnable job = () -> {
		
			try {
				
				// Updating UI
				utilLockMasterPanel(true);
				utilMessage("Downloading 'master.json'...", blue, true);
				
				// Trying to download and parse the JSON object
				final URL jsonURL = new URL(website);
				final JSONObject json = JSONParser.getJSON(jsonURL);
				
				// if I have a proper master.json...
				if (json != null) {
					
					utilMessage("Parsing JSON...", blue, true);
					
					// ...then I save it, ...
					this.json = json;
					
					// ...parse its embedded media, ...
					this.videoList = JSONParser.getVideoList(json);
					this.audioList = JSONParser.getAudioList(json);
					
					// ...and fill the combos.
					utilFillCombo(this.videoList,this.comboVideo);
					utilFillCombo(this.audioList,this.comboAudio);
					
					// When everything finishes, the label is hidden and the clear button shown. 
					utilHideMessage();
					SwingUtilities.invokeLater(() -> buttonJSONClear.setEnabled(true));
					
				}
				
			}
			catch (MalformedURLException exception) {
				utilLockMasterPanel(false);
				utilMessage("Invalid JSON URL", rd_dk, false);
			}
			catch (JSONException exception) {
				utilLockMasterPanel(false);
				utilMessage(exception.getMessage(), rd_dk, false);
			}
			catch (Exception exception) {
				utilLockMasterPanel(false);
				utilMessage("Unknown error occurred, please check the console", rd_dk, false);
			}
		
		};
		
		// Doing the hard work
		Thread jsonParseThread = new Thread(job);
		jsonParseThread.setName("JSON Parse Thread");
		jsonParseThread.start();
		
	}
	
	/** Shows a selection dialog for the output media file. */
	private void actionOutputSelect() {
		
		// Recovering the selected file
		final File file = FileChooserHelper.loadFile(this,FileFilters.MP4,"Select an output file",false,FileChooserHelper.HOME_DIRECTORY);
		
		// If something was selected...
		if (file != null) {
			
			// ... and the file already exists, an overwrite dialog is shown.
			if (file.exists()) {
				
				String message = ResourceManager.getText(this,"output-select-override.txt",0);
				int choice = JOptionPane.showConfirmDialog(this,message);
				
				// If the user doesn't want to overwrite the selected file, the code ends here
				if (choice != JOptionPane.OK_OPTION)
					return;
				
			}
			
			// ... otherwise internal references and UI are updated
			this.outputFile = file;
			textOutputFile.setText(file.getAbsolutePath());
			
		}
		
	}
	
	/** Clears the output file internal references. */
	private void actionOutputClear() {
		
		textOutputFile.setText(null);
		this.outputFile = null;
		
	}
	
	/************************* Utility Methods Section ************************************/

	/** Calculates the aproximated media size (extracted from the 'master.json' file) and
	 *  shows it in a human readable format using the output size label (right down the file
	 *  selection textfield). */
	private synchronized void utilCalculateMediaSize() {
		
		long videoSize = 0, audioSize = 0;
		
		if (this.selectedVideo != null)
			videoSize = this.selectedVideo.getMediaSize();
		
		if (this.selectedAudio != null)
			audioSize = this.selectedAudio.getMediaSize();
		
		textOutputSize.setText(PhillFileUtils.humanReadableByteCount(videoSize + audioSize));
		
	}
	
	/** Fills the given 'comboBox' with information of each individual {@link Media} provided through 'mediaList'.
	 *  @param mediaList - list of available {@link Media} extracted from the 'master.json' file
	 *  @param comboBox - the desired combo to be filled */
	private void utilFillCombo(ArrayList<? extends Media> mediaList, JComboBox<String> comboBox) {
		
		SwingUtilities.invokeLater(() -> {
		
			comboBox.removeAllItems();
			comboBox.addItem("<none>");
			
			for (Media media: mediaList)
				comboBox.addItem(media.getComboInfo());
		
		});
		
	}
	
	/** Sets visibility of the first panel components (panelJSON).
	 *  @param lock - if 'true' then then components are locked. Otherwise, unlocked */
	private void utilLockMasterPanel(final boolean lock) {
		
		final boolean visibility = !lock;
		
		SwingUtilities.invokeLater(() -> {
		
			textJSONURL        .setEditable(visibility);
			buttonJSONClipboard.setEnabled (visibility);
			buttonJSONClear    .setEnabled (visibility);
			buttonJSONParse    .setEnabled (visibility);
		
		});
		
	}
	
	/** Resets the comboboxes */
	private void utilResetCombos() {
		
		final String none = "<none>";
		
		comboVideo.removeAllItems();
		comboAudio.removeAllItems();
		
		comboVideo.addItem(none);
		comboAudio.addItem(none);
		
	}
	

	
	
	
	
	

	
	private void actionDownload() {
		
		if (this.json == null) {
			AlertDialog.erro("You first need to parse a valid 'master.json' file");
			return;
		}
		
		if ((this.selectedVideo == null) && (this.selectedAudio == null)) {
			AlertDialog.erro("Please, select at least one media stream");
			return;
		}
		
		if (this.outputFile == null) {
			AlertDialog.erro("Please, select an output file");
			return;
		}
		
		if (this.outputFile.exists()) {
			
			String message = ResourceManager.getText(this,"output-select-override.txt",0);
			int choice = JOptionPane.showConfirmDialog(this,message);
			
			// If the user doesn't want to overwrite the selected file, the code ends here
			if (choice != JOptionPane.OK_OPTION)
				return;
			
		}
		
		// I must create a dialog here!
		
			
			buttonJSONClear.setEnabled(false);
			comboVideo.setEnabled(false);
			comboAudio.setEnabled(false);
			buttonOutputSelect.setEnabled(false);
			buttonOutputClear.setEnabled(false);
			
			Thread downloader = new Thread(() -> prepare());
			downloader.setName("Downloader thread");
			downloader.start();
			
			
	}
	
	private void prepare() {
		
		utilMessage("Downloading media", blue, true);
		
		try {
			
			Thread audio = downloader(this.selectedAudio, this.progressAudio, this.textProgressAudio);
			Thread video = downloader(this.selectedVideo, this.progressVideo, this.textProgressVideo);
			
			video.start();
			audio.start();
			
			video.join();
			audio.join();
			
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			
			File a = selectedAudio.getTempFile();
			File v = selectedVideo.getTempFile();
			
			String[] cmd = {"ffmpeg","-i",v.getAbsolutePath(), "-i",a.getAbsolutePath(),"-c:v","copy","-c:a","mp3","-movflags","+faststart","-y",outputFile.getAbsolutePath()};

			utilMessage("Merging files with ffmpeg", blue, true);
			
			try {
				int i = Runtime.getRuntime().exec(cmd).waitFor();
				
				if (i == 0) {
					utilMessage("Everything complete", gr_dk, false);
					JOptionPane.showMessageDialog(this,"Everything complete");
				}
				
				else
					utilMessage("Merging process failed", rd_dk, false);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				
				SwingUtilities.invokeLater(() -> {
					
					buttonJSONClear.setEnabled(true);
					comboVideo.setEnabled(true);
					comboAudio.setEnabled(true);
					buttonOutputSelect.setEnabled(true);
					buttonOutputClear.setEnabled(true);
					
				});
				
				utilHideMessage();
				
			}
			
			
		}
		
		
	}
	
	private void utilUpdateProgress(JProgressBar progress, JLabel label, int currentChunk, int totalChunk, long bytesLoaded) {
		
		String labelText = String.format("Downloading chunk %d/%d [%s]",currentChunk,totalChunk,PhillFileUtils.humanReadableByteCount(bytesLoaded));
		
		double percent = 100 * ((double) currentChunk / (double) totalChunk);
	    int intPercent = (int) (percent + 0.5);
		
		SwingUtilities.invokeLater(() -> {
			
			label.setText(labelText);
			progress.setValue(intPercent);
			
		});
		
	}
	
	private Thread downloader(Media media, JProgressBar progress, JLabel label) {

		if (media == null)
			return new Thread();
		
		progress.setValue(0);
		progress.setVisible(true);
		
		label.setVisible(true);
		
		Runnable job = () -> {
			
			try {
				
				String masterURL = this.textJSONURL.getText().trim();
				String   baseURL = this.json.getString("base_url");
				String  mediaURL = media.getBaseURL();
				
				URL tempURL = new URL(new URL(masterURL),baseURL);
				URL videoBaseURL = new URL(tempURL,mediaURL);
				
				long bytesDownloaded = 0L;
				
				File output = media.getTempFile();
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(output));
				
				byte[] init_segment = media.getInitSegment();
				stream.write(init_segment);
				
				bytesDownloaded += init_segment.length;
				
				JSONArray segments = media.getSegments();
				
				utilUpdateProgress(progress, label, 0, segments.length(), bytesDownloaded);
				
				for (int i=0; i<segments.length(); i++) {
					
					JSONObject chunk = (JSONObject) segments.get(i);
					URL chunkURL = new URL(videoBaseURL,chunk.getString("url"));
					
					HttpURLConnection httpConn = (HttpURLConnection) chunkURL.openConnection();
			        int responseCode = httpConn.getResponseCode();
			        
			        if (responseCode == 200) {
			        	
				        InputStream inputStream = httpConn.getInputStream();
				        
				        bytesDownloaded += IOUtils.copy(inputStream, stream);
				        
				        utilUpdateProgress(progress, label, (i+1), segments.length(), bytesDownloaded);
				        
			            inputStream.close();
			            stream.flush();
			            httpConn.disconnect();
			            
			        }
			        // ELSE ??
			        
				}
				
				stream.close();
				
				final String finish = String.format("Downloaded %d chunks [%s]",segments.length(),PhillFileUtils.humanReadableByteCount(bytesDownloaded));
				
				SwingUtilities.invokeLater(() -> {
					label.setForeground(gr_dk);
					label.setText(finish);
				});
				
			}
			catch (Exception e) {
				
				SwingUtilities.invokeLater(() -> {
					label.setForeground(rd_dk);
					label.setText("Something went wrong");
				});
				
				e.printStackTrace();
			}
			
			
		};
		
		Thread thread = new Thread(job);
		thread.setName(media.getMediaType() + " downloader thread");

		return thread;
	}
	
	private void utilMessage(final String message, final Color color, final boolean loading) {
		
		Runnable job = () -> {
			textLog.setText(message);
			textLog.setForeground(color);
			textLog.setIcon(loading ? this.loading : null);
		};
		
		SwingUtilities.invokeLater(job);
	}
	
	private void utilHideMessage() {
		
		Runnable job = () -> {
			textLog.setText(null);
			textLog.setIcon(null);
		};
		
		SwingUtilities.invokeLater(job);
	}
	
	/** Starts the graphical UI */
	public static void main(String[] args) {
		new VDWMainGui();
	}
	
}
