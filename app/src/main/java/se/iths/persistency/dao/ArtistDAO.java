package se.iths.persistency.dao;

import se.iths.persistency.CRUDInterface;
import se.iths.persistency.ConnectionHandler;
import se.iths.persistency.model.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ArtistDAO implements CRUDInterface<Artist> {
    Connection con = null;
    @Override
    public Collection<Artist> findAll() throws SQLException {
        con = ConnectionHandler.connect();
        Collection<Artist> artists = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery("SELECT ArtistId, Name FROM Artist");
        long oldId = -1L;
        while (rs.next()) {
            long artistId = rs.getLong("ArtistId");
            String name = rs.getString("Name");
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
        PreparedStatement stat = con.prepareStatement("SELECT ArtistId, Name FROM Artist WHERE ArtistId = ?");
        stat.setLong(1, artistId);
        ResultSet rs = stat.executeQuery();
        Optional<Artist> artist = Optional.empty();
        if (rs.next()) {
            long newArtistId = rs.getLong("ArtistId");
            String name = rs.getString("Name");
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
        PreparedStatement stat = con.prepareStatement("INSERT INTO Artist(Name) VALUES (?)");
        String name = artist.getName();
        stat.setString(1, name);
        stat.execute();
        stat = con.prepareStatement("SELECT * FROM Artist ORDER BY ArtistId DESC");
        ResultSet rs = stat.executeQuery();
        rs.next();
        long newArtistId = rs.getLong("ArtistId");
        artist.setArtistId(newArtistId);
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);
        return Optional.of(artist);
    }

    @Override
    public Optional<Artist> update(Artist artist) throws SQLException {
        con = ConnectionHandler.connect();
        PreparedStatement stat = con.prepareStatement("UPDATE Artist SET Name = ? WHERE ArtistId = ?");
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
        ResultSet rs = con.createStatement().executeQuery("SELECT Count(*) FROM Artist");
        rs.next();
        long countBefore = rs.getLong("Count(*)");

        PreparedStatement stat = con.prepareStatement("DELETE FROM Artist WHERE ArtistId = ?");
        stat.setLong(1, artist.getArtistId());
        stat.execute();

        rs = con.createStatement().executeQuery("SELECT Count(*) FROM Artist");
        rs.next();
        long countAfter = rs.getLong("Count(*)");
        ConnectionHandler.close(stat);
        ConnectionHandler.close(con);;
        return countBefore == countAfter + 1;
    }
}
