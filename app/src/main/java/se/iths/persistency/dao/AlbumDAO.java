package se.iths.persistency.dao;

import se.iths.persistency.CRUDInterface;
import se.iths.persistency.ConnectionHandler;
import se.iths.persistency.model.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class AlbumDAO implements CRUDInterface<Album> {
    private static final String TABLE_NAME = "Album";
    private static final String COL_ID = "AlbumId";
    private static final String COL_ARTIST_ID = "ArtistId";
    private static final String COL_TITLE = "Title";
    private static final String COL_COUNT = "Count(*)";
    private static final String SQL_SELECT_BY_ID = String.format("SELECT %s, %s FROM %s WHERE %s = ?", COL_TITLE, COL_ARTIST_ID, TABLE_NAME, COL_ID);
    private static final String SQL_SELECT_BY_PARENT_ID = String.format("SELECT %s, %s, %s FROM %s WHERE %S = ?", COL_ID, COL_TITLE, COL_ARTIST_ID, TABLE_NAME, COL_ARTIST_ID);
    private static final String SQL_SELECT_LAST = String.format("SELECT %s FROM %s ORDER BY %s DESC", COL_ID, TABLE_NAME, COL_ID);
    private static final String SQL_SELECT_ALL = String.format("SELECT %s, %s, %s FROM %s", COL_ID, COL_TITLE, COL_ARTIST_ID, TABLE_NAME);
    private static final String SQL_INSERT = String.format("INSERT INTO %s (%s, %s) VALUES(?, ?)", TABLE_NAME, COL_TITLE, COL_ARTIST_ID);
    private static final String SQL_DELETE = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, COL_ID);
    private static final String SQL_UPDATE = String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_NAME, COL_TITLE, COL_ID);
    private static final String SQL_COUNT_ROW = String.format("SELECT %s FROM %s", COL_COUNT, TABLE_NAME);
    Connection con = null;
    @Override
    public Collection<Album> findAll() throws SQLException {
        con = ConnectionHandler.connect();
        Collection<Album> albums = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery(SQL_SELECT_ALL);
        long oldId = -1L;
        while (rs.next()) {
            long albumId = rs.getLong(COL_ID);
            String title = rs.getString(COL_TITLE);
            long artistId = rs.getLong(COL_ARTIST_ID);
            if (albumId != oldId) {
                Album album = new Album(title, artistId);
                album.setAlbumId(albumId);
                albums.add(album);
                oldId = albumId;
            }
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(con);
        return albums;
    }

    @Override
    public Optional<Album> findById(long albumId) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_SELECT_BY_ID);
        stat.setLong(1, albumId);
        ResultSet rs = stat.executeQuery();
        Optional<Album> album = Optional.empty();
        if(rs.next()) {
            String title = rs.getString(COL_TITLE);
            long artistId = rs.getLong(COL_ARTIST_ID);
            album = Optional.of(new Album(title, artistId));
            album.get().setAlbumId(albumId);
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return album;
    }

    public Collection<Album> findByArtistId(long artistId) throws SQLException {
        con = ConnectionHandler.connect();
        Collection<Album> albums = new ArrayList<>();
        PreparedStatement stat = con.prepareStatement(SQL_SELECT_BY_PARENT_ID);
        stat.setLong(1, artistId);
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            long albumId = rs.getLong(COL_ID);
            String title = rs.getString(COL_TITLE);
            Optional<Album> album = Optional.of(new Album(title, artistId));
            album.get().setAlbumId(albumId);
            albums.add(album.get());
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return albums;
    }

    @Override
    public Optional<Album> create(Album album) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_INSERT);
        String title = album.getTitle();
        long artistId = album.getArtistId();
        if (artistId <= 0L) {
            throw new RuntimeException("Album can't be created without Artist!");
        }
        stat.setString(1, title);
        stat.setLong(2,artistId);
        stat.execute();
        stat = con.prepareStatement(SQL_SELECT_LAST);
        ResultSet rs = stat.executeQuery();
        rs.next();
        long newAlbumId = rs.getLong(COL_ID);
        album.setAlbumId(newAlbumId);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(album);
    }

    @Override
    public Optional<Album> update(Album album) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_UPDATE);
        stat.setString(1, album.getTitle());
        stat.setLong(2, album.getAlbumId());
        stat.execute();
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(album);
    }

    @Override
    public boolean delete(Album album) throws SQLException {
        con = ConnectionHandler.connect();
        ResultSet rs = con.createStatement().executeQuery(SQL_COUNT_ROW);
        rs.next();
        long countBefore = rs.getLong(COL_COUNT);

        PreparedStatement stat = con.prepareStatement(SQL_DELETE);
        stat.setLong(1, album.getAlbumId());
        stat.execute();

        rs = con.createStatement().executeQuery(SQL_COUNT_ROW);
        rs.next();
        long countAfter = rs.getLong(COL_COUNT);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return countBefore == countAfter + 1;
    }
}
