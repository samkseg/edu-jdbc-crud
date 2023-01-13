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

    Optional<Artist> artist = artistDAO.create(new Artist("TestArtist"));
    Optional<Album> album = albumDAO.create(new Album("TestAlbum", artist.get().getArtistId()));
    artist.get().add(album.get());

    albumDAO.update(new Album("NewTestAlbum", artist.get().getArtistId()));
    artistDAO.update(new Artist("NewTestArtist"));

  }
}