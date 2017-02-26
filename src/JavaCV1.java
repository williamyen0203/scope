import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class JavaCV1 {	
	// spacing
	public static int PADDING = 5;
	public static int MD_SPACING = 25;
	public static int SM_SPACING = 10;

	// main UI component dimensions
	public static int HEIGHT = 480;
	public static int WIDTH = 640;
	
	// window dimensions
	public static int WINDOW_H = HEIGHT * 2 + MD_SPACING * 2;
	public static int WINDOW_W = WIDTH + 260;
	
	// location of marker
	public static String MARKER_LOCATION = "match.png";
	
	// threshold for determining if aligned or not
	public static double THRESHOLD_LOW = 0.05;
	public static double THRESHOLD_MED = 0.10;
	
	public static JLabel markerImgLabel;
	
	public static void main(String[] args) throws Exception {
		setUIDefaults();

		JFrame container = new JFrame();
		
		///////////////
		// left pane //
		///////////////
		
		// create camera display
		JPanel cPane = new JPanel();
		cPane.setSize(WIDTH, HEIGHT);
		cPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		cPane.setVisible(true);
		JLabel camLabel = new JLabel();
		
		// create analysis display
		APanel aPane = new APanel();
		aPane.setSize(WIDTH, HEIGHT);
		aPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		aPane.setBackground(Color.WHITE);
		aPane.setVisible(true);
		
		// left panel
		JPanel leftPane = new JPanel();
		leftPane.setSize(WIDTH, HEIGHT);
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
		
		leftPane.add(cPane);
		leftPane.add(Box.createRigidArea(new Dimension(0, 10)));
		leftPane.add(aPane);

		////////////////
		// right pane //
		////////////////

		// standard error
		JLabel errLabel = new JLabel("Standard Error");
		errLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JTextField errText = new JTextField("...", 10);
		errText.setFont(new Font("Segoe UI", Font.BOLD, 32));
		errText.setHorizontalAlignment(JTextField.CENTER);
		errText.setForeground(Color.WHITE);
		errText.setBackground(new Color(28, 28, 28));
		errText.setEditable(false);
		
		// status
		JLabel statusLabel = new JLabel("Alignment");
		statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JTextField statusText = new JTextField("...", 10);
		statusText.setFont(new Font("Segoe UI", Font.BOLD, 32));
		statusText.setHorizontalAlignment(JTextField.CENTER);
		statusText.setBackground(new Color(28, 28, 28));
		statusText.setEditable(false);
		
		// number of markers
		int tagNum;
		String[] posChoices = {"3", "4", "5", "6", "7"};
		JLabel markersLabel = new JLabel("Number of Markers");
		markersLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JComboBox<?> markersBox = new JComboBox<Object>(posChoices);
		((JLabel) markersBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		markersBox.getEditor().getEditorComponent().setBackground(new Color(28, 28, 28));
		markersBox.getEditor().getEditorComponent().setForeground(Color.WHITE);
		markersBox.setEditable(false);
		
		// current marker
		JLabel markerLabel = new JLabel("Current Marker");
		markerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		BufferedImage marker = ImageIO.read(new File(MARKER_LOCATION));
		markerImgLabel = new JLabel(new ImageIcon(marker));
		JButton changeMarker = new JButton("Change");
		changeMarker.addActionListener(e -> {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			int result = fc.showOpenDialog(container);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fc.getSelectedFile();
				MARKER_LOCATION = selectedFile.getAbsolutePath();
				BufferedImage m;
				try {
					m = ImageIO.read(new File(MARKER_LOCATION));
					markerImgLabel.setIcon(new ImageIcon(m));
					markerImgLabel.repaint();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// marker viewer container
		JPanel markerPanel = new JPanel();
		markerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		markerPanel.setPreferredSize(new Dimension(275, 150));
		markerPanel.add(markerImgLabel);
		markerPanel.add(changeMarker);
		
		// credits
		JLabel creditsLabel = new JLabel("Credits");
		creditsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		JLabel credits1 = new JLabel("Made with:");;
		JLabel credits2 = new JLabel("    Java");;
		JLabel credits3 = new JLabel("    OpenCV");
		JLabel credits4 = new JLabel("Created by:");
		JLabel credits5 = new JLabel("    William Yen");
		JLabel credits6 = new JLabel("    Ishan Sethi");
		JLabel credits7 = new JLabel("    Riley Wong");
		JLabel credits8 = new JLabel("Hack@CEWIT 2017");
		credits8.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		rightPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPane.setPreferredSize(new Dimension(WINDOW_W - WIDTH - (PADDING * 6), WINDOW_H - (PADDING * 6)));

		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		rightPane.add(errLabel);
		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		rightPane.add(errText);
		rightPane.add(Box.createRigidArea(new Dimension(0, MD_SPACING)));
		
		rightPane.add(statusLabel);
		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		rightPane.add(statusText);
		rightPane.add(Box.createRigidArea(new Dimension(0, MD_SPACING)));
		
		rightPane.add(markersLabel);
		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		rightPane.add(markersBox);
		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		
		rightPane.add(markerLabel);
		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		rightPane.add(markerPanel);
		rightPane.add(Box.createRigidArea(new Dimension(0, 200)));
		
		rightPane.add(creditsLabel);
		rightPane.add(credits1);
		rightPane.add(credits2);
		rightPane.add(credits3);
		rightPane.add(credits4);
		rightPane.add(credits5);
		rightPane.add(credits6);
		rightPane.add(credits7);
		rightPane.add(Box.createRigidArea(new Dimension(0, SM_SPACING)));
		rightPane.add(credits8);
		rightPane.add(Box.createRigidArea(new Dimension(0, MD_SPACING)));
		
		///////////////
		// container //
		///////////////
		
		container.setTitle("Alignment Analyzer");
		container.setLayout(new FlowLayout(FlowLayout.LEFT, PADDING, PADDING));
		container.setSize(WINDOW_W, WINDOW_H + 20);
		container.setPreferredSize(new Dimension(WINDOW_W + PADDING * 3, WINDOW_H + PADDING * 3 + 10));
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		container.add(leftPane);
		container.add(rightPane);
		
		container.setVisible(true);
		container.setResizable(true);
		
		//////////////////////
		// image processing //
		//////////////////////
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture vc = new VideoCapture(2);

		Mat m = new Mat();
		
		try {
			while (true) {
				if (vc.read(m)) {
					// match
					tagNum = Integer.valueOf((String)markersBox.getSelectedItem());
					ResultsDataObject rdo = match(m, MARKER_LOCATION, tagNum);

					// update camera display
					updateCameraDisplay(cPane, camLabel, rdo);
					
					// update analysis display
					updateAnalysisDisplay(aPane, rdo);
					
					// update results display
					updateResultsDisplay(errText, statusText, rdo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// return Mat of image with all matches
	public static ResultsDataObject match(Mat inFile, String matchFile, int n) {		
		Mat match = Imgcodecs.imread(matchFile);
		Mat img;
		
		double[][] points = new double[n][2];

		for (int i = 0; i < n; i++) {
			img = inFile;

			// create result matrix
			int result_cols = img.cols() - match.cols() + 1;
			int result_rows = img.rows() - match.rows() + 1;
			Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

			// do matching
			Imgproc.matchTemplate(img, match, result, Imgproc.TM_SQDIFF_NORMED);
			Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

			MinMaxLocResult mmr = Core.minMaxLoc(result);

			Point matchLocation = mmr.minLoc;

			// fill in location of match
			Imgproc.rectangle(img, matchLocation,
					new Point(matchLocation.x + match.cols(),
							matchLocation.y + match.rows()),
					new Scalar(0, 255, 0), -1);
			
			points[i][0] = matchLocation.x;
			points[i][1] = matchLocation.y;
		}
		
		ResultsDataObject rdo = new ResultsDataObject(inFile, points);
		
		return rdo;
	}

	// convert Mat to BufferedImage
	public static Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
	
	public static void updateCameraDisplay(JPanel cFrame, JLabel lbl, ResultsDataObject rdo) {
		// display image
		Image matToImg = toBufferedImage(rdo.getMat());
		ImageIcon icon = new ImageIcon(matToImg);
		cFrame.setSize(matToImg.getWidth(null),
				matToImg.getHeight(null));
		cFrame.add(lbl);
		lbl.setIcon(icon);
		
		// repaint
		cFrame.repaint();
	}
	
	public static void updateAnalysisDisplay(APanel aPanel, ResultsDataObject rdo) {
		aPanel.drawPoints(rdo.getPoints());
		
		aPanel.drawLines(rdo.getSlope(), rdo.getIntercept());
		
		// repaint
		aPanel.repaint();
	}
	
	public static void updateResultsDisplay(JTextField errLabel, JTextField statusLabel, ResultsDataObject rdo) {
		double percent = rdo.getStdErr() * 100;
		DecimalFormat df = new DecimalFormat("#.####");
		errLabel.setText(df.format(percent) + "%");
		
		if (rdo.getStdErr() < THRESHOLD_LOW) {
			// good
			statusLabel.setForeground(Color.GREEN);
			statusLabel.setText("Aligned");
		} else if (rdo.getStdErr() < THRESHOLD_MED) {
			// medium
			statusLabel.setForeground(Color.ORANGE);
			statusLabel.setText("Slightly unaligned");
		}
		else {
			// bad
			statusLabel.setForeground(Color.RED);
			statusLabel.setText("Unaligned");
		}
	}
	
	public static void setUIDefaults() {
		// font
		FontUIResource f = new FontUIResource(new Font("Segoe UI Light", Font.PLAIN, 18));
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource){
				UIManager.put(key, f);
			}
		}
		
		// combo box
		UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
		
		// labels
		UIManager.put("Label.foreground", new ColorUIResource(Color.WHITE));
		
		// panel
		UIManager.put("Panel.background", new ColorUIResource(new Color(51, 51, 51)));
	}
}
