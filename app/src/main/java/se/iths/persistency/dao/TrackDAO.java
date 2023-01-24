package se.iths.persistency.dao;

import se.iths.persistency.CRUDInterface;
import se.iths.persistency.ConnectionHandler;
import se.iths.persistency.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TrackDAO implements CRUDInterface<Track> {
    private static final String TABLE_NAME = "Track";
    private static final String COL_ID = "TrackId";
    private static final String COL_ALBUM_ID = "Albumid";
    private static final String COL_NAME = "Name";
    private static final String COL_COUNT = "Count(*)";
    private static final String SQL_SELECT_BY_ID = String.format("SELECT %s, %s FROM %s WHERE %s = ?", COL_NAME, COL_ALBUM_ID, TABLE_NAME, COL_ID);
    private static final String SQL_SELECT_BY_PARENT_ID = String.format("SELECT %s, %s, %s FROM %s WHERE %S = ?", COL_ID, COL_NAME, COL_ALBUM_ID, TABLE_NAME, COL_ALBUM_ID);
    private static final String SQL_SELECT_ALL = String.format("SELECT %s, %s, %s FROM %s", COL_ID, COL_NAME, COL_ALBUM_ID, TABLE_NAME);
    private static final String SQL_SELECT_LAST = String.format("SELECT %s FROM %s ORDER BY %s DESC", COL_ID, TABLE_NAME, COL_ID);
    private static final String SQL_INSERT = String.format("INSERT INTO %s(%s, AlbumId, MediaTypeId, Milliseconds, UnitPrice) VALUES (?, ?, ?, ?, ?)", TABLE_NAME, COL_NAME);
    private static final String SQL_DELETE = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, COL_ID);
    private static final String SQL_DELETE_FROM_PLAYLIST = String.format("DELETE FROM PlaylistTrack WHERE %s = ?", COL_ID);
    private static final String SQL_UPDATE = String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_NAME, COL_NAME, COL_ID);
    private static final String SQL_COUNT_ROW = String.format("SELECT %s FROM %s", COL_COUNT, TABLE_NAME);
    Connection con = null;
    @Override
    public Collection<Track> findAll() throws SQLException {
        con = ConnectionHandler.connect();
        Collection<Track> tracks = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery(SQL_SELECT_ALL);
        long oldId = -1L;
        while (rs.next()) {
            long trackId = rs.getLong(COL_ID);
            String name = rs.getString(COL_NAME);
            long albumId = rs.getLong(COL_ALBUM_ID);
            if (trackId != oldId) {
                Track track = new Track(name, albumId);
                track.setTrackId(trackId);
                tracks.add(track);
                oldId = trackId;
            }
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(con);
        return tracks;
    }

    @Override
    public Optional<Track> findById(long trackId) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_SELECT_BY_ID);
        stat.setLong(1, trackId);
        ResultSet rs = stat.executeQuery();
        Optional<Track> track = Optional.empty();
        if (rs.next()) {
            String name = rs.getString(COL_NAME);
            long albumId = rs.getLong(COL_ALBUM_ID);
            track = Optional.of(new Track(name, albumId));
            track.get().setTrackId(trackId);
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return track;
    }



    public Collection<Track> findByAlbumId(long albumId) throws SQLException {
        con = ConnectionHandler.connect();
        Collection<Track> tracks = new ArrayList<>();
        PreparedStatement stat = con.prepareStatement(SQL_SELECT_BY_PARENT_ID);
        stat.setLong(1, albumId);
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            long trackId = rs.getLong(COL_ID);
            String name = rs.getString(COL_NAME);
            Optional<Track> track = Optional.of(new Track(name, albumId));
            track.get().setTrackId(trackId);
            tracks.add(track.get());
        }
        ConnectionHandler.close(rs);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return tracks;
    }

    @Override
    public Optional<Track> create(Track track) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_INSERT);
        String name = track.getName();
        long albumId = track.getAlbumId();
        if (albumId <= 0L) {
            throw new RuntimeException("Album can't be created without Artist!");
        }
        stat.setString(1, name);
        stat.setLong(2, albumId);
        stat.setLong(3, 1);
        stat.setLong(4, 1);
        stat.setDouble(5, 0.0);
        stat.execute();
        stat = con.prepareStatement(SQL_SELECT_LAST);
        ResultSet rs = stat.executeQuery();
        rs.next();
        long newTrackId = rs.getLong(COL_ID);
        track.setTrackId(newTrackId);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(track);
    }

    @Override
    public Optional<Track> update(Track track) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_UPDATE);
        stat.setString(1, track.getName());
        stat.setLong(2, track.getTrackId());
        stat.execute();
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(track);
    }

    @Override
    public boolean delete(Track track) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement(SQL_DELETE_FROM_PLAYLIST);
        stat.setLong(1, track.getTrackId());
        stat.execute();

        ResultSet rs = con.createStatement().executeQuery(SQL_COUNT_ROW);
        rs.next();
        long countBefore = rs.getLong(COL_COUNT);

        stat = con.prepareStatement(SQL_DELETE);
        stat.setLong(1, track.getTrackId());
        stat.execute();

        rs = con.createStatement().executeQuery(SQL_COUNT_ROW);
        rs.next();
        long countAfter = rs.getLong(COL_COUNT);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return countBefore == countAfter + 1;
    }
}
