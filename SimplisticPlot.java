import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimplisticPlot extends JPanel {
    private static class PlotHolder {
        Color color;
        double[] points;
        BiConsumer<Graphics2D, double[]> drawingFunction;

        PlotHolder(double[] points, Color color, BiConsumer<Graphics2D, double[]> drawingFunction) {
            this.points = points;
            this.color = color;
            this.drawingFunction = drawingFunction;
        }
    }

    private java.util.List<PlotHolder> plots = new ArrayList<>();
    private int maxPoints = 0;
    private double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
    private double verticalSpan = 0;

    public void addPlot(double[] aPoints, Color color, BiConsumer<Graphics2D, double[]> drawingFunction) {
        plots.add(new PlotHolder(aPoints, color, drawingFunction));
        maxPoints = Math.max(maxPoints, aPoints.length);
        DoubleSummaryStatistics stats = Arrays.stream(aPoints).summaryStatistics();
        minY = Math.min(stats.getMin(), minY);
        maxY = Math.max(stats.getMax(), maxY);
        verticalSpan = maxY - minY;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        int sx = this.getWidth(), sy = this.getHeight();
        int separation = sx / maxPoints;

        for (PlotHolder plot : plots) {
            g2.setColor(plot.color);
            g2.setStroke(new BasicStroke(3));
            plot.drawingFunction.accept(g2, plot.points);
        }
    }

    private double normalizePoint (double y) {
        return (y - minY)/verticalSpan;
    }

    public void drawLinePlot (Graphics2D g2, double[] points) {
        int sx = this.getWidth(), sy = this.getHeight();
        int separation = sx / maxPoints;


        for (int x = 1; x < points.length; x++) {
            double y1 = 1 - normalizePoint(points[x - 1]);
            double y2 = 1 - normalizePoint(points[x]);
            g2.drawLine((x - 1) * separation,
                    (int) (sy * y1),
                    x * separation,
                    (int) (sy * y2));
        }
    }

    public void drawPointPlot (Graphics2D g2, double[] points) {
        int sx = this.getWidth(), sy = this.getHeight();
        int separation = sx / maxPoints;
        for (int x = 0; x < points.length; x++) {
            double y = 1 - normalizePoint(points[x]);
            g2.drawOval((int)(x * separation) - 2,
                    (int) (sy * y) - 2,
                   4, 4);
        }
    }
}
