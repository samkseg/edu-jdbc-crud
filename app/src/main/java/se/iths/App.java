package se.iths;

import se.iths.persistency.AlbumDAO;
import se.iths.persistency.ArtistDAO;
import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class App {

  private static ArrayList<Artist> artists = new ArrayList<>();
  private static ArrayList<Album> albums = new ArrayList<>();
  private static ArtistDAO artistDAO = new ArtistDAO();
  private static AlbumDAO albumDAO = new AlbumDAO();
  public static void main(String[] args) {
    App app = new App();
    try {

      app.load();
      addArtist("TestArtist");
      addAlbum(findArtist(276), "TestAlbum");
      addAlbum(findArtist(276), "TestAlbum2");

      updateArtist(findArtist(276), "NewTestArtist");
      updateAlbum(348, "NewTestAlbum");

      deleteAlbum(findArtist(276), 348);
//      deleteAlbum(findArtist(276), 349);
      deleteArtist(findArtist(276));

    } catch (SQLException e) {
      System.err.println(String.format("Error reading database %s", e.toString()));
    }
  }

  private void load() throws SQLException {
    loadArtistsAndAlbums();
    for(Artist artist : artists){
      System.out.println(artist);
    }
  }

  private void loadArtistsAndAlbums() throws SQLException {
    artists = (ArrayList<Artist>) artistDAO.findAll();
    albums = (ArrayList<Album>) albumDAO.findAll();
    for (Artist artist : artists) {
      Collection<Album> artistAlbums = albumDAO.findByArtistId(artist.getArtistId());
      artist.addAll(artistAlbums);
    }
  }

  private static void deleteAlbum(Optional<Artist> artist, long albumId) throws SQLException {
    if (!artist.get().getAlbums().isEmpty()) {
      for (Album album : artist.get().getAlbums()) {
        if (album.getAlbumId() == albumId) {
          boolean deleted = albumDAO.delete(album);
          if (deleted) {
            artist.get().remove(album);
            albums.remove(album);
          }
        }
      }
    }
  }


  private static void deleteArtist(Optional<Artist> artist) throws SQLException {
    for (Album album : artist.get().getAlbums()) {
      albumDAO.delete(album);
      albums.remove(album);
    }
    artist.get().removeAll();
    artistDAO.delete(artist.get());
    artists.remove(artist);
  }

  private static void updateAlbum(long albumId, String newTitle) throws SQLException {
    for (Album album : albums) {
      if (album.getAlbumId() == albumId) {
        album.setTitle(newTitle);
        albumDAO.update(album);
      }
    }
  }

  private static void updateArtist(Optional<Artist> artist, String newName) throws SQLException {
    artist.get().setName(newName);
    artistDAO.update(artist.get());
  }

  private static Optional<Artist> findArtist (long artistId) {
    Artist artistFound = null;
    for (Artist artist : artists) {
      if (artist.getArtistId() == artistId) {
        artistFound = artist;
      }
    }
    return Optional.of(artistFound);
  }

  private static void addArtist(String name) throws SQLException {
    Optional<Artist> artist = artistDAO.create(new Artist(name));
    artists.add(artist.get());
  }

  private static void addAlbum(Optional<Artist> artist, String name) throws SQLException {
    Optional<Album> album = albumDAO.create(new Album(name, artist.get().getArtistId()));
    artist.get().add(album.get());
    albums.add(album.get());
  }
}