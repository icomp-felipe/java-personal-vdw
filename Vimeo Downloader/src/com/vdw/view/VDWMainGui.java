package com.vdw.view;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.json.*;

import com.phill.libs.AlertDialog;
import com.phill.libs.FileChooserHelper;
import com.phill.libs.FileFilters;
import com.phill.libs.GraphicsHelper;
import com.phill.libs.JPaintedPanel;
import com.phill.libs.KeyboardAdapter;
import com.phill.libs.PhillFileUtils;
import com.phill.libs.ResourceManager;

import com.vdw.controller.*;
import com.vdw.model.*;

public class VDWMainGui extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final JTextField textJSON, textOutputPath;
	private final JComboBox<String> comboVideo, comboAudio;
	private final JLabel textLog;
	
	private final ImageIcon loading = new ImageIcon(ResourceManager.getResource("icon/loading.gif"));
	
	private JSONObject json;
	
	private ArrayList<Video> videoList;
	private ArrayList<Audio> audioList;
	
	private File outputFile;
	
	private Video selectedVideo;
	private Audio selectedAudio;
	
	private final JPanel panelVideoInfo, panelAudioInfo;
	private final JLabel textVideoRes, textVideoBitrate, textVideoFPS, textVideoDuration, textVideoSize;
	private final JLabel textAudioBitrate, textAudioSample, textAudioChannel, textAudioDuration, textAudioSize;
	private JLabel textOutputSize;
	private JButton buttonClipboard;
	private JButton buttonClear;
	private JButton buttonParse;
	
	public static void main(String[] args) {
		new VDWMainGui();
	}

	public VDWMainGui() {
		super("VDW - build 20200521");
		
		Dimension dimension = new Dimension(1024,768);
		JPanel mainFrame = new JPaintedPanel("img/background.png",dimension);
		setContentPane(mainFrame);

		GraphicsHelper helper = GraphicsHelper.getInstance();
		Font   font = helper.getFont ();
		Color color = helper.getColor();
		
		Icon pasteIcon = ResourceManager.getResizedIcon("icon/clipboard_past.png",20,20);
		Icon clearIcon = ResourceManager.getResizedIcon("icon/clear.png",20,20);
		Icon parseIcon = ResourceManager.getResizedIcon("icon/cog.png",20,20);
		
		Icon selectIcon = ResourceManager.getResizedIcon("icon/zoom.png",20,20);
		
		Icon exitIcon     = ResourceManager.getResizedIcon("icon/shutdown.png",20,20);
		Icon downloadIcon = ResourceManager.getResizedIcon("icon/save.png",20,20);
		
		setSize(dimension);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel panelJSON = new JPanel();
		panelJSON.setOpaque(false);
		panelJSON.setBorder(helper.getTitledBorder("Master.JSON URL"));
		panelJSON.setBounds(12, 12, 1000, 75);
		getContentPane().add(panelJSON);
		panelJSON.setLayout(null);
		
		textJSON = new JTextField();
		textJSON.setToolTipText("Here goes the 'master.json' URL");
		textJSON.requestFocus();
		textJSON.setFont(font);
		textJSON.setForeground(color);
		textJSON.setColumns(10);
		textJSON.setBounds(12, 30, 850, 25);
		panelJSON.add(textJSON);
		
		buttonClipboard = new JButton(pasteIcon);
		buttonClipboard.addActionListener((event) -> actionCopyFromClipboard());
		buttonClipboard.setToolTipText("Get link from clipboard");
		buttonClipboard.setBounds(875, 30, 30, 25);
		panelJSON.add(buttonClipboard);
		
		buttonClear = new JButton(clearIcon);
		buttonClear.addActionListener((event) -> actionClear());
		buttonClear.setToolTipText("Clear");
		buttonClear.setBounds(915, 30, 30, 25);
		panelJSON.add(buttonClear);
		
		buttonParse = new JButton(parseIcon);
		buttonParse.addActionListener((event) -> actionParse());
		buttonParse.setToolTipText("Parse");
		buttonParse.setBounds(955, 30, 30, 25);
		panelJSON.add(buttonParse);
		
		KeyListener listener = (KeyboardAdapter) (event) -> { if (event.getKeyCode() == KeyEvent.VK_ENTER) buttonParse.doClick(); };
		textJSON.addKeyListener(listener);
		
		JPanel panelMedia = new JPanel();
		panelMedia.setOpaque(false);
		panelMedia.setBorder(helper.getTitledBorder("Media Selection"));
		panelMedia.setBounds(12, 95, 1000, 350);
		getContentPane().add(panelMedia);
		panelMedia.setLayout(null);
		
		JPanel panelVideo = new JPanel();
		panelVideo.setOpaque(false);
		panelVideo.setBorder(helper.getTitledBorder("Video"));
		panelVideo.setBounds(12, 25, 480, 314);
		panelVideo.setLayout(null);
		panelMedia.add(panelVideo);
		
		comboVideo = new JComboBox<String>();
		comboVideo.addItem("<none>");
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
		
		JLabel labelVideoBitrate = new JLabel("AVG Bitrate:");
		labelVideoBitrate.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoBitrate.setFont(font);
		labelVideoBitrate.setBounds(12, 60, 95, 20);
		panelVideoInfo.add(labelVideoBitrate);
		
		JLabel labelVideoFPS = new JLabel("Framerate:");
		labelVideoFPS.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoFPS.setFont(font);
		labelVideoFPS.setBounds(12, 90, 95, 20);
		panelVideoInfo.add(labelVideoFPS);
		
		JLabel labelVideoDuration = new JLabel("Duration:");
		labelVideoDuration.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoDuration.setFont(font);
		labelVideoDuration.setBounds(12, 120, 95, 20);
		panelVideoInfo.add(labelVideoDuration);
		
		JLabel labelVideoSize = new JLabel("Size (aprox):");
		labelVideoSize.setHorizontalAlignment(JLabel.RIGHT);
		labelVideoSize.setFont(font);
		labelVideoSize.setBounds(12, 150, 95, 20);
		panelVideoInfo.add(labelVideoSize);
		
		textVideoRes = new JLabel();
		textVideoRes.setFont(font);
		textVideoRes.setForeground(color);
		textVideoRes.setBounds(115, 30, 329, 20);
		panelVideoInfo.add(textVideoRes);
		
		textVideoBitrate = new JLabel();
		textVideoBitrate.setForeground(color);
		textVideoBitrate.setFont(font);
		textVideoBitrate.setBounds(115, 60, 329, 20);
		panelVideoInfo.add(textVideoBitrate);
		
		textVideoFPS = new JLabel();
		textVideoFPS.setForeground(color);
		textVideoFPS.setFont(font);
		textVideoFPS.setBounds(115, 90, 329, 20);
		panelVideoInfo.add(textVideoFPS);
		
		textVideoDuration = new JLabel();
		textVideoDuration.setForeground(color);
		textVideoDuration.setFont(font);
		textVideoDuration.setBounds(115, 120, 329, 20);
		panelVideoInfo.add(textVideoDuration);
		
		textVideoSize = new JLabel();
		textVideoSize.setForeground(color);
		textVideoSize.setFont(font);
		textVideoSize.setBounds(115, 150, 329, 20);
		panelVideoInfo.add(textVideoSize);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(12, 283, 456, 20);
		panelVideo.add(progressBar);
		
		JLabel textProgressVideo = new JLabel("Chunk 5/52 [3.78 MB]");
		textProgressVideo.setHorizontalAlignment(JLabel.CENTER);
		textProgressVideo.setFont(font);
		textProgressVideo.setForeground(color);
		textProgressVideo.setBounds(12, 260, 456, 20);
		panelVideo.add(textProgressVideo);
		
		JPanel panelAudio = new JPanel();
		panelAudio.setOpaque(false);
		panelAudio.setBorder(helper.getTitledBorder("Audio"));
		panelAudio.setLayout(null);
		panelAudio.setBounds(505, 25, 480, 314);
		panelMedia.add(panelAudio);
		
		comboAudio = new JComboBox<String>();
		comboAudio.addItem("<none>");
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
		
		JProgressBar progressBar_1 = new JProgressBar();
		progressBar_1.setBounds(10, 283, 456, 20);
		panelAudio.add(progressBar_1);
		
		JLabel textProgressAudio = new JLabel("Chunk 5/52 [3.78 MB]");
		textProgressAudio.setHorizontalAlignment(JLabel.CENTER);
		textProgressAudio.setFont(font);
		textProgressAudio.setForeground(color);
		textProgressAudio.setBounds(12, 260, 456, 20);
		panelAudio.add(textProgressAudio);
		
		JPanel panelOutput = new JPanel();
		panelOutput.setOpaque(false);
		panelOutput.setBorder(helper.getTitledBorder("Output"));
		panelOutput.setBounds(12, 456, 1000, 90);
		getContentPane().add(panelOutput);
		panelOutput.setLayout(null);
		
		textOutputPath = new JTextField();
		textOutputPath.setEditable(false);
		textOutputPath.setForeground(color);
		textOutputPath.setFont(font);
		textOutputPath.setColumns(10);
		textOutputPath.setBounds(12, 30, 890, 25);
		panelOutput.add(textOutputPath);
		
		JButton buttonFileSelect = new JButton(selectIcon);
		buttonFileSelect.addActionListener((event) -> actionFileSelect());
		buttonFileSelect.setToolTipText("Select file");
		buttonFileSelect.setBounds(915, 30, 30, 25);
		panelOutput.add(buttonFileSelect);
		
		JButton buttonFileClear = new JButton(clearIcon);
		buttonFileClear.addActionListener((event) -> actionFileClear());
		buttonFileClear.setToolTipText("Clear");
		buttonFileClear.setBounds(955, 30, 30, 25);
		panelOutput.add(buttonFileClear);
		
		JLabel labelOutputSize = new JLabel("Aprox. Size:");
		labelOutputSize.setFont(font);
		labelOutputSize.setBounds(12, 60, 90, 20);
		panelOutput.add(labelOutputSize);
		
		textOutputSize = new JLabel("0 B");
		textOutputSize.setFont(font);
		textOutputSize.setForeground(color);
		textOutputSize.setBounds(105, 60, 100, 20);
		panelOutput.add(textOutputSize);
		
		JButton buttonExit = new JButton(exitIcon);
		buttonExit.addActionListener((event) -> dispose());
		buttonExit.setToolTipText("Exit");
		buttonExit.setBounds(942, 706, 30, 25);
		getContentPane().add(buttonExit);
		
		JButton buttonDownload = new JButton(downloadIcon);
		buttonDownload.addActionListener((event) -> actionDownload());
		buttonDownload.setToolTipText("Download media");
		buttonDownload.setBounds(982, 706, 30, 25);
		getContentPane().add(buttonDownload);
		
		textLog = new JLabel();
		textLog.setFont(font);
		textLog.setBounds(12, 711, 912, 20);
		getContentPane().add(textLog);
		
		setVisible(true);
		
	}

	private void actionClear() {
		
		if (this.json != null) {
			
			int option = AlertDialog.dialog("When clearing, the entire screen is reset.\nDo you still need to continue?");
			
			if (option != AlertDialog.OK_OPTION)
				return;
			
		}
		
		utilLockMasterPanel(false);
		
		this.json = null;
		this.videoList = null;
		this.audioList = null;
		
		this.outputFile = null;
		textOutputPath.setText(null);
		
		resetCombos();
		
		textJSON.setText(null);
		textJSON.requestFocus();
		
	}
	
	private void resetCombos() {
		
		comboVideo.removeAllItems();
		comboAudio.removeAllItems();
		
		final String none = "<none>";
		
		comboVideo.addItem(none);
		comboAudio.addItem(none);
		
	}
	
	private void actionParse() {
		
		// Getting URL from text field
		final String website = textJSON.getText().trim();
		
		// This job needs to be run inside a thread, as soon as it gets connected to the Internet
		Runnable job = () -> {
		
			try {
				
				utilLockMasterPanel(true);
				utilMessage("Downloading 'master.json'...", Color.BLUE, true);
				
				// Trying to download and parse the JSON object
				final URL jsonURL = new URL(website);
				final JSONObject json = JSONParser.getJSON(jsonURL);
				
				// if I have a proper master.json...
				if (json != null) {
					
					utilMessage("Parsing JSON...", Color.BLUE, true);
					
					// ...then I save it and...
					this.json = json;
					
					// ...parse its embedded media...
					this.videoList = JSONParser.getVideoList(json);
					this.audioList = JSONParser.getAudioList(json);
					
					// ...and fill the combos
					utilFillCombo(this.videoList,this.comboVideo);
					utilFillCombo(this.audioList,this.comboAudio);
					
					utilHideMessage();
					SwingUtilities.invokeLater(() -> buttonClear.setEnabled(true));
					
				}
				
			}
			catch (MalformedURLException exception) {
				utilLockMasterPanel(false);
				utilMessage("Invalid JSON URL", Color.RED, false);
			}
			catch (JSONException exception) {
				utilLockMasterPanel(false);
				utilMessage(exception.getMessage(), Color.RED, false);
			}
			catch (Exception exception) {
				utilLockMasterPanel(false);
				utilMessage("Unknown error occurred, please check the console", Color.RED, false);
			}
		
		};
		
		new Thread(job).start();
		
	}
	
	private void utilLockMasterPanel(final boolean lock) {
		
		final boolean visibility = !lock;
		
		SwingUtilities.invokeLater(() -> {
		
			textJSON       .setEditable(visibility);
			buttonClipboard.setEnabled (visibility);
			buttonClear    .setEnabled (visibility);
			buttonParse    .setEnabled (visibility);
		
		});
		
	}
	
	private void actionDownload() {
		
		if (this.json == null) {
			AlertDialog.erro("You first need to parse a valid 'master.json'");
			return;
		}
		
		if ((this.selectedVideo == null) && (this.selectedAudio == null)) {
			AlertDialog.erro("Select at least one media stream");
			return;
		}
		
		if (this.outputFile == null) {
			AlertDialog.erro("Select an output file");
			return;
		}
		
		// I must create a dialog here!
		
		try {
			downloader(this.selectedVideo, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void downloader(Media media, JProgressBar progress, JLabel label) throws Exception {

		String masterURL = this.textJSON.getText().trim();
		String   baseURL = this.json.getString("base_url");
		String  mediaURL = media.getBaseURL();
		
		URL tempURL = new URL(new URL(masterURL),baseURL);
		URL videoBaseURL = new URL(tempURL,mediaURL);
		
		File output = media.getTempFile();
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(output,true));
		
		byte[] init_segment = media.getInitSegment();
		stream.write(init_segment);
		
		JSONArray segments = media.getSegments();
		
		for (int i=0; i<segments.length(); i++) {
			
			JSONObject chunk = (JSONObject) segments.get(i);
			URL chunkURL = new URL(videoBaseURL,chunk.getString("url"));
			
			HttpURLConnection httpConn = (HttpURLConnection) chunkURL.openConnection();
	        int responseCode = httpConn.getResponseCode();
	        
	        if (responseCode == 200) {
	        	
	        	System.out.print("Downloading chunk " + (i+1) + "/" + segments.length() + "...");
	        	
		        InputStream inputStream = httpConn.getInputStream();
		        
		        IOUtils.copy(inputStream, stream);
		        
	            inputStream.close();
	            stream.flush();
	            httpConn.disconnect();
	            
	            System.out.println("ok");
	        	
	        }
			
		}
		
		stream.close();
		
	}
	
	private void listenerComboVideo() {
		
		final int videoIndex = this.comboVideo.getSelectedIndex();
		
		panelVideoInfo.setVisible(videoIndex > 0);
		
		if (videoIndex > 0) {
			
			this.selectedVideo = this.videoList.get(videoIndex - 1);
			
			textVideoRes     .setText(this.selectedVideo.getResolution    ());
			textVideoBitrate .setText(this.selectedVideo.getLabelBitrate  ());
			textVideoFPS     .setText(this.selectedVideo.getLabelFramerate());
			textVideoDuration.setText(this.selectedVideo.getLabelDuration ());
			textVideoSize    .setText(this.selectedVideo.getLabelSize     ());
			
		}
		else
			this.selectedVideo = null;
		
		listenerCalculateMediaSize();
		
	}
	
	private void listenerComboAudio() {
		
		final int audioIndex = this.comboAudio.getSelectedIndex();
		
		panelAudioInfo.setVisible(audioIndex > 0);
		
		if (audioIndex > 0) {
			
			this.selectedAudio = this.audioList.get(audioIndex - 1);
			
			textAudioBitrate .setText(this.selectedAudio.getLabelBitrate   ());
			textAudioSample  .setText(this.selectedAudio.getLabelSamplerate());
			textAudioChannel .setText(this.selectedAudio.getLabelChannels  ());
			textAudioDuration.setText(this.selectedAudio.getLabelDuration  ());
			textAudioSize    .setText(this.selectedAudio.getLabelSize      ());
			
		}
		else
			this.selectedAudio = null;
		
		listenerCalculateMediaSize();
		
	}
	
	private synchronized void listenerCalculateMediaSize() {
		
		long videoSize = 0, audioSize = 0;
		
		if (this.selectedVideo != null)
			videoSize = this.selectedVideo.getMediaSize();
		
		if (this.selectedAudio != null)
			audioSize = this.selectedAudio.getMediaSize();
		
		textOutputSize.setText(PhillFileUtils.humanReadableByteCount(videoSize + audioSize));
		
	}
	
	private void utilFillCombo(ArrayList<? extends Media> mediaList, JComboBox<String> comboBox) {
		
		SwingUtilities.invokeLater(() -> {
		
			comboBox.removeAllItems();
			comboBox.addItem("<none>");
			
			for (Media media: mediaList)
				comboBox.addItem(media.getComboInfo());
		
		});
		
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
	
	private void actionCopyFromClipboard() {
		
		final String url = AlertDialog.copyFromClipboard();
		
		textJSON.setText(url);
		
	}
	
	private void actionFileSelect() {
		
		final File file = FileChooserHelper.loadFile(this,FileFilters.MKV,"Select an output file",true,FileChooserHelper.HOME_DIRECTORY);
		
		if (file != null) {
			
			this.outputFile = file;
			textOutputPath.setText(file.getAbsolutePath());
			
		}
		
	}
	
	private void actionFileClear() {
		
		textOutputPath.setText(null);
		this.outputFile = null;
		
	}
}
