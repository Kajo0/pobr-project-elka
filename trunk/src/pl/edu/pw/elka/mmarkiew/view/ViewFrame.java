package pl.edu.pw.elka.mmarkiew.view;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import pl.edu.pw.elka.mmarkiew.model.ImageHolder;
import pl.edu.pw.elka.mmarkiew.model.ImageProcessor;

/**
 * Main App Frame
 * 
 * @author Mikolaj Markiewicz
 */
@SuppressWarnings("serial")
public class ViewFrame extends JFrame {

	/** Gather chosen images */
	private ImageHolder imageHolder;
	
	/** Image processor instance */
	private ImageProcessor imageProcessor;
	
	/** File chooser opening button */
	private JButton openButton;
	
	/** Paint loaded image button */
	private JButton repaintButton;
	
	/** Start image processing => recognition button */
	private JButton processButton;
	
	/** Start image processing also on inverted image => recognition button */
	private JButton processInvertButton;
	
	/** Reset image processor instance - for moment range calculations button */
	private JButton newProcessButton;
	
	/** Iterate segmentation for loaded images button */
	private JButton iterateProcess;
	
	/** Moment range calculation for segmented images from iteration button */
	private JButton calculateMomentsButton;

	/**
	 * C-tor
	 * Creates window
	 */
	public ViewFrame() {
		super("POBR - AMD recognition");

		setBounds(100, 100, 300, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(7, 1));
		setVisible(true);

		init();
	}

	/**
	 * Initialize buttons
	 */
	private void init() {

		openButton = new JButton("Choose image to recognition");
		repaintButton = new JButton("Paint first loaded image");
		processButton = new JButton("Single recognition");
		processInvertButton = new JButton("Single recognition with invertion");
		newProcessButton = new JButton("Clear segmented images");
		calculateMomentsButton = new JButton("Calculate range of moments");
		iterateProcess = new JButton("Iterate segmentation");


		/**
		 * Open image chooser
		 */
		final ActionListener openAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(final File file) {
						if (file.isDirectory())
							return true;

						String ext = getExtension(file);
						if (ext != null)
							if (ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("jpg") ||
																									ext.equals("jpeg"))
								return true;

						return false;
					}

					public String getExtension(final File file) {
						String ext = null;
						String s = file.getName();

						int i = s.lastIndexOf('.');
						if (i > 0 && i < s.length() - 1)
							ext = s.substring(i + 1).toLowerCase();

						return ext;
					}

					@Override
					public String getDescription() {
						return "Images (png, jpg, gif, bmp)";
					}
				});

				chooser.showOpenDialog(ViewFrame.this);
				File[] files = chooser.getSelectedFiles();

				// Put images into holder
				if (files != null)
					imageHolder = new ImageHolder(files);
			}
		};

		/**
		 * Paint first loaded image if at least one loaded
		 */
		ActionListener repaintAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (imageHolder != null && imageHolder.getAmount() != 0)
					new ImagePainter(imageHolder.getImage(), imageHolder.getName(0));
			}
		};

		/**
		 * Do recognition if at least one image loaded
		 */
		ActionListener processAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageHolder.getAmount() != 0) {
					imageProcessor = new ImageProcessor(imageHolder.getImage());
					imageProcessor.process(false);
				}
			}
		};

		/**
		 * Do recognition also on inverted image if at least one image loaded
		 */
		ActionListener processInvertAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageHolder.getAmount() != 0) {
					imageProcessor = new ImageProcessor(imageHolder.getImage());
					imageProcessor.process(true);
				}
			}
		};
		
		/**
		 * Create new image processor if at least one image loaded
		 */
		ActionListener newProcessAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageHolder != null && imageHolder.getAmount() != 0)
					imageProcessor = new ImageProcessor(imageHolder.getImage());
			}
		};

		/**
		 * Calculate moments for segmented images
		 */
		final ActionListener calculateMomentsAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageProcessor != null)
					imageProcessor.calculateSegmentsMoments();
			}
		};

		/**
		 * Iterate segmentation for loaded images
		 * Show result dialog at the end to compare correctness of segmentation
		 */
		ActionListener iterateAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {});
				
				if (imageHolder.getAmount() == 0)
					return;

				if (imageProcessor == null)
					imageProcessor = new ImageProcessor(imageHolder.getImage());

				StringBuilder str = new StringBuilder();
				int i = 0, j = 0;
				for (BufferedImage img : imageHolder.getImages()) {
					str.append("\t\t\t\t\t" + imageHolder.getName(i++) + "\n\n");

					imageProcessor.initProcessor(img);
					imageProcessor.processComputation();

					j += ImageProcessor.debugInt;
					str.append(ImageProcessor.debugString);
					str.append("\n\n\n##########################################################################\n\n");
				}
				str.append("\t" + i + " images and " + j + " segments computed");

				JTextArea textArea = new JTextArea(6, 25);
				textArea.setText(str.toString());
				textArea.setEditable(true);
				JScrollPane scrollPane = new JScrollPane(textArea);
				JOptionPane.showMessageDialog(new JFrame(), scrollPane);
				
				calculateMomentsAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {});
			}
		};

		/*
		 * Link listeners
		 */
		openButton.addActionListener(openAction);
		repaintButton.addActionListener(repaintAction);
		processButton.addActionListener(processAction);
		processInvertButton.addActionListener(processInvertAction);
		newProcessButton.addActionListener(newProcessAction);
		iterateProcess.addActionListener(iterateAction);
		calculateMomentsButton.addActionListener(calculateMomentsAction);
		
		/*
		 * Add buttons to frame
		 */
		Container pane = getContentPane();
		pane.add(openButton);
		pane.add(repaintButton);
		pane.add(processButton);
		pane.add(processInvertButton);
		pane.add(newProcessButton);
		pane.add(iterateProcess);
		pane.add(calculateMomentsButton);
	}

}
