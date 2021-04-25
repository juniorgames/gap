package com.juniorgames.gap.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class LevelData {
    public int world = 0;// world number
    public int level = 0;// level number
    public Vector2 start = new Vector2();// coords where player appears at start
    public Vector2 exit = new Vector2();// coords of exit doors
    public Array<Vector2> plant1s = new Array<>();// array of plants version 1 coords to put on current map
    public Array<Vector2> plant2s = new Array<>();// array of plants version 2 coords to put on current map
    public Array<Vector2> switches = new Array<>();// array of switches coords to put on current map
    public Array<Vector2> enemies = new Array<>();
    public Array<Vector2> spikeEnemies = new Array<>();
    public Array<Vector2> bumps = new Array<>();
    public Array<Vector2> buffBombs = new Array<>();
    public Array<Vector2> buffJumps = new Array<>();
    public Array<Vector2> buffShields = new Array<>();
    public Vector2 tutorial = new Vector2();// position of tutorial image IF IT EXIST FOR CURRENT LEVEL!

    public void loadLevel(int world, int level) {
        this.world = world;
        this.level = level;
        JsonReader reader = new JsonReader();
        JsonValue value;
        JsonValue json = reader.parse(Gdx.files.internal("level" + world + "-" + level + ".json"));

        //int
        this.world = json.getInt("world");
        this.level = json.getInt("level");

        //Vector2
        value = json.getChild("start");
        this.start.x = value.asFloat();
        this.start.y = value.next.asFloat();
        value = json.getChild("exit");
        this.exit.x = value.asFloat();
        this.exit.y = value.next.asFloat();
        value = json.getChild("tutorial");
        this.tutorial.x = value.asFloat();
        this.tutorial.y = value.next.asFloat();

        //Array<Vector2> plant1s
        value = json.getChild("plant1s");
        while (value != null) {
            this.plant1s.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> plant2s
        value = json.getChild("plant2s");
        while (value != null) {
            this.plant2s.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> switches
        value = json.getChild("switches");
        while (value != null) {
            this.switches.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> enemies
        value = json.getChild("enemies");
        while (value != null) {
            this.enemies.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> spikeEnemies
        value = json.getChild("spikeEnemies");
        while (value != null) {
            this.spikeEnemies.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> bumps
        value = json.getChild("bumps");
        while (value != null) {
            this.bumps.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> buffBombs
        value = json.getChild("buffBombs");
        while (value != null) {
            this.buffBombs.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> buffJumps
        value = json.getChild("buffJumps");
        while (value != null) {
            this.buffJumps.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while

        //Array<Vector2> buffShields
        value = json.getChild("buffShields");
        while (value != null) {
            this.buffShields.add(new Vector2(value.get("x").asFloat(), value.get("y").asFloat()));
            value = value.next;
        }//end while
    }//loadLevel

}
