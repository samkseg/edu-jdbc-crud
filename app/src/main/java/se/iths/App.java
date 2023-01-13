package se.iths;

import se.iths.persistency.AlbumDAO;
import se.iths.persistency.ArtistDAO;
import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.util.Optional;

public class App {



  public static void main(String[] args) throws Exception {
    ArtistDAO artistDAO = new ArtistDAO();
    AlbumDAO albumDAO = new AlbumDAO();

    Optional<Artist> artist = artistDAO.create(new Artist(-1L, "TestArtist"));
    Optional<Album> album = albumDAO.create(new Album(-1L, "TestAlbum", artist.get().getArtistId()));
  }
}