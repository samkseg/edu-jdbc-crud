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

      Optional<Artist> testGetArtistFromDatabaseAndReplaceWithNewObject = findArtistById(276);
      Optional<Album> testGetAlbumFromDatabaseAndReplaceWithNewObject = findAlbumById(348);

      updateArtist(276, "NewTestArtist");
      updateAlbum(348, "NewTestAlbum");

      deleteAlbum(276, 348);
      deleteArtist(276);

      addArtist("TestArtist2");
      addAlbum(277, "TestAlbum3");
      addAlbum(277, "TestAlbum4");

      printList();
      System.out.println("Expected: TestArtist2 (ArtistId 277), TestAlbum3 (AlbumId 350) + TestAlbum4 (AlbumId 351)");

    } catch (SQLException e) {
      System.err.println(String.format("Error reading database %s", e.toString()));
    }
  }

  private void load() throws SQLException {
    loadArtistsAndAlbums();
    printList();
  }

  private static void printList() {
    for(Artist artist : artists.values()){
      System.out.println(artist);
    }
  }

  //  CREATE - add new objects to database
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

  // READ - load all & get new objects by id from database
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
    if (artist.isPresent()) {
      Collection<Album> artistAlbums = albumDAO.findByArtistId(artistId);
      artist.get().addAll(artistAlbums);
      artists.replace(artistId, artist.get());
      artistAlbums.forEach(album -> albums.replace(album.getAlbumId(), album));
    }
    return artist;
  }

  private static Optional<Album> findAlbumById (long albumId) throws SQLException {
    Optional<Album> album = albumDAO.findById(albumId);
    if (album.isPresent()) {
      albums.replace(albumId, album.get());
      Artist artist = artists.get(album.get().getArtistId());
      artist.replace(album.get());
    }
    return album;
  }

  // UPDATE - renames objects and pushes to database
  private static void updateArtist(long artistId, String newName) throws SQLException {
    Optional<Artist> artist = Optional.of(artists.get(artistId));
    artist.get().setName(newName);
    artistDAO.update(artist.get());
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
    boolean deletedFromDB = artistDAO.delete(artist.get());
    if (deletedFromDB) artists.remove(artist.get().getArtistId());
  }

  private static void deleteAlbum(long artistId, long albumId) throws SQLException {
    Optional<Artist> artist = Optional.of(artists.get(artistId));
    Album album = artist.get().getAlbum(albumId);
    boolean deletedFromDB = albumDAO.delete(album);
      if (deletedFromDB) {
        albums.remove(albumId);
        artist.get().remove(album);
      }
  }
}