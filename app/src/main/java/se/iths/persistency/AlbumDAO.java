package se.iths.persistency;

import se.iths.persistency.model.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class AlbumDAO implements CRUDInterface<Album> {

    Connection con = null;
    @Override
    public Collection<Album> findAll() throws SQLException {
        con = ConnectToDB.connect();
        Collection<Album> albums = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery("SELECT AlbumId, Title, ArtistId FROM Album");
        long oldId = -1L;
        while (rs.next()) {
            long albumId = rs.getLong("AlbumId");
            String title = rs.getString("Title");
            long artistId = rs.getLong("ArtistId");
            if (albumId != oldId) {
                Album album = new Album(title, artistId);
                album.setAlbumId(albumId);
                albums.add(album);
                oldId = albumId;
            }
        }
        rs.close();
        con.close();
        return albums;
    }

    @Override
    public Optional<Album> findById(long albumId) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("SELECT AlbumId, Title FROM Album WHERE AlbumId = ?;");
        stat.setLong(1, albumId);
        ResultSet rs = stat.executeQuery();
        String title = rs.getString("Title");
        long artistId = rs.getLong("ArtistId");
        Optional<Album> album = Optional.of(new Album(title, artistId));
        album.get().setAlbumId(albumId);
        rs.close();
        stat.close();
        con.close();
        return album;
    }

    public Collection<Album> findByArtistId(long artistId) throws SQLException {
        con = ConnectToDB.connect();
        Collection<Album> albums = new ArrayList<>();
        PreparedStatement stat = con.prepareStatement("SELECT AlbumId, Title, ArtistId FROM Album WHERE ArtistId = ?;");
        stat.setLong(1, artistId);
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            long albumId = rs.getLong("AlbumId");
            String title = rs.getString("Title");
            Optional<Album> album = Optional.of(new Album(title, artistId));
            album.get().setAlbumId(albumId);
            albums.add(album.get());
        }
        rs.close();
        stat.close();
        con.close();
        return albums;
    }

    @Override
    public Optional<Album> create(Album album) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("INSERT INTO Album(Title, ArtistId) VALUES (?, ?)");
        String title = album.getTitle();
        long artistId = album.getArtistId();
        stat.setString(1, title);
        stat.setLong(2,artistId);
        stat.execute();
        stat = con.prepareStatement("SELECT * FROM Album ORDER BY AlbumId DESC");
        ResultSet rs = stat.executeQuery();
        rs.next();
        long newAlbumId = rs.getLong("AlbumId");
        album.setAlbumId(newAlbumId);
        stat.close();
        con.close();
        return Optional.of(album);
    }

    @Override
    public Optional<Album> update(Album album) throws SQLException {
        con = ConnectToDB.connect();
        PreparedStatement stat = con.prepareStatement("UPDATE Album SET Title = ? WHERE AlbumId = ?");
        stat.setString(1, album.getTitle());
        stat.setLong(2, album.getAlbumId());
        stat.execute();
        stat.close();
        con.close();
        return Optional.of(album);
    }

    @Override
    public boolean delete(Album album) throws SQLException {
        con = ConnectToDB.connect();
        ResultSet rs = con.createStatement().executeQuery("SELECT Count(*) FROM Album");
        rs.next();
        long countBefore = rs.getLong("Count(*)");

        PreparedStatement stat = con.prepareStatement("DELETE FROM Album WHERE AlbumId = ?");
        stat.setLong(1, album.getAlbumId());
        stat.execute();

        rs = con.createStatement().executeQuery("SELECT Count(*) FROM Album");
        rs.next();
        long countAfter = rs.getLong("Count(*)");
        stat.close();
        con.close();
        return countBefore == countAfter + 1;
    }
}
