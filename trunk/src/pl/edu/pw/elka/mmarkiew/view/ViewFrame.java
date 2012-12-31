package pl.edu.pw.elka.mmarkiew.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import pl.edu.pw.elka.mmarkiew.model.ImageHolder;
import pl.edu.pw.elka.mmarkiew.model.ImageProcessor;

public class ViewFrame extends JFrame {

	private ImageHolder imageHolder;
	private ImageProcessor imageProcessor;
	private JButton openButton;
	private JButton repaintButton;
	private JButton processButton;
	private JButton newProcessButton;
	private JButton iterateProcess;
	private JButton calculateMomentsButton;

	public ViewFrame() {
		super("POBR");

		setBounds(100, 100, 500, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout());
		setVisible(true);

		init();
	}

	private void init() {

		openButton = new JButton("Open");
		repaintButton = new JButton("Repaint");
		processButton = new JButton("Single Process");
		newProcessButton = new JButton("New Process");
		calculateMomentsButton = new JButton("Calc Moments");
		iterateProcess = new JButton("Iterate");


		final ActionListener openAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setMultiSelectionEnabled(true);

				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(final File file) {
						if (file.isDirectory())
							return true;

						String ext = getExtension(file);
						if (ext != null)
							if (ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("jpg"))
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
						return "png, jpg, gif, bmp";
					}
				});

				chooser.showOpenDialog(ViewFrame.this);
				File[] files = chooser.getSelectedFiles();

				if (files != null)
					imageHolder = new ImageHolder(files);
			}
		};

		ActionListener repaintAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (imageHolder != null && imageHolder.getAmount() != 0)
					new ImagePainter(imageHolder.getImage());
			}
		};

		ActionListener processAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageHolder.getAmount() != 0) {
					imageProcessor = new ImageProcessor(imageHolder.getImage());
					imageProcessor.process();
				}
			}
		};
		
		ActionListener newProcessAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageHolder.getAmount() != 0)
					imageProcessor = new ImageProcessor(imageHolder.getImage());
			}
		};
		
		final ActionListener calculateMomentsAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (imageProcessor != null)
					imageProcessor.calculateSegmentsMoments();
			}
		};

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

					j += imageProcessor.debugInt;
					str.append(imageProcessor.debugString);
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


		openButton.addActionListener(openAction);
		repaintButton.addActionListener(repaintAction);
		processButton.addActionListener(processAction);
		newProcessButton.addActionListener(newProcessAction);
		iterateProcess.addActionListener(iterateAction);
		calculateMomentsButton.addActionListener(calculateMomentsAction);
		
		

		Container pane = getContentPane();
		pane.add(openButton);
		pane.add(repaintButton);
		pane.add(processButton);
		pane.add(newProcessButton);
		pane.add(iterateProcess);
		pane.add(calculateMomentsButton);
	}

}
