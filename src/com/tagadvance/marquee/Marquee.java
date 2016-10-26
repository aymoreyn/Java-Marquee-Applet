package com.tagadvance.marquee;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Marquee extends JApplet {

	public static final int DEFAULT_DELAY = 3000; // milli

	private List<BufferedImage> images;
	private ImageIcon icon;
	private JLabel label;
	private Timer timer;

	@Override
	public void init() {
		images = Collections.synchronizedList(new ArrayList<BufferedImage>());

		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					initGUI();
				}
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.getCause().printStackTrace();
		}
		
		Thread thread = new Thread() {
			public void run() {
				String listParam = getParameter("list");
				if (listParam == null)
					displayErrorDialog("missing parameter \"list\"");
				try {
					String text = Web.cURL(listParam);
					System.out.println("/*");
					System.out.println(text);
					System.out.println("*/");
					// TODO: setup status bar
					for (String spec : text.split("[\n]")) {
						loadImage(spec.trim());
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		};
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}
	
	private void displayErrorDialog(String message) {
		String title = "Error";
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
	
	private void loadImage(String spec) {
		System.out.println("loading image: " + spec);
		try {
			URL input = new URL(spec);
			BufferedImage image = ImageIO.read(input);
			images.add(image);
			
			if(icon.getImage() == null) {
				update(image);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void initGUI() {
		icon = new ImageIcon();
		label = new JLabel(icon);
		Container contentPane = getContentPane();
		Rectangle bounds = new Rectangle(new Point(), contentPane.getSize());
		label.setBounds(bounds);
		contentPane.add(label);
	}

	@Override
	public void start() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					initTimer();
				}
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.getCause().printStackTrace();
		}
	}

	private void initTimer() {
		if (timer == null) {
			int delay = DEFAULT_DELAY;
			String delayParam = getParameter("delay");
			if (delayParam != null) {
				try {
					delay = Integer.parseInt(delayParam);
				} catch (NumberFormatException ex) {
					System.err.println(delayParam);
					ex.printStackTrace();
				}
			}
			timer = new Timer(delay, new ActionListener() {
				private volatile int i = 0;

				public void actionPerformed(ActionEvent e) {
					if (images.isEmpty())
						return;
					// TODO: add random option
					if (i >= images.size())
						i = 0;
					update(images.get(i++));
				}
			});
		}
		timer.start();
	}

	private void update(BufferedImage image) {
		Image oldImage = icon.getImage();
		if (oldImage != null) {
			oldImage.flush();
		}

		System.out.println("Switching to: " + image);
		Dimension size = getContentPane().getSize();
		Color background = getBackgroundColor();
		boolean center = true;
		image = new Thumbnail(image, size.width, size.height, background,
				center);
		icon.setImage(image);
		label.setIcon(icon);
		label.repaint();
	}
	
	private Color getBackgroundColor() {
		Color background = Color.BLACK;
		String backgroundParam = getParameter("delay");
		if(backgroundParam != null) {
			String hex = backgroundParam.replaceAll("[^0-9]", "");
			int rgb = Integer.valueOf(hex, 0xF);
			background = new Color(rgb);
		}
		return background;
	}

	@Override
	public void stop() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					if (timer != null) {
						timer.stop();
					}
				}
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.getCause().printStackTrace();
		}
	}

	@Override
	public void destroy() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					for (BufferedImage image : images) {
						image.flush();
					}
				}
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.getCause().printStackTrace();
		}
	}

	@Override
	public String getAppletInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Copyright \u00a92010 Tag Spilman\n");
		return sb.toString();
	}

	@Override
	public String[][] getParameterInfo() {
		return new String[][]{
				{"list", "url", ""},
				{"delay", "integer", "delay in milliseconds"},
				{"background", "#000000", "background color"},
		};
	}

}