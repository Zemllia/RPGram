package rpgram.mapobjects;

import com.crown.maps.Point3D;

public class LargeObjectTemplates {
    /**
     * Example:
     * <pre>
     * {@code
     * pt0
     * |--------------------|
     * v                    v
     * /---\      ->      |
     * | + |      ->       |
     * |___|      ->        |
     * _______________________________
     * x*y        ->      z projection
     * projection
     * }
     * </pre>
     */
    public static Point3D[] getSquareLinearZTemplate(Point3D pt0, int size) {
        assert size > 1;

        var points = new Point3D[size * size];
        int idx = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                points[idx++] = new Point3D(
                    pt0.x + x,
                    pt0.y + y,
                    pt0.z + size - y - 1
                );
            }
        }
        return points;
    }
}
