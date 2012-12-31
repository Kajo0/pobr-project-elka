package pl.edu.pw.elka.mmarkiew.model;

public class Pixel implements Comparable<Pixel> {

	public int r;
	public int g;
	public int b;

	public Pixel(final int intPixel) {
		this.r = ((intPixel >> 16) & 0xFF);
		this.g = ((intPixel >> 8) & 0xFF);
		this.b = ((intPixel >> 0) & 0xFF);
	}

	public Pixel(final int r, final int g, final int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Pixel(final Pixel pixel) {
		this.r = pixel.r;
		this.g = pixel.g;
		this.b = pixel.b;
	}

	public int toIntPixel() {
		return (0xFF << 24 | (int) r << 16 | (int) g << 8 | (int) b << 0);
	}
	
	public int grayValue() {
		return (r + g + b ) / 3;
	}
	
	/**
	 * Only for binary pixels! (x>0 or 0 per each color)
	 * @return
	 */
	public boolean isWhite() {
		return (r > 0 || g > 0 || b > 0) ? true : false;
	}

	@Override
	public int compareTo(Pixel other) {
		return (this.grayValue() < other.grayValue() ? -1 : this.grayValue() == other.grayValue() ? 0 : 1);
	}

}
