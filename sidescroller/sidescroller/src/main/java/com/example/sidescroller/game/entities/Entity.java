package com.example.sidescroller.game.entities;

import com.example.sidescroller.game.Screen;
import com.example.sidescroller.game.graphics.Sprite;
import com.example.sidescroller.game.level.Level;
import com.example.sidescroller.game.level.Tile;

import java.util.LinkedList;

/**
 * Created by Owner on 18/11/13.
 */
public class Entity {
    protected int x, y;
    protected Level  level;
    protected Sprite sprite;
    private   static Screen screen;
    public static LinkedList<Entity> entities;

    public void setLevel(Level level) { this.level = level; }
    public static void setScreen(Screen displayScreen) { screen = displayScreen; }

    protected boolean collision(int xa, int ya) {
        int tileX = (x + xa) / Tile.TILE_SIZE;
        int tileY = (y + ya) / Tile.TILE_SIZE;
        if (level.getTile(tileX, tileY).isSolid())
            return true;
        return false;
    }

    public void move() {}
    public void draw() { screen.draw(x, y, sprite); }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}