package geogebra.web.euclidian;



public class BaseEuclidianView {	

	
	
/*	protected BaseEuclidianView() {
		this(Canvas.createIfSupported());
	}
	
	protected BaseEuclidianView(Canvas canvas) {
		this.g2 = new Graphics2D(canvas);
	}*/

		
	
	// Canvas Drawing and Context Methods
	/*
	protected void fillRect(double x,double y,double width,double height) {
		g2.fillRect(x, y, width, height);
	}
	
	protected void setLineWidth(double lineWidth) {
		context.setLineWidth(lineWidth);
	}
	
	protected void setLineCap(String lineCap) {
		context.setLineCap(lineCap);
	}
	
	protected void setLineJoin(String lineJoin) {
		context.setLineJoin(lineJoin);
	}
	
	protected void fillText(String text, double x, double y, String cssFont) {
		String oldFont = context.getFont();
		context.setFont(cssFont);
		context.fillText(text, x, y);
		context.setFont(oldFont);
	}

	protected double measureText(String text, String cssFont) {
		String oldFont = context.getFont();
		context.setFont(cssFont);
		TextMetrics measure = context.measureText(text);
		context.setFont(oldFont);
		return measure.getWidth();
	}
	
	protected void clear() {
		context.clearRect(0, 0, getWidth(), getHeight());
	}
	
	protected void rect(double x, double y, double width, double height) {
		context.rect(x, y, width, height);
	}
	
	protected void drawImage(ImageElement image,double x, double y) {
		context.drawImage(image, x, y);
	}
	
	protected void setFillStyle(String fillSyleColor) {
		context.setFillStyle(fillSyleColor);
	}
	
	protected void setFillStyle(CssColor color) {
		setFillStyle(color.toString());
	}
	
	protected void setStrokeStyle(String strokeStyleColor) {
		context.setStrokeStyle(strokeStyleColor);
	}
	
	protected void beginPath() {
		context.beginPath();
	}
	
	protected void moveTo(double x,double y) {
		context.moveTo(x, y);
	}
	
	protected void cubicCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
		context.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
	}
	
	protected void closePath() {
		context.closePath();
	}
	
	protected void stroke() {
		context.stroke();
	}
	
	protected void fill() {
		context.fill();
	}
	
	protected void quadraticCurveTo(double cpx, double cpy, double x, double y) {
		context.quadraticCurveTo(cpx, cpy, x, y);
	}
	
	protected void lineTo(double x, double y) {
		context.lineTo(x, y);
	}
	
	protected void saveContext() {
		context.save();
	}
	
	protected void setGlobalAlpha(double alpha) {
		context.setGlobalAlpha(alpha);
	}

	protected void transform(double m11, double m12, double m21, double m22, double dx, double dy) {
		context.transform(m11, m12, m21, m22, dx, dy);
	}
	
	protected void restoreContext() {
		context.restore();
	}*/
	
}
