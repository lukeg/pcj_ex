import javax.swing.*;
import java.awt.*;

public class SmoothingSequential {
    public static void main (String[] args) {
        final int N = 50;
        final double[] points = new double[N];
        final double[] smoothed = new double[N];
        SimplisticPlot plot = new SimplisticPlot();

        for (int i = 0; i < N; i++) {
            points[i] = Math.cos(i * Math.PI / 2 / N) + Math.random() / 2;
        }
        plot.addPlot(points, Color.BLACK, plot::drawLinePlot);

        double norm = -3 + 12 + 17 + 12 - 3;

        smoothed[0] = 17*points[0] + 12*points[1] - 3 * points[2];
        smoothed[0] /= (17 + 12 - 3);

        smoothed[1] = 12 * points[0] + 17 * points[1] + 12 * points[2] - 3 * points[3];
        smoothed[1] /= (12 + 17 + 12 - 3);

        for (int i = 2; i < N - 2; i++) {
            smoothed[i] = -3 * points[i - 2] + 12 * points[i - 1] + 17 * points[i] + 12 * points[i + 1] - 3 * points[i + 2];
            smoothed[i] /= norm;
        }

        smoothed[N - 2] = -3 * points[ N - 2 - 2] + 12 * points[N - 2 - 1] + 17 * points[N - 2] + 12 * points[N - 2 + 1];
        smoothed[N - 2] /= (-3 + 12 + 17 + 12);

        smoothed[N - 1] = -3 * points[N - 1 - 2] + 12 * points [N - 1 - 1] + 17 * points[N - 1];
        smoothed[N - 1] /= (-3 + 12 + 17);
        plot.addPlot(smoothed, Color.RED, plot::drawPointPlot);
        show(plot);
    }

    private static void show(SimplisticPlot plot) {

        JFrame frame = new JFrame("Plot");
        frame.add(plot);
        frame.setSize(new Dimension(800, 600));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
