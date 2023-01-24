package se.iths.persistency.dao;

import se.iths.persistency.CRUDInterface;
import se.iths.persistency.ConnectionHandler;
import se.iths.persistency.model.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ArtistDAO implements CRUDInterface<Artist> {
    private static final String TABLE_NAME = "Artist";
    private static final String COL_ID = "ArtistId";
    private static final String COL_NAME = "Name";
    private static final String COL_COUNT = "Count(*)";
    private static final String SQL_SELECT_BY_ID = String.format("SELECT %s, %s FROM %s WHERE %s = ?", COL_ID, COL_NAME, TABLE_NAME, COL_ID);
    private static final String SQL_SELECT_ALL = String.format("SELECT %s, %s FROM %s", COL_ID, COL_NAME, TABLE_NAME);
    private static final String SQL_SELECT_LAST = String.format("SELECT %s FROM %s ORDER BY %s DESC", COL_ID, TABLE_NAME, COL_ID);
    private static final String SQL_INSERT = String.format("INSERT INTO %s (%s) VALUES(?)", TABLE_NAME, COL_NAME);
    private static final String SQL_DELETE = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, COL_ID);
    private static final String SQL_UPDATE = String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_NAME, COL_NAME, COL_ID);
    private static final String SQL_COUNT_ROW = String.format("SELECT %s FROM %s", COL_COUNT, TABLE_NAME);
    Connection con = null;
    @Override
    public Collection<Artist> findAll() throws SQLException {
        con = ConnectionHandler.connect();
        Collection<Artist> artists = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery(SQL_SELECT_ALL);
        long oldId = -1L;
        while (rs.next()) {
            long artistId = rs.getLong(COL_ID);
            String name = rs.getString(COL_NAME);
            if (artistId != oldId) {
                Artist artist = new Artist(name);
                artist.setArtistId(artistId);
                artists.add(artist);
                oldId = artistId;
            }
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(con);
        return artists;
    }

    @Override
    public Optional<Artist> findById(long artistId) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_SELECT_BY_ID);
        stat.setLong(1, artistId);
        ResultSet rs = stat.executeQuery();
        Optional<Artist> artist = Optional.empty();
        if (rs.next()) {
            long newArtistId = rs.getLong(COL_ID);
            String name = rs.getString(COL_NAME);
            artist = Optional.of(new Artist(name));
            artist.get().setArtistId(newArtistId);
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return artist;
    }

    @Override
    public Optional<Artist> create(Artist artist) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_INSERT);
        String name = artist.getName();
        stat.setString(1, name);
        stat.execute();
        stat = con.prepareStatement(SQL_SELECT_LAST);
        ResultSet rs = stat.executeQuery();
        rs.next();
        long newArtistId = rs.getLong(COL_ID);
        artist.setArtistId(newArtistId);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(artist);
    }

    @Override
    public Optional<Artist> update(Artist artist) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_UPDATE);
        stat.setString(1, artist.getName());
        stat.setLong(2, artist.getArtistId());
        stat.execute();
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(artist);
    }

    @Override
    public boolean delete(Artist artist) throws SQLException {
        con = ConnectionHandler.connect();
        ResultSet rs = con.createStatement().executeQuery(SQL_COUNT_ROW);
        rs.next();
        long countBefore = rs.getLong(COL_COUNT);

        PreparedStatement stat = con.prepareStatement(SQL_DELETE);
        stat.setLong(1, artist.getArtistId());
        stat.execute();

        rs = con.createStatement().executeQuery(SQL_COUNT_ROW);
        rs.next();
        long countAfter = rs.getLong(COL_COUNT);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);;
        return countBefore == countAfter + 1;
    }
}
