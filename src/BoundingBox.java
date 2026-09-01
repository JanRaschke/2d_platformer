public class BoundingBox {
    public Vec2 min;
    public Vec2 max;

    public BoundingBox(float minX, float minY, float maxX, float maxY) {
        this.min = new Vec2(minX, minY);
        this.max = new Vec2(maxX, maxY);
    }

    public void set(float minX, float minY, float maxX, float maxY) {
        this.min.x = minX;
        this.min.y = minY;
        this.max.x = maxX;
        this.max.y = maxY;
    }

    public boolean intersect(BoundingBox b) {
        return (min.x <= b.max.x) &&
                (max.x >= b.min.x) &&
                (min.y <= b.max.y) &&
                (max.y >= b.min.y);
    }

    public Vec2 overlapSize(BoundingBox b) {
        Vec2 result = new Vec2(0, 0);

        if (min.x < b.min.x)
            result.x = max.x - b.min.x;
        else
            result.x = b.max.x - min.x;

        if (min.y < b.min.y)
            result.y = max.y - b.min.y;
        else
            result.y = b.max.y - min.y;

        return result;
    }
}