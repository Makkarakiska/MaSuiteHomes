package fi.matiaspaavilainen.masuitehomes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Home {

    private int id;
    private String name;
    private String server;
    private UUID owner;
    //Location
    private String world;
    private Double x;
    private Double y;
    private Double z;
    private Float yaw;
    private Float pitch;

    public Home() { }

    public Home(String name, String server, UUID owner, String world, Double x, Double y, Double z, Float yaw, Float pitch) {
        this.name = name;
        this.server = server;
        this.owner = owner;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Home set(Home home){
        // INSERT INTO masuite_homes (name, server, owner, world, x, y, z, yaw, pitch) VALUES (?,?,?,?,?,?,?,?,?)
        return home;
    }
    public Home update(Home home){
        // UPDATE masuite_homes SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE name = ? AND owner = ?
        return home;
    }
    public Home find(String name){
        Home home = new Home();
        // SELECT * FROM masuite_homes WHERE name = ? AND owner = ?
        return home;
    }
    public Set<Home> homes(UUID owner){
        HashSet<Home> homes = new HashSet<>();
        homes.add(new Home());
        homes.add(new Home());
        homes.add(new Home());
        // SELECT * FROM masuite_homes WHERE AND owner = ?
        return homes;
    }

    public Boolean delete(String name){
        // DELETE FROM masuite_homes WHERE name = ? AND owner ?
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
    }

    public Float getPitch() {
        return pitch;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }


}
