package se.iths;

import se.iths.persistency.AlbumDAO;
import se.iths.persistency.ArtistDAO;
import se.iths.persistency.TrackDAO;
import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;
import se.iths.persistency.model.Track;

import java.sql.*;
import java.util.*;

public class App {
  private static HashMap<Long, Artist> artists = new HashMap();
  private static ArtistDAO artistDAO = new ArtistDAO();
  private static AlbumDAO albumDAO = new AlbumDAO();
  private static TrackDAO trackDAO = new TrackDAO();
  public static void main(String[] args) {
    App app = new App();
    try {

      app.load();

//    CRUD

      addArtist("TestArtist");
      addAlbum(276, "TestAlbum");
      addAlbum(276, "TestAlbum2");

      Optional<Artist> testGetArtistFromDatabase = findArtistById(276);
      Optional<Album> testGetAlbumFromDatabase = findAlbumById(348);

      updateArtist(276, "NewTestArtist");
      updateAlbum(348, "NewTestAlbum");

      deleteAlbum(348);
      deleteArtist(276);

      addArtist("TestArtist2");
      addArtist("TestArtist3");
      addAlbum(277, "TestAlbum3");
      addAlbum(277, "TestAlbum4");
      addAlbum(278, "TestAlbum5");

      addTrack(277, 350, "TestTrack");
      addTrack(277, 350, "TestTrack2");
      addTrack(277, 351, "TestTrack3");
      addTrack(278, 352, "TestTrack4");

      Optional<Track> testGetTrackFromDatabase = findTrackById(3504);

      updateTrack(3504, "NewTestTrack");

      deleteTrack(3505);
      deleteAlbum(351);
      deleteArtist(278);

//    Handle empty Optional

      addAlbum(0, "TestAlbumWithoutArtist");
      addTrack(0, 350, "TestTrackWithoutArtist");
      addTrack(277, 0, "TestTrackWithoutAlbum");



      updateArtist(0, "NewTestArtist");
      updateAlbum(0, "NewTestAlbum");
      updateTrack(0, "NewTestTrack2");

      boolean deletedArtist = deleteArtist(0);
      boolean deletedAlbum = deleteAlbum(0);
      boolean deletedTrack = deleteTrack(0);

      printList();
      System.out.println("Expected:\n277: TestArtist2\n\tAlbums:\n\t\t350: TestAlbum3\n\t\t\tTracks:\n\t\t\t\t3504: NewTestTrack\n");

      Optional<Artist> testGetEmptyArtistFromDatabase = findArtistById(0);
      Optional<Album> testGetEmptyAlbumFromDatabase = findAlbumById(0);
      Optional<Track> testGetEmptyTrackFromDatabase = findTrackById(0);

      if (!deletedArtist) System.out.println("ArtistId: 0 not found");
      if (!deletedAlbum) System.out.println("AlbumId: 0 not found");
      if (!deletedTrack) System.out.println("TrackId: 0 not found");

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
    artist.ifPresent(a -> artists.put(a.getArtistId(), a));
  }

  private static void addAlbum(long artistId, String title) throws SQLException {
    Optional<Artist> artist = findArtistById(artistId);
    if (artist.isPresent()) {
      Optional<Album> album = albumDAO.create(new Album(title, artist.get().getArtistId()));
      album.ifPresent(a -> artist.get().add(a));
    }
  }

  private static void addTrack(long artistId, long albumId, String name) throws SQLException {
    Optional<Artist> artist = findArtistById(artistId);
    Optional<Album> album = findAlbumById(albumId);
    if (artist.isPresent() && album.isPresent()) {
      Optional<Track> track = trackDAO.create(new Track(name, album.get().getAlbumId()));
      track.ifPresent(a -> album.get().add(a));
    }
  }

  // READ - load all & get new objects by id from database
  private void loadArtistsAndAlbums() throws SQLException {
    artists.clear();
    for (Artist artist : artistDAO.findAll()) {
      artists.put(artist.getArtistId(), artist);
      Collection<Album> artistAlbums = albumDAO.findByArtistId(artist.getArtistId());
      artist.addAll(artistAlbums);
      for (Album album : artistAlbums) {
        Collection<Track> albumTracks = trackDAO.findByAlbumId(album.getAlbumId());
        album.addAll(albumTracks);
      }
    }
  }

  private static Optional<Artist> findArtistById (long artistId) throws SQLException {
    Optional<Artist> artist = artistDAO.findById(artistId);
    if (artist.isPresent()) {
      Collection<Album> artistAlbums = albumDAO.findByArtistId(artistId);
      for (Album album : artistAlbums) {
        Collection<Track> albumTracks = trackDAO.findByAlbumId(album.getAlbumId());
        album.addAll(albumTracks);
      }
      artist.get().addAll(artistAlbums);
      artists.replace(artistId, artist.get());
      return artist;
    }
    return Optional.empty();
  }

  private static Optional<Album> findAlbumById (long albumId) throws SQLException {
    Optional<Album> album = albumDAO.findById(albumId);
    if (album.isPresent()) {
      Optional<Artist> artist = findArtistById(album.get().getArtistId());
      if (artist.isPresent()) {
        Collection<Track> albumTracks = trackDAO.findByAlbumId(albumId);
        album.get().addAll(albumTracks);
        artist.get().replace(album.get());
        return album;
      }
    }
    return Optional.empty();
  }

  private static Optional<Track> findTrackById (long trackId) throws SQLException {
    Optional<Track> track = trackDAO.findById(trackId);
    if (track.isPresent()) {
      Optional<Album> album = findAlbumById(track.get().getAlbumId());
      Optional<Artist> artist = findArtistById(album.get().getArtistId());
      if (album.isPresent() && artist.isPresent()) {
        album.get().replace(track.get());
        artist.get().replace(album.get());
        return track;
      }
    }
    return Optional.empty();
  }

  // UPDATE - renames objects and pushes to database
  private static void updateArtist(long artistId, String newName) throws SQLException {
    Optional<Artist> artist = findArtistById(artistId);
    if (artist.isPresent()) {
      artist.get().setName(newName);
      artistDAO.update(artist.get());
    }
  }

  private static void updateAlbum(long albumId, String newTitle) throws SQLException {
    Optional<Album> album = findAlbumById(albumId);
    if (album.isPresent()) {
      album.get().setTitle(newTitle);
      albumDAO.update(album.get());
    }
  }

  private static void updateTrack(long trackId, String newName) throws SQLException {
    Optional<Track> track = findTrackById(trackId);
    if (track.isPresent()) {
      track.get().setName(newName);
      trackDAO.update(track.get());
    }
  }

  // DELETE - removes objects from database
  private static boolean deleteArtist(long artistId) throws SQLException {
    Optional<Artist> artist = findArtistById(artistId);
    int countAlbum = 0;
    if (artist.isPresent()) {
      for (Album album : artist.get().getAlbums()) {
        int countTrack = 0;
        for (Track track : album.getTracks()) {
          if (trackDAO.delete(track)) countTrack++;
        }
        if (countTrack == album.getTracks().size()) album.removeAll();
        if (albumDAO.delete(album)) countAlbum++;
      }
      if (countAlbum == artist.get().getAlbums().size()) artist.get().removeAll();
      boolean deletedFromDB = artistDAO.delete(artist.get());
      if (deletedFromDB) {
        artists.remove(artist.get().getArtistId());
        return true;
      }
    }
    return false;
  }

  private static boolean deleteAlbum(long albumId) throws SQLException {
    Optional<Album> album = findAlbumById(albumId);
    if (album.isPresent()) {
      Optional<Artist> artist = findArtistById(album.get().getArtistId());
      int counter = 0;
      for (Track track : album.get().getTracks()) {
        if (trackDAO.delete(track)) counter++;
      }
      if (counter == album.get().getTracks().size()) album.get().removeAll();
      boolean deletedFromDB = albumDAO.delete(album.get());
      if (deletedFromDB) {
        artist.get().remove(album.get());
        return true;
      }
    }
    return false;
  }

  private  static boolean deleteTrack(long trackId) throws SQLException {
    Optional<Track> track = findTrackById(trackId);
    if (track.isPresent()) {
      Optional<Album> album = findAlbumById(track.get().getAlbumId());
      boolean deletedFromDB = trackDAO.delete(track.get());
      if (deletedFromDB) {
        album.get().remove(track.get());
        return true;
      }
    }
    return false;
  }
}