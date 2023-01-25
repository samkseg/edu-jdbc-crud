package se.iths.persistency.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.iths.App;
import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistDAOTest {
    static ArtistDAO artistDAO = new ArtistDAO();

    @BeforeAll
    public static void setUp() throws Exception {
        App app = new App();
        app.load();
    }
    @AfterAll
    public static void last() throws Exception {
        deleteAllModified();
    }

    private static void deleteAllModified() throws SQLException {
        Optional<Artist> artist1 = artistDAO.findById(280);
        artistDAO.delete(artist1.get());
        Optional<Artist> artist2 = artistDAO.findById(282);
        artistDAO.delete(artist2.get());
        Optional<Artist> artist3 = artistDAO.findById(283);
        artistDAO.delete(artist3.get());
        Optional<Artist> artist4 = artistDAO.findById(285);
        artistDAO.delete(artist4.get());
        Optional<Artist> artist5 = artistDAO.findById(286);
        artistDAO.delete(artist5.get());
    }

    @Test
    public void shouldFindAllArtists() throws SQLException {
        Collection<Artist> Artists = artistDAO.findAll();

        assertNotNull(Artists, "Artists must not be null after find all!");
        assertTrue(Artists.size() > 0 , "Artists must exist after find all!");
    }

    @Test
    public void shouldFindArtistById() throws SQLException {
        Long ArtistId = artistDAO.create(new Artist("A name")).get().getArtistId();

        Optional<Artist> artist = artistDAO.findById(ArtistId);

        assertNotNull(artist.get());
    }

    @Test
    public void shouldNotFindArtistByFaultyId() throws SQLException {
        Long nonExistingId = -1L;
        Optional<Artist> artist = artistDAO.findById(nonExistingId);
        assertTrue(artist.isEmpty(), "Artists must not be found with faulty id!");
    }

    @Test
    public void shouldCreateArtist() throws SQLException {
        Artist artist = new Artist("A Name");
        Optional<Artist> persistentArtist = artistDAO.create(artist);
        assertNotNull( persistentArtist.get().getArtistId(), "Artist id must not be null after create!");
        assertTrue(persistentArtist.get().getArtistId() >0, "Artist id must be greater than 0 after create!");
    }

    @Test
    public void shouldCreateArtistWithAlbums() throws SQLException {
        Artist artist = new Artist("A Name");
        artist.add(new Album("Title 1", artist.getArtistId()));
        artist.add(new Album("Title 2", artist.getArtistId()));
        artist.add(new Album("Title 3", artist.getArtistId()));

        Optional<Artist> persistentArtist = artistDAO.create(artist);

        assertNotNull( persistentArtist.get().getArtistId(), "Artist id must not be null after create!");
        assertTrue(persistentArtist.get().getArtistId() >0, "Artist id must be greater than 0 after create!");
    }

    @Test
    public void shouldUpdateArtist() throws SQLException {
        Long existingId = 1L;
        String oldName = "An old Name";
        String newName = "A new Name";
        Optional<Artist> artist = artistDAO.create(new Artist(oldName));

        artist.get().setName(newName);
        artist = artistDAO.update(artist.get());

        assertEquals(newName, artist.get().getName());
    }

    @Test
    public void shouldUpdateArtistAndAlbums() throws SQLException {
        Long existingId = 1L;
        String oldName = "An old Name";
        String newName = "A new Name";

        Optional<Artist> artist = Optional.of(new Artist(oldName));
        artist.get().add(new Album("Title 4", artist.get().getArtistId()));
        artist.get().add(new Album("Title 5", artist.get().getArtistId()));
        artist.get().add(new Album("Title 6", artist.get().getArtistId()));

        artist = artistDAO.create(artist.get());

        artist.get().setName(newName);
        for(Album album : artist.get().getAlbums()){
            album.setTitle(album.getTitle() + "_updated");
        }
        artist = artistDAO.update(artist.get());

        assertEquals(newName, artist.get().getName());
    }

    @Test
    public void shouldDeleteArtist() throws SQLException {
        Optional<Artist> artist = artistDAO.create(new Artist("A Name"));

        long artistId = artist.get().getArtistId();
        artistDAO.delete(artist.get());

        assertTrue(artistDAO.findById(artistId).isEmpty(), "Artist must not exist after delete");
    }

    @Test
    public void shouldDeleteArtistWithAlbums() throws SQLException {
        Optional<Artist> artist = artistDAO.create(new Artist("A Name"));

        long artistId = artist.get().getArtistId();
        artistDAO.delete(artist.get());

        assertTrue(artistDAO.findById(artistId).isEmpty(), "Artist must not exist after delete");
    }
}