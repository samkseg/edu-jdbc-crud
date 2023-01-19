package se.iths;

import se.iths.persistency.AlbumDAO;
import se.iths.persistency.ArtistDAO;
import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.sql.*;
import java.util.*;

public class App {
  private static HashMap<Long, Artist> artists = new HashMap();
  private static HashMap<Long, Album> albums = new HashMap();
  private static ArtistDAO artistDAO = new ArtistDAO();
  private static AlbumDAO albumDAO = new AlbumDAO();
  public static void main(String[] args) {
    App app = new App();
    try {

      app.load();

      addArtist("TestArtist");
      addAlbum(276, "TestAlbum");
      addAlbum(276, "TestAlbum2");

      Optional<Artist> testArtist = findArtistById(276);
      Optional<Album> testFindAlbum = findAlbumById(348);

      updateArtist(276, "NewTestArtist");
      updateAlbum(348, "NewTestAlbum");

      deleteAlbum(276, 348);
      deleteArtist(276);

    } catch (SQLException e) {
      System.err.println(String.format("Error reading database %s", e.toString()));
    }
  }

  private void load() throws SQLException {
    loadArtistsAndAlbums();
    for(Artist artist : artists.values()){
      System.out.println(artist);
    }
  }

//  CREATE - add objects to database
  private static void addArtist(String name) throws SQLException {
    Optional<Artist> artist = artistDAO.create(new Artist(name));
    artists.put(artist.get().getArtistId(), artist.get());
  }

  private static void addAlbum(long artistId, String name) throws SQLException {
    Artist artist = artists.get(artistId);
    Optional<Album> album = albumDAO.create(new Album(name, artist.getArtistId()));
    artist.add(album.get());
    albums.put(album.get().getAlbumId(), album.get());
  }

  // READ - load all & find from database
  private void loadArtistsAndAlbums() throws SQLException {
    artists.clear();
    albums.clear();
    for (Artist artist : artistDAO.findAll()) {
      artists.put(artist.getArtistId(), artist);
      Collection<Album> artistAlbums = albumDAO.findByArtistId(artist.getArtistId());
      artist.addAll(artistAlbums);
      artistAlbums.forEach(album -> albums.put(album.getAlbumId(), album));
    }
  }

  private static Optional<Artist> findArtistById (long artistId) throws SQLException {
    Optional<Artist> artist = artistDAO.findById(artistId);
    if (!artist.isEmpty()) {
      Collection<Album> artistAlbums = albumDAO.findByArtistId(artist.get().getArtistId());
      artist.get().addAll(artistAlbums);
      artists.replace(artistId, artist.get());
      artistAlbums.forEach(album -> albums.replace(album.getAlbumId(), album));
    }
    return artist;
  }

  private static Optional<Album> findAlbumById (long albumId) throws SQLException {
    Optional<Album> album = Optional.of(albums.get(albumId));
    Optional<Album> newAlbum = albumDAO.findById(albumId);
    if (!newAlbum.isEmpty()) {
      albums.replace(albumId, newAlbum.get());
      Optional<Artist> artist = artistDAO.findById(newAlbum.get().getArtistId());
      if (!album.isEmpty()) artist.get().remove(album.get());
      artist.get().add(newAlbum.get());
    }
    return album;
  }

  // UPDATE - renames objects and pushes to database
  private static void updateArtist(long artistId, String newName) throws SQLException {
    Optional<Artist> artist = Optional.of(artists.get(artistId));
    artist.get().setName(newName);
    artist = artistDAO.update(artist.get());
  }

  private static void updateAlbum(long albumId, String newTitle) throws SQLException {
    Album album = albums.get(albumId);
    album.setTitle(newTitle);
    albumDAO.update(album);
  }

  // DELETE - removes objects from database
  private static void deleteArtist(long artistId) throws SQLException {
    Optional<Artist> artist = Optional.of(artists.get(artistId));
    int counter = 0;
    for (Album album : artist.get().getAlbums()) {
      if (albumDAO.delete(album)) counter++;
    }
    if (counter == artist.get().getAlbums().size()) artist.get().removeAll();
    boolean artistDeleted = artistDAO.delete(artist.get());
    if (artistDeleted) artists.remove(artist.get().getArtistId());
  }

  private static void deleteAlbum(long artistId, long albumId) throws SQLException {
    Optional<Artist> artist = Optional.of(artists.get(artistId));
    for (Album album : artist.get().getAlbums()) {
      if (album.getAlbumId() == albumId) {
        boolean deleted = albumDAO.delete(album);
        if (deleted) {
            artist.get().remove(album);
        }
      }
    }
  }
}