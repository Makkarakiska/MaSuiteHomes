package fi.matiaspaavilainen.masuitehomes;

import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Home {

    private Database db = MaSuiteHomes.db;
    private Connection connection = null;
    private PreparedStatement statement = null;
    private Configuration config = new Configuration();
    private String tablePrefix = config.load(null, "config.yml").getString("database.table-prefix");
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

    public Home() {
    }

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

    public Home set(Home home) {
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("INSERT INTO masuite_homes (name, server, owner, world, x, y, z, yaw, pitch) VALUES (?,?,?,?,?,?,?,?,?);");
            statement.setString(1, home.getName().toLowerCase());
            statement.setString(2, home.getServer());
            statement.setString(3, String.valueOf(home.getOwner()));
            statement.setString(4, home.getWorld());
            statement.setDouble(5, home.getX());
            statement.setDouble(6, home.getY());
            statement.setDouble(7, home.getZ());
            statement.setFloat(8, home.getYaw());
            statement.setFloat(9, home.getPitch());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return home;
    }

    public Home update(Home home) {
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("UPDATE masuite_homes SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE name = ? AND owner = ?;");
            statement.setString(1, home.getServer());
            statement.setString(2, home.getWorld());
            statement.setDouble(3, home.getX());
            statement.setDouble(4, home.getY());
            statement.setDouble(5, home.getZ());
            statement.setFloat(6, home.getYaw());
            statement.setFloat(7, home.getPitch());
            statement.setString(8, home.getName().toLowerCase());
            statement.setString(9, String.valueOf(home.getOwner()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return home;
    }

    public Home findExact(String name, UUID owner) {
        Home home = new Home();
        ResultSet rs = null;

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "homes WHERE name = ? AND owner = ? LIMIT 1;");
            statement.setString(1, name.toLowerCase());
            statement.setString(2, String.valueOf(owner));
            rs = statement.executeQuery();

            if (rs == null) {
                return new Home();
            }
            findHome(home, rs);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return home;
    }

    private void findHome(Home home, ResultSet rs) throws SQLException {
        while(rs.next()){
            home.setId(rs.getInt("id"));
            home.setName(rs.getString("name"));
            home.setServer(rs.getString("server"));
            home.setOwner(UUID.fromString(rs.getString("owner")));
            home.setWorld(rs.getString("world"));
            home.setX(rs.getDouble("x"));
            home.setY(rs.getDouble("y"));
            home.setZ(rs.getDouble("z"));
            home.setYaw(rs.getFloat("yaw"));
            home.setPitch(rs.getFloat("pitch"));
        }
    }

    public Home findLike(String name, UUID owner) {
        Home home = new Home();
        ResultSet rs = null;

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "homes WHERE name LIKE ? ESCAPE '!' AND owner = ? LIMIT 1;");
            statement.setString(1, name.toLowerCase() + "%");
            statement.setString(2, String.valueOf(owner));
            rs = statement.executeQuery();

            if (rs == null) {
                return new Home();
            }
            findHome(home, rs);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return home;
    }
    public Set<Home> homes(UUID owner) {
        HashSet<Home> homes = new HashSet<>();
        ResultSet rs = null;
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "homes WHERE owner = ?;");
            statement.setString(1, String.valueOf(owner));
            rs = statement.executeQuery();
            while (rs.next()) {
                Home home = new Home();
                home.setId(rs.getInt("id"));
                home.setName(rs.getString("name").toLowerCase());
                home.setServer(rs.getString("server"));
                home.setOwner(UUID.fromString(rs.getString("owner")));
                home.setWorld(rs.getString("world"));
                home.setX(rs.getDouble("x"));
                home.setY(rs.getDouble("y"));
                home.setZ(rs.getDouble("z"));
                home.setYaw(rs.getFloat("yaw"));
                home.setPitch(rs.getFloat("pitch"));
                homes.add(home);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return homes;
    }

    public Boolean delete(Home home) {
        ResultSet rs = null;

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("DELETE FROM " + tablePrefix + "homes WHERE name = ? AND owner = ?");
            statement.setString(1, home.getName().toLowerCase());
            statement.setString(2, String.valueOf(home.getOwner()));
            statement.execute();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
