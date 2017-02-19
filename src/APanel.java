import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class APanel extends JPanel {
	int WIDTH = JavaCV1.WIDTH;
	int HEIGHT = JavaCV1.HEIGHT;
	int i = 0;

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	protected void drawPoints(double[][] points) {
		Graphics g = this.getGraphics();

		g.setColor(new Color(0, 0, 0, 64));
		for (int i = 0; i < points.length; i++) {
			g.fillOval((int) points[i][0], (int) points[i][1], 10, 10);
		}
	}

	protected void drawLines(double m, double b) {
		Graphics g = this.getGraphics();

		g.setColor(new Color(0, 0, 0, 64));
		
		int t = (int) (-b / m);
		int d = (int) ((HEIGHT - b) / m);
		int l = (int) (b); 
		int r = (int) (m * WIDTH + b);

		ArrayList<int[]> ps = new ArrayList<>();
		
		if (m == 0) {
			// horizontal line
			g.drawLine(0, (int) b, WIDTH, (int) b);
		} else if (m == Double.NaN) {
			// vertical line
			g.drawLine(1, 0, 1, HEIGHT);
		} else {			
			if (t >= 0 && t <= WIDTH) {
				// touches top
				int[] p = {(int) (-b / m), 0};
				ps.add(p);
			}
			if (d >= 0 && d <= WIDTH) {
				// touches bottom
				int[] p = {(int) ((HEIGHT - b) / m), HEIGHT};
				ps.add(p);
			}
			if (l >= 0 && l <= HEIGHT) {
				int[] p = {0, (int) b};
				ps.add(p);			
			}
			if (r >= 0 && r <= HEIGHT) {
				int[] p = {WIDTH, (int) (m * WIDTH + b)};
				ps.add(p);
			}
			
			g.drawLine(ps.get(0)[0], ps.get(0)[1], ps.get(1)[0], ps.get(1)[1]);
		}
	}

	@Override
	public void repaint() {
		if (i++ % 5 == 0) {
			super.repaint();
		}
	}
}
