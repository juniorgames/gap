package com.juniorgames.gap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.juniorgames.gap.GapGame;
import com.juniorgames.gap.scenes.PlayMenuHUD;

import static com.juniorgames.gap.GapGame.GAME_PPM;

public class PlayMenuScreen extends ScreenAdapter {
    private GapGame game;
    private AssetManager manager;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayMenuHUD playMenuHud;
    //tiled map values
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //sfx
    private Music music;

    public PlayMenuScreen(GapGame game, AssetManager manager) {
        this.game = game;
        this.manager = manager;
        batch = new SpriteBatch();
        initScreen();
        initScreenSFX();
    }//constructor

    private void initScreenSFX() {
        music = manager.get("audio/music/world1-music.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.2f);//0-1 range
        if (!game.musicMuted) {
            music.play();
        }//end if
    }

    private void initScreen() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(this.game.GAME_WIDTH / GAME_PPM, this.game.GAME_HEIGHT / GAME_PPM, camera);

        playMenuHud = new PlayMenuHUD(this.game, this.manager);

        maploader = new TmxMapLoader();
        map = maploader.load("level0-0.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / GAME_PPM);//scaling map with PPM

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }

    public void update(float dt) {
        handleInput(dt);
        playMenuHud.update(dt);
        camera.update();
        renderer.setView(camera);
    }

    private void handleInput(float dt) {
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render screen map
        renderer.render();
        //render HUD
        batch.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(playMenuHud.stage.getCamera().combined);
        playMenuHud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        Gdx.app.log("PlayMenuScreen", "Resizing screen to: " + width + " x " + height);

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        music.dispose();
        playMenuHud.dispose();
        manager.dispose();
    }
}
