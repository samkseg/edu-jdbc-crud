package se.iths.persistency;

import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ArtistDAO implements CRUDInterface<Artist> {
    Connection con = null;
    @Override
    public Collection<Artist> findAll() throws Exception {
        con = ConnectToDB.connect();
        Collection<Artist> artists = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery("SELECT AlbumId, Title FROM Album");
        long oldId = -1L;
        while (rs.next()) {
            long artistId = rs.getLong("ArtistId");
            String name = rs.getString("Name");
            if (artistId != oldId) {
                Artist artist = new Artist(artistId, name);
                artists.add(artist);
                oldId = artistId;
            }
        }
        rs.close();
        con.close();
        return artists;
    }

    @Override
    public Optional<Artist> findById(long artistId) throws Exception {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("SELECT ArtistId, Name FROM Artist WHERE ArtistId = ?");
        stat.setLong(1, artistId);
        ResultSet rs = stat.executeQuery();
        String name = rs.getString("Name");
        Optional<Artist> artist = Optional.of(new Artist(artistId, name));
        rs.close();
        stat.close();
        con.close();
        return artist;
    }

    @Override
    public Optional<Artist> create(Artist artist) throws Exception {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("INSERT INTO Artist(Name) VALUES (?)");
        String title = artist.getName();
        stat.setString(1, title);
        stat.execute();
        stat = con.prepareStatement("SELECT ArtistId FROM Artist WHERE Name = ?");
        stat.setString(1, artist.getName());
        ResultSet rs = stat.executeQuery();
        long newArtistId = rs.getLong("ArtistId");
        artist.setArtistId(newArtistId);
        stat.close();
        con.close();
        return Optional.of(artist);
    }

    @Override
    public Optional<Artist> updated(Artist artist) throws Exception {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("UPDATE Artist SET Name = ? WHERE ArtistId = ?");
        stat.setString(1, artist.getName());
        stat.setLong(2, artist.getArtistId());
        stat.execute();
        stat.close();
        con.close();
        return Optional.of(artist);
    }

    @Override
    public boolean delete(Artist artist) throws Exception {
        con = ConnectToDB.connect();
        ResultSet rs = con.createStatement().executeQuery("SELECT Count(*) FROM Artist");
        long countBefore = rs.getLong("Count");
        PreparedStatement stat = con.prepareStatement("DELETE FROM Artist WHERE ArtistId = ?");
        stat.setLong(1, artist.getArtistId());
        stat.execute();
        ResultSet rs2 = con.createStatement().executeQuery("SELECT Count(*) FROM Artist");
        long countAfter = rs2.getLong("Count");
        stat.close();
        con.close();
        return countBefore == countAfter + 1;
    }
}
