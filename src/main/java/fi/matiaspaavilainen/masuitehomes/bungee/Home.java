package fi.matiaspaavilainen.masuitehomes.bungee;

import fi.matiaspaavilainen.masuitecore.core.database.ConnectionManager;
import fi.matiaspaavilainen.masuitecore.core.database.Database;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Home {

    private Database db = ConnectionManager.db;
    private Connection connection = null;
    private PreparedStatement statement = null;
    private String tablePrefix = db.getTablePrefix();
    private int id;
    private String name;
    private String server;
    private UUID owner;
    private Location location;

    /**
     * An empty constructor for MaSuiteHomes
     */
    public Home() {
    }

    /**
     * Constructor for MaSuiteHomes
     * @param name name of the home
     * @param server server of the home
     * @param owner owner of the home
     * @param loc location of the home
     */
    public Home(String name, String server, UUID owner, Location loc) {
        this.name = name;
        this.server = server;
        this.owner = owner;
        this.location = loc;
    }

    /**
     * Create home
     * @return created home
     */
    public Home create() {
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("INSERT INTO " + tablePrefix + "homes (name, server, owner, world, x, y, z, yaw, pitch) VALUES (?,?,?,?,?,?,?,?,?);");
            statement.setString(1, this.name.toLowerCase());
            statement.setString(2, this.server);
            statement.setString(3, this.owner.toString());
            statement.setString(4, this.location.getWorld());
            statement.setDouble(5, this.location.getX());
            statement.setDouble(6, this.location.getY());
            statement.setDouble(7, this.location.getZ());
            statement.setFloat(8, this.location.getYaw());
            statement.setFloat(9, this.location.getPitch());
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
        return this;
    }

    /**
     * Update home point
     * @return updated home point
     */
    public Home update() {
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("UPDATE " + tablePrefix + "homes SET server = ?, world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE name = ? AND owner = ?;");
            statement.setString(1, this.server);
            statement.setString(2, this.location.getWorld());
            statement.setDouble(3, this.location.getX());
            statement.setDouble(4, this.location.getY());
            statement.setDouble(5, this.location.getZ());
            statement.setFloat(6, this.location.getYaw());
            statement.setFloat(7, this.location.getPitch());
            statement.setString(8, this.name.toLowerCase());
            statement.setString(9, this.owner.toString());
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
        return this;
    }

    /**
     * Find home by exactly name and the owner's uuid
     * @param name name of the home point
     * @param owner uuid of the owner
     * @return result of the query
     */
    public Home findExact(String name, UUID owner) {
        Home home = new Home();
        ResultSet rs = null;

        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "homes WHERE name = ? AND owner = ? LIMIT 1;");
            statement.setString(1, name.toLowerCase());
            statement.setString(2, owner.toString());
            rs = statement.executeQuery();


            boolean empty = true;
            while (rs.next()) {
                home.setId(rs.getInt("id"));
                home.setName(rs.getString("name"));
                home.setServer(rs.getString("server"));
                home.setOwner(UUID.fromString(rs.getString("owner")));
                home.setLocation(new Location(rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
                empty = false;
            }
            if (empty) {
                return null;
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
        return home;
    }

    /**
     * Find home by like name and the owner's uuid
     * @param name name of the home point
     * @param owner uuid of the owner
     * @return result of the query
     */
    public Home findLike(String name, UUID owner) {
        Home home = new Home();
        ResultSet rs = null;
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "homes WHERE name LIKE ? ESCAPE '!' AND owner = ? LIMIT 1;");
            statement.setString(1, name.toLowerCase() + "%");
            statement.setString(2, owner.toString());
            rs = statement.executeQuery();

            boolean empty = true;
            while (rs.next()) {
                home.setId(rs.getInt("id"));
                home.setName(rs.getString("name"));
                home.setServer(rs.getString("server"));
                home.setOwner(UUID.fromString(rs.getString("owner")));
                home.setLocation(new Location(rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
                empty = false;
            }

            if (empty) {
                return null;
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
        return home;
    }

    /**
     * Get all homes by UUID
     * @param owner uuid of the owner
     * @return set of homes
     */
    public Set<Home> getHomes(UUID owner) {
        HashSet<Home> homes = new HashSet<>();
        ResultSet rs = null;
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "homes WHERE owner = ?;");
            statement.setString(1, owner.toString());
            rs = statement.executeQuery();
            while (rs.next()) {
                Home home = new Home();
                home.setId(rs.getInt("id"));
                home.setName(rs.getString("name").toLowerCase());
                home.setServer(rs.getString("server"));
                home.setOwner(UUID.fromString(rs.getString("owner")));
                home.setLocation(new Location(rs.getString("world"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
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

    /**
     * DeleteCommand home
     * @return if deletion was successful or not
     */
    public Boolean delete() {
        try {
            connection = db.hikari.getConnection();
            statement = connection.prepareStatement("DELETE FROM " + tablePrefix + "homes WHERE name = ? AND owner = ?");
            statement.setString(1, getName().toLowerCase());
            statement.setString(2, getOwner().toString());
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
