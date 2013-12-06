package com.example.sidescroller.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.sidescroller.game.buttons.ButtonSprites;
import com.example.sidescroller.game.buttons.JumpButton;
import com.example.sidescroller.game.entities.Entity;
import com.example.sidescroller.game.entities.enemies.FishEnemy;
import com.example.sidescroller.game.entities.enemies.SnailEnemy;
import com.example.sidescroller.game.entities.peripherals.Bomb;
import com.example.sidescroller.game.entities.player.Frank;
import com.example.sidescroller.game.graphics.SpriteSheet;
import com.example.sidescroller.game.level.Level;
import com.example.sidescroller.game.level.Tile;
import com.example.sidescroller.game.sound.Sounds;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by soote on 11/23/13.
 */
public class Surface extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final Bitmap.Config    IMAGE_FORMAT = Bitmap.Config.ARGB_8888;
    private              int              xScroll;

    private        int[] pixels;
    private static int   GAME_WIDTH;
    private static int   GAME_HEIGHT;
    private static int   LEVEL_ID;

    private Screen      screen;
    private Level       level;
    private Frank       frank;
    private SnailEnemy  snailEnemy;
    private FishEnemy   fishEnemy;
    private GameLoop    thread;
    private JumpButton  jumpButton;
    private Sounds      pool;


    public static void setDimensions(int width, int height) {
        GAME_WIDTH = width;
        GAME_HEIGHT = height;
    }
    public static void setLevel(int id) { LEVEL_ID = id; }

    public Surface(Context c) {
        super(c);

        SpriteSheet.setView(this);
        Level.setView(this);

        jumpButton = new JumpButton(GAME_WIDTH, GAME_HEIGHT, ButtonSprites.jumpButton);
        screen = new Screen(GAME_WIDTH, GAME_HEIGHT);
        level = new Level(LEVEL_ID);

        Entity.entities = new LinkedList<Entity>();
        Entity.setScreen(screen);
        Bomb.bombs = new ConcurrentLinkedQueue<Bomb>();

        xScroll = -Tile.TILE_SIZE;
        //game height is 720. that is not a multiple of 64 so we have to set franks y to be a
        // multiple
        frank = new Frank(GAME_WIDTH, 128);
        snailEnemy = new SnailEnemy(GAME_WIDTH, 128);
        fishEnemy = new FishEnemy(GAME_WIDTH, 128);

        for (Entity e : Entity.entities) {
            e.setLevel(level);
        }

        pool = new Sounds(c);

        //Set thread
        getHolder().addCallback(this);
        setFocusable(true);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameLoop(holder, this);
        // at this point the surface is created and
        // we can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // tell the thread to shut down and wait for it to finish
        // this is a clean shutdown
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (jumpButton.wasClicked(x, y)) {
                if (!frank.isJumping() && !frank.isFalling()) {
                    frank.setJumping(true);
                    pool.play(1, false);
                }
            } else {
                Bomb bomb = new Bomb();
                bomb.setLevel(level);
                bomb.setShooting(true, frank.getX(), frank.getY(), event.getX(), event.getY());
                synchronized (Bomb.bombs) {
                    Bomb.bombs.add(bomb);
                }
                pool.play(2, false);
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        Log.i("MyActivity", "MyClass.getView() — get item number " + 22);

        xScroll += Tile.TILE_SIZE; //scroll map to the left
        level.draw(xScroll, 0, screen);
        jumpButton.draw(screen);

        //move and draw all our entities
        for (Entity e : Entity.entities) e.move();
        //this for loop allows us to shoot more then 1 bomb at 1 time.. we have to render each one
        for (Bomb b : Bomb.bombs) b.shoot(screen);

        pixels = new int[GAME_WIDTH * GAME_HEIGHT];
        System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);

        Bitmap bmp = newBitmap();
        fillBitmap(bmp);
        c.drawBitmap(bmp, 0, 0, null);
    }

    private Bitmap newBitmap() {
        return Bitmap.createBitmap(GAME_WIDTH, GAME_HEIGHT,
                IMAGE_FORMAT);
    }
    private void fillBitmap(Bitmap bmp) {
        bmp.setPixels(pixels, 0, GAME_WIDTH, 0, 0, GAME_WIDTH,
                GAME_HEIGHT);
    }
}
