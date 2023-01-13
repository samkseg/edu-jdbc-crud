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

    Optional<Artist> updatedArtist = artistDAO.create(new Artist("NewTestArtist"));
    Optional<Album> updatedAlbum = albumDAO.create(new Album("NewTestAlbum", updatedArtist.get().getArtistId()));
    albumDAO.update(updatedAlbum.get());
    artistDAO.update(updatedArtist.get());



  }
}