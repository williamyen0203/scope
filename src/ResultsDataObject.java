import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.opencv.core.Mat;

public class ResultsDataObject {
	private Mat mat;
	
	private double[][] points;
	
	private double stdErr;

	private double slope;
	
	private double intercept;
	
	public ResultsDataObject(Mat mat, double[][] points) {
		this.mat = mat;
		this.points = points;

		SimpleRegression sr = new SimpleRegression();
		sr.addData(points);
		this.stdErr = sr.getSlopeStdErr();
		this.intercept = sr.getIntercept();
		this.slope = sr.getSlope();
	}

	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}

	public double getStdErr() {
		return stdErr;
	}

	public void setStdErr(double stdErr) {
		this.stdErr = stdErr;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getIntercept() {
		return intercept;
	}

	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}
	
	public double[][] getPoints() {
		return points;
	}

	public void setPoints(double[][] points) {
		this.points = points;
	}
}
