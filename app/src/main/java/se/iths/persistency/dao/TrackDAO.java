package se.iths.persistency.dao;

import se.iths.persistency.CRUDInterface;
import se.iths.persistency.ConnectToDB;
import se.iths.persistency.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TrackDAO implements CRUDInterface<Track> {
    Connection con = null;
    @Override
    public Collection<Track> findAll() throws SQLException {
        con = ConnectToDB.connect();
        Collection<Track> tracks = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery("SELECT TrackId, Name, AlbumId FROM Track");
        long oldId = -1L;
        while (rs.next()) {
            long trackId = rs.getLong("TrackId");
            String name = rs.getString("Name");
            long albumId = rs.getLong("AlbumId");
            if (trackId != oldId) {
                Track track = new Track(name, albumId);
                track.setTrackId(trackId);
                tracks.add(track);
                oldId = trackId;
            }
        }
        rs.close();
        con.close();
        return tracks;
    }

    @Override
    public Optional<Track> findById(long trackId) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("SELECT Name, AlbumId FROM Track WHERE TrackId = ?");
        stat.setLong(1, trackId);
        ResultSet rs = stat.executeQuery();
        Optional<Track> track = Optional.empty();
        if (rs.next()) {
            String name = rs.getString("Name");
            long albumId = rs.getLong("AlbumId");
            track = Optional.of(new Track(name, albumId));
            track.get().setTrackId(trackId);
        }
        rs.close();
        stat.close();
        con.close();
        return track;
    }



    public Collection<Track> findByAlbumId(long albumId) throws SQLException {
        con = ConnectToDB.connect();
        Collection<Track> tracks = new ArrayList<>();
        PreparedStatement stat = con.prepareStatement("SELECT TrackId, Name, AlbumId FROM Track WHERE AlbumId = ?");
        stat.setLong(1, albumId);
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            long trackId = rs.getLong("TrackId");
            String name = rs.getString("Name");
            Optional<Track> track = Optional.of(new Track(name, albumId));
            track.get().setTrackId(trackId);
            tracks.add(track.get());
        }
        rs.close();
        stat.close();
        con.close();
        return tracks;
    }

    @Override
    public Optional<Track> create(Track track) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("INSERT INTO Track(Name, AlbumId, MediaTypeId, Milliseconds, UnitPrice) VALUES (?, ?, ?, ?, ?)");
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
        stat = con.prepareStatement("SELECT * FROM Track ORDER BY TrackId DESC");
        ResultSet rs = stat.executeQuery();
        rs.next();
        long newTrackId = rs.getLong("TrackId");
        track.setTrackId(newTrackId);
        stat.close();
        con.close();
        return Optional.of(track);
    }

    @Override
    public Optional<Track> update(Track track) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("UPDATE Track SET Name = ? WHERE TrackId = ?");
        stat.setString(1, track.getName());
        stat.setLong(2, track.getTrackId());
        stat.execute();
        stat.close();
        con.close();
        return Optional.of(track);
    }

    @Override
    public boolean delete(Track track) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("DELETE FROM PlaylistTrack WHERE TrackId = ?");
        stat.setLong(1, track.getTrackId());
        stat.execute();

        ResultSet rs = con.createStatement().executeQuery("SELECT Count(*) FROM Track");
        rs.next();
        long countBefore = rs.getLong("Count(*)");

        stat = con.prepareStatement("DELETE FROM Track WHERE TrackId = ?");
        stat.setLong(1, track.getTrackId());
        stat.execute();

        rs = con.createStatement().executeQuery("SELECT Count(*) FROM Track");
        rs.next();
        long countAfter = rs.getLong("Count(*)");
        stat.close();
        con.close();
        return countBefore == countAfter + 1;
    }
}
