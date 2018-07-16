import org.pcj.*;

import java.util.Collections;

@RegisterStorage(Smoothing.Shared.class)
public class Smoothing implements StartPoint {

    @Storage(Smoothing.class)
    enum Shared {
        points
    }
    double[] points;
    double[] smoothed;

    public static void main (String[] args) {
        PCJ.deploy(Smoothing.class, new NodesDescription(Collections.nCopies(4, "localhost").toArray(new String[0])));
    }

    @Override
    public void main() throws Throwable {
        int N = 56;
        int localN = N / PCJ.threadCount();

        points = new double[localN];
        smoothed = new double[localN];
        for (int i = 0; i < points.length; i++) {
            points[i] = Math.random();
        }
        double l0 = 0, l1 = 0, r0 = 0, r1 = 0;
        PCJ.barrier();
        if (PCJ.myId() > 0) {
            l0 = PCJ.get(PCJ.myId() - 1, Shared.points, localN - 2);
            l1 = PCJ.get (PCJ.myId() - 1, Shared.points, localN - 1);
        }
        if (PCJ.myId() < PCJ.threadCount() - 1) {
            r0 = PCJ.get(PCJ.myId() + 1, Shared.points, 0);
            r1 = PCJ.get(PCJ.myId() + 1, Shared.points, 1);
        }

        double norm = -3 + 12 + 17 + 12 - 3;

        for (int i = 2; i < localN - 2; i++) {
            smoothed[i] = -3 * points[i - 2] + 12 * points[i - 1] + 17 * points[i] + 12 * points[i + 1] - 3 * points[i + 2];
            smoothed[i] /= norm;
        }

        if (PCJ.myId() == 0) {
            smoothed[0] = 17 * points[0] + 12 * points[1] - 3 * points[2];
            smoothed[0] /= (17 + 12 - 3);

            smoothed[1] = 12 * points[0] + 17 * points[1] + 12 * points[2] - 3 * points[3];
            smoothed[1] /= (12 + 17 + 12 - 3);
        } else {
            smoothed[0] = -3 * l0 + 12 * l1 + 17 * points[0] + 12 * points[1] - 3 * points[2];
            smoothed[0] /= norm;

            smoothed[1] = -3 * l1 + 12 * points[0] + 17 * points[0] + 12 * points[1] - 3 * points[2];
            smoothed[0] /= norm;
        }


        if (PCJ.myId() == PCJ.threadCount() - 1) {
            smoothed[localN - 2] = -3 * points[localN - 2 - 2] + 12 * points[localN - 2 - 1] + 17 * points[localN - 2] + 12 * points[localN - 2 + 1];
            smoothed[localN - 2] /= (-3 + 12 + 17 + 12);

            smoothed[localN - 1] = -3 * points[localN - 1 - 2] + 12 * points[localN - 1 - 1] + 17 * points[localN - 1];
            smoothed[localN - 1] /= (-3 + 12 + 17);
        } else {
            smoothed[localN - 2] = -3 * points[localN - 4] + 12 * points[localN - 3] + 17 * points[localN - 2] + 12 * points[localN - 1] - 3 * r0;
            smoothed[localN - 2] /= norm;

            smoothed[localN - 1] = -3 * points[localN - 3] + 12 * points[localN - 2] + 17 * points[localN - 1] + 12 * r0 - 3 * r1;
            smoothed[localN - 1] /= norm;
        }
        PCJ.barrier();

        for (int j = 0; j < PCJ.threadCount(); j++) {
            if (PCJ.myId() == j) {
                System.out.format("[%d]: ", PCJ.myId());
                for (int i = 0; i < smoothed.length; i++) {
                    System.out.println(smoothed[i] + ", ");
                }
                System.out.println();
            }
            PCJ.barrier();
        }
    }

}
