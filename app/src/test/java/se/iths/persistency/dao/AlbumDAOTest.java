package se.iths.persistency.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.iths.App;
import se.iths.persistency.model.Album;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumDAOTest {
    static AlbumDAO albumDAO = new AlbumDAO();
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
        Optional<Album> album1 = albumDAO.findById(354);
        albumDAO.delete(album1.get());
        Optional<Album> album2 = albumDAO.findById(356);
        albumDAO.delete(album2.get());
        Optional<Album> album3 = albumDAO.findById(357);
        albumDAO.delete(album3.get());
    }

    @Test
    public void shouldCreateAlbum() throws SQLException {
        //Given
        Album album = new Album("A title", 275L);

        //When
        Optional<Album> persistentAlbumm = albumDAO.create(album);

        //Then
        assertNotNull( persistentAlbumm.get().getAlbumId(), "Album id must not be null after create!");
        assertTrue(persistentAlbumm.get().getAlbumId() >0, "Album id must be greater than 0 after create!");
    }

    @Test
    public void shouldNotCreateAlbumWithoutArtist(){
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            albumDAO.create(new Album("any title", 0L));
        });
    }


    @Test
    public void shouldFindAllAlbums() throws SQLException {
        //Given

        //When
        Collection<Album> albums = albumDAO.findAll();
        //Then
        assertNotNull(albums, "Albums must not be null after find all!");
        assertTrue(albums.size() > 0 , "Albums must exist after find all!");
    }

    @Test
    public void shouldFindAllAlbumsForArtist() throws SQLException {
        //Given
        Long artistId = 275L;

        //When
        Collection<Album> albums = albumDAO.findByArtistId(artistId);
        //Then
        assertNotNull(albums, "Albums must not be null after find all!");
        assertTrue(albums.size() > 0 , "Albums must exist after find all!");
    }

    @Test
    public void shouldFindAlbumById() throws SQLException {
        //Given
        Long existingId = 275L;
        Optional<Album> album1 = albumDAO.create(new Album("A title", existingId));

        //When
        Optional<Album> album = albumDAO.findById(album1.get().getAlbumId());

        //Then
        assertTrue(album.isPresent());
    }

    @Test
    public void shouldNotFindAlbumByFaultyId() throws SQLException {
        //Given
        Long nonExistingId = -1L;

        //When
        Optional<Album> album = albumDAO.findById(nonExistingId);

        //Then
        assertTrue(album.isEmpty(), "Albums must not be found with faulty id!");
    }

    @Test
    public void shouldUpdateAlbum() throws SQLException {
        //Given
        Long existingId = 275L;
        String oldTitle = "An old title";
        String newTitle = "A new title";
        Optional<Album> album = albumDAO.create(new Album(oldTitle, existingId));

        //When
        album.get().setTitle(newTitle);
        album = albumDAO.update(album.get());

        //Then
        assertEquals(newTitle, album.get().getTitle());
    }

    @Test
    public void shouldDeleteAlbum() throws SQLException {
        //Given
        Long existingId = 275L;
        Optional<Album> album = albumDAO.create(new Album("A title", existingId));

        //When
        albumDAO.delete(album.get());

        //Then
        assertTrue(albumDAO.findById(album.get().getAlbumId()).isEmpty(), "Album must not exist after delete");
    }
}
