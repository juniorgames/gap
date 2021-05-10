package com.juniorgames.gap.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.juniorgames.gap.GapGame;

import java.util.HashMap;

public class SpikeEnemy extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING_RIGHT, RUNNING_LEFT, DIEING}

    public State prevState, currState;
    public Body body;
    private Filter filter;
    private Fixture fixture;
    private HashMap<String, Animation> animations;
    private float stateTimer;
    public boolean runRight;
    private GapGame game;
    private AssetManager manager;
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    private Array<TextureRegion> frames;
    private TextureRegion region;
    private PolygonShape shape;

    public SpikeEnemy(GapGame game, float enemyX, float enemyY) {
        super(game.spikeEnemyAtlas.findRegion("senemy"));
        //defineVars
        this.game = game;
        this.manager = game.manager;
        filter = new Filter();
        currState = State.STANDING;
        prevState = State.STANDING;
        stateTimer = 0;
        runRight = true;
        region = new TextureRegion();
        animations = new HashMap<>();
        bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        shape = new PolygonShape();
        //
        defineAnimation();
        defineSpikeEnemy(enemyX, enemyY);
        setRegAndBounds();
    }//constructor

    public void jump() {
        body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        if (!game.soundsMuted) {
            game.playSound(game.jumpSound);
        }
    }//jump

    public void runRight() {
        body.applyLinearImpulse(new Vector2(0.05f, 0), body.getWorldCenter(), true);
    }//moveRight

    public void runLeft() {
        body.applyLinearImpulse(new Vector2(-0.05f, 0), body.getWorldCenter(), true);
    }//moveLeft

    public void run(){
        if (runRight) {
            runRight();
        }else{
            runLeft();
        }
    }//run

    public void update(float dt) {
        setRegAndPos(dt);
        //=================MOVING===============================
        run();
        //=======================DIE============================
        if (getFilterBit() == game.DESTROYED_BIT) {
            filter.maskBits = game.DEFAULT_BIT;
            fixture.setFilterData(filter);
            if (body.getPosition().y * game.GAME_PPM > game.GAME_HEIGHT) {
                game.savedGame.killed++;
                game.savedGame.save();
                game.tasksTracker.update(game.savedGame);
                game.world.destroyBody(body);
            } else {// else flying up the screen
                body.setActive(false);
                body.setTransform(body.getPosition().x, body.getPosition().y + dt * 2, 0);
            }//else
        }//if
        else {// if not dieing
            //=======================WRAP===========================
            if (body.getPosition().x * game.GAME_PPM < 0) {
                body.setTransform((body.getPosition().x * game.GAME_PPM + game.GAME_WIDTH) / game.GAME_PPM, body.getPosition().y, 0);
                game.savedGame.wrapped++;
                game.playSound(game.warpSound);
                game.tasksTracker.update(game.savedGame);
            }//if -x
            if (body.getPosition().x * game.GAME_PPM > game.GAME_WIDTH) {
                body.setTransform((body.getPosition().x * game.GAME_PPM - game.GAME_WIDTH) / game.GAME_PPM, body.getPosition().y, 0);
                game.savedGame.wrapped++;
                game.playSound(game.warpSound);
                game.tasksTracker.update(game.savedGame);
            }//if +x
            if (body.getPosition().y * game.GAME_PPM < 0) {
                body.setTransform(body.getPosition().x, (body.getPosition().y * game.GAME_PPM + game.GAME_HEIGHT) / game.GAME_PPM, 0);
                game.savedGame.wrapped++;
                game.playSound(game.warpSound);
                game.tasksTracker.update(game.savedGame);
            }//if -y
            if (body.getPosition().y * game.GAME_PPM > game.GAME_HEIGHT) {
                body.setTransform(body.getPosition().x, (body.getPosition().y * game.GAME_PPM - game.GAME_HEIGHT) / game.GAME_PPM, 0);
                game.savedGame.wrapped++;
                game.playSound(game.warpSound);
                game.tasksTracker.update(game.savedGame);
            }//if +y
        }//else
    }//update

    private void setRegAndPos(float dt) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }//setRegAndPos

    public TextureRegion getFrame(float dt) {
        currState = getState();
        switch (currState) {
            case JUMPING:
                region = (TextureRegion) animations.get("enemyJump").getKeyFrame(stateTimer);
                break;
            case RUNNING_RIGHT:
                region = (TextureRegion) animations.get("enemyRun").getKeyFrame(stateTimer, true);
                break;
            case RUNNING_LEFT:
                region = (TextureRegion) animations.get("enemyRun").getKeyFrame(stateTimer, true);
                break;
            case FALLING:
                region = (TextureRegion) animations.get("enemyFall").getKeyFrame(stateTimer);
                break;
            case DIEING:
                region = (TextureRegion) animations.get("enemyDie").getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = (TextureRegion) animations.get("enemyIdle").getKeyFrame(stateTimer, true);
                break;
        }//switch
        updateDirection();
        stateTimer = currState == prevState ? stateTimer + dt : 0;
        prevState = currState;
        return region;
    }//getFrame

    public void setFilterBit(short bit) {
        filter.categoryBits = bit;
        fixture.setFilterData(filter);
    }//setFilterBit

    public short getFilterBit() {
        return fixture.getFilterData().categoryBits;
    }

    private State getState() {
        if (getFilterBit() == game.DESTROYED_BIT)
            return State.DIEING;
        if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y > 0 && prevState == State.JUMPING))
            return State.JUMPING;
        else if (body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (body.getLinearVelocity().x > 0)
            return State.RUNNING_RIGHT;
        else if (body.getLinearVelocity().x < 0)
            return State.RUNNING_LEFT;
        else return State.STANDING;
    }//getState

    private void setRegAndBounds() {
        setBounds(0, 0, 64 / game.GAME_PPM, 64 / game.GAME_PPM);
        setRegion((TextureRegion) animations.get("enemyIdle").getKeyFrame(stateTimer, true));
    }//setRegAndBounds

    private void defineSpikeEnemy(float x, float y) {
        this.bodyDef.position.set(x / game.GAME_PPM, y / game.GAME_PPM);
        this.bodyDef.type = BodyDef.BodyType.DynamicBody;
        this.body = game.world.createBody(bodyDef);
        shape.setAsBox(20 / game.GAME_PPM, 28 / game.GAME_PPM);
        fixtureDef.filter.maskBits = (short) (game.GROUND_BIT | game.DEFAULT_BIT | game.PLAYER_BIT);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        setFilterBit(game.ENEMY_BIT);
        fixture.setUserData(this);
    }//defineEnemy

    private void defineAnimation() {
        frames = new Array<>();
        //run
        for (int i = 0; i < 13; i++) {
            frames.add(new TextureRegion(getTexture(), i * 64, 0, 64, 64));
        }
        animations.put("enemyRun", new Animation(0.05f, frames));
        frames.clear();
        //jump
        frames.add(new TextureRegion(getTexture(), 14 * 64, 0, 64, 64));
        animations.put("enemyJump", new Animation(0.1f, frames));
        frames.clear();
        //fall
        frames.add(new TextureRegion(getTexture(), 13 * 64, 0, 64, 64));
        animations.put("enemyFall", new Animation(0.1f, frames));
        frames.clear();
        //idle
        for (int i = 0; i < 13; i++) {
            frames.add(new TextureRegion(getTexture(), i * 64, 64, 64, 64));
        }
        animations.put("enemyIdle", new Animation(0.1f, frames));
        frames.clear();
        //die
        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(getTexture(), i * 64, 128, 64, 64));
        }
        animations.put("enemyDie", new Animation(0.1f, frames));
        frames.clear();
    }//defineAnimation

    private void updateDirection() {
        if ((body.getLinearVelocity().x < 0 || !runRight) && !region.isFlipX()) {
            region.flip(true, false);
            runRight = false;
        } else if ((body.getLinearVelocity().x > 0 || runRight) && region.isFlipX()) {
            region.flip(true, false);
            runRight = true;
        }//if
    }//updateDirection
}
