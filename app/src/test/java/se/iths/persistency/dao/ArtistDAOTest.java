package se.iths.persistency.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.iths.App;
import se.iths.persistency.ConnectToDB;
import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistDAOTest {
    private static Connection con = null;
    ArtistDAO artistDAO = new ArtistDAO();

    @BeforeAll
    public static void setUp() throws Exception {
        con = ConnectToDB.connect();
        App app = new App();
        app.load();
    }

    @Test
    public void shouldFindAllArtists() throws SQLException {
        //Given

        //When
        Collection<Artist> Artists = artistDAO.findAll();
        //Then
        assertNotNull(Artists, "Artists must not be null after find all!");
        assertTrue(Artists.size() > 0 , "Artists must exist after find all!");
    }

    @Test
    public void shouldFindArtistById() throws SQLException {
        //Given
        Long ArtistId = artistDAO.create(new Artist("A name")).get().getArtistId();

        //When
        Optional<Artist> artist = artistDAO.findById(ArtistId);

        //Then
        assertNotNull(artist.get());
    }

    @Test
    public void shouldNotFindArtistByFaultyId() throws SQLException {
        //Given
        Long nonExistingId = -1L;

        //When
        Optional<Artist> artist = artistDAO.findById(nonExistingId);

        //Then
        assertTrue(artist.isEmpty(), "Artists must not be found with faulty id!");
    }

    @Test
    public void shouldCreateArtist() throws SQLException {
        //Given
        Artist artist = new Artist("A Name");

        //When
        Optional<Artist> persistentArtistm = artistDAO.create(artist);

        //Then
        assertNotNull( persistentArtistm.get().getArtistId(), "Artist id must not be null after create!");
        assertTrue(persistentArtistm.get().getArtistId() >0, "Artist id must be greater than 0 after create!");
    }

    @Test
    public void shouldCreateArtistWithAlbums() throws SQLException {
        //Given
        Artist artist = new Artist("A Name");
        artist.add(new Album("Title 1", artist.getArtistId()));
        artist.add(new Album("Title 2", artist.getArtistId()));
        artist.add(new Album("Title 3", artist.getArtistId()));

        //When
        Optional<Artist> persistentArtistm = artistDAO.create(artist);

        //Then
        assertNotNull( persistentArtistm.get().getArtistId(), "Artist id must not be null after create!");
        assertTrue(persistentArtistm.get().getArtistId() >0, "Artist id must be greater than 0 after create!");
    }

    @Test
    public void shouldUpdateArtist() throws SQLException {
        //Given
        Long existingId = 1L;
        String oldName = "An old Name";
        String newName = "A new Name";
        Optional<Artist> artist = artistDAO.create(new Artist(oldName));

        //When
        artist.get().setName(newName);
        artist = artistDAO.update(artist.get());

        //Then
        assertEquals(newName, artist.get().getName());
    }

    @Test
    public void shouldUpdateArtistAndAlbums() throws SQLException {
        //Given
        Long existingId = 1L;
        String oldName = "An old Name";
        String newName = "A new Name";

        Optional<Artist> artist = Optional.of(new Artist(oldName));
        artist.get().add(new Album("Title 4", artist.get().getArtistId()));
        artist.get().add(new Album("Title 5", artist.get().getArtistId()));
        artist.get().add(new Album("Title 6", artist.get().getArtistId()));

        artist = artistDAO.create(artist.get());

        //When
        artist.get().setName(newName);
        for(Album album : artist.get().getAlbums()){
            album.setTitle(album.getTitle() + "_updated");
        }
        artist = artistDAO.update(artist.get());

        //Then
        assertEquals(newName, artist.get().getName());
    }

    @Test
    public void shouldDeleteArtist() throws SQLException {
        //Given
        Optional<Artist> artist = artistDAO.create(new Artist("A Name"));

        //When
        long artistId = artist.get().getArtistId();
        artistDAO.delete(artist.get());

        //Then
        assertTrue(artistDAO.findById(artistId).isEmpty(), "Artist must not exist after delete");
    }

    @Test
    public void shouldDeleteArtistWithAlbums() throws SQLException {
        //Given
        Optional<Artist> artist = artistDAO.create(new Artist("A Name"));

        //When
        long artistId = artist.get().getArtistId();
        artistDAO.delete(artist.get());

        //Then
        assertTrue(artistDAO.findById(artistId).isEmpty(), "Artist must not exist after delete");
    }
}