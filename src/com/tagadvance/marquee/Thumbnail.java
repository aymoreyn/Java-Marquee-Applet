package com.tagadvance.marquee;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Thumbnail extends BufferedImage {

	protected Thumbnail(int width, int height, int imageType) {
		super(width, height, imageType == 0 ? TYPE_INT_ARGB : imageType); //PNG
	}

	public Thumbnail(BufferedImage image, int maxWidth, int maxHeight) {
		this(image, maxWidth, maxHeight, Color.WHITE, true);
	}

	/**
	 * Scales <code>image</code> to <code>maxWidth</code>, <code>maxHeight</code>.
	 * The scaling used prevents landscape/portrait distortion, however, 
	 * loss of image quality may occur due to shrinking.
	 */
	public Thumbnail(BufferedImage image, int maxWidth, int maxHeight,
			Color background, boolean center) {
		this(maxWidth, maxHeight, image.getType());

		//scale it
		Dimension scale = scale(image, maxWidth, maxHeight);

		double offset = .5; //center
		int x = center ? (int) ((maxWidth - scale.width) * offset) : 0;
		int y = center ? (int) ((maxHeight - scale.height) * offset) : 0;

		//draw it
		Graphics2D g2d = createGraphics();
		if (background != null) {
			g2d.setColor(background);
			g2d.fillRect(0, 0, maxWidth, maxHeight);
		}
		g2d.drawImage(image, x, y, scale.width, scale.height, null);
		g2d.dispose();
	}

	/**
	 * Scales and crops <code>image</code>.
	 * <b>Warning:</b> actual image size may not match either <code>maxWidth</code> or <code>maxHeight</code>
	 * @param image
	 * @param maxWidth
	 * @param maxHeight
	 */
	public Thumbnail(BufferedImage image, double maxWidth, double maxHeight) {
		this(image, scale(image, maxWidth, maxHeight));
	}

	/**
	 * Forcibly resizes <code>image</code> to <code>size</code>.
	 * <b>Warning:</b> image may distort
	 * @param image
	 * @param size
	 */
	public Thumbnail(BufferedImage image, Dimension size) {
		this(size.width, size.height, image.getType());

		//draw it
		Graphics2D g2d = createGraphics();
		g2d.drawImage(image, 0, 0, size.width, size.height, null);
		g2d.dispose();
	}
	
	private static Dimension scale(BufferedImage image, double maxWidth,
			double maxHeight) {
		double widthScale = maxWidth / image.getWidth();
		double heightScale = maxHeight / image.getHeight();
		double scale = Math.min(widthScale, heightScale);

		int width = (int) (image.getWidth() * scale);
		int height = (int) (image.getHeight() * scale);
		return new Dimension(width, height);
	}

}