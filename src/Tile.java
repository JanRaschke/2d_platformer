public class Tile {
    public Vec2 pos;
    public int tileIndex;
    public BoundingBox boundingBox;

    public Tile(float x, float y, int tileIndex, int tileSize) {
        this.pos = new Vec2(x, y);
        this.tileIndex = tileIndex;
        this.boundingBox = new BoundingBox(x, y, x + tileSize, y + tileSize);
    }
}