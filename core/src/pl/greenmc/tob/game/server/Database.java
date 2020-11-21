package pl.greenmc.tob.game.server;

import pl.greenmc.tob.game.Player;

import java.sql.*;

import static pl.greenmc.tob.game.util.Logger.log;

public class Database {
    private Connection conn = null;

    public Database() {
        try {
            // db parameters
            String url = "jdbc:sqlite:database.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            log("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Player getPlayer(int ID) throws SQLException {
        String sql = "SELECT * FROM players WHERE ID = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new Player(rs.getInt("ID"), rs.getString("identity"), rs.getString("name"));
        }
        return null;
    }

    public Player addPlayer(String identity) throws SQLException {
        String sql = "INSERT INTO players (identity, name) VALUES (?, null)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, identity);
        pstmt.execute();

        return getPlayer(identity);
    }

    public Player getPlayer(String identity) throws SQLException {
        String sql = "SELECT * FROM players WHERE identity = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, identity);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new Player(rs.getInt("ID"), rs.getString("identity"), rs.getString("name"));
        }
        return null;
    }
}
