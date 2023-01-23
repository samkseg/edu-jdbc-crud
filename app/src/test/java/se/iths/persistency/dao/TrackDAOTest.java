package se.iths.persistency.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.iths.App;
import se.iths.persistency.ConnectToDB;
import se.iths.persistency.model.Track;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrackDAOTest {
    private static Connection con = null;
    TrackDAO trackDAO = new TrackDAO();

    @BeforeAll
    public static void setUp() throws Exception {
        con = ConnectToDB.connect();
        App app = new App();
        app.load();
    }

    @Test
    public void shouldCreateTrack() throws SQLException {
        //Given
        Track track = new Track("A title", 347L);

        //When
        Optional<Track> persistentTrack = trackDAO.create(track);

        //Then
        assertNotNull( persistentTrack.get().getTrackId(), "Album id must not be null after create!");
        assertTrue(persistentTrack.get().getTrackId() >0, "Album id must be greater than 0 after create!");
    }

    @Test
    public void shouldNotCreateTrackWithoutAlbum(){
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            trackDAO.create(new Track("any title", 0L));
        });
    }


    @Test
    public void shouldFindAllTracks() throws SQLException {
        //Given

        //When
        Collection<Track> tracks = trackDAO.findAll();
        //Then
        assertNotNull(tracks, "Albums must not be null after find all!");
        assertTrue(tracks.size() > 0 , "Albums must exist after find all!");
    }

    @Test
    public void shouldFindAllTracksForArtist() throws SQLException {
        //Given
        Long albumId = 347L;

        //When
        Collection<Track> tracks = trackDAO.findByAlbumId(albumId);
        //Then
        assertNotNull(tracks, "Albums must not be null after find all!");
        assertTrue(tracks.size() > 0 , "Albums must exist after find all!");
    }

    @Test
    public void shouldFindTrackById() throws SQLException {
        //Given
        Long existingId = 347L;
        Optional<Track> track1 = trackDAO.create(new Track("A title", existingId));

        //When
        Optional<Track> track = trackDAO.findById(track1.get().getTrackId());

        //Then
        assertTrue(track.isPresent());
    }

    @Test
    public void shouldNotFindTrackByFaultyId() throws SQLException {
        //Given
        Long nonExistingId = -1L;

        //When
        Optional<Track> track = trackDAO.findById(nonExistingId);

        //Then
        assertTrue(track.isEmpty(), "Albums must not be found with faulty id!");
    }

    @Test
    public void shouldUpdateTrack() throws SQLException {
        //Given
        Long existingId = 347L;
        String oldTitle = "An old title";
        String newTitle = "A new title";
        Optional<Track> track = trackDAO.create(new Track(oldTitle, existingId));

        //When
        track.get().setName(newTitle);
        track = trackDAO.update(track.get());

        //Then
        assertEquals(newTitle, track.get().getName());
    }

    @Test
    public void shouldDeleteTrack() throws SQLException {
        //Given
        Long existingId = 347L;
        Optional<Track> track = trackDAO.create(new Track("A title", existingId));

        //When
        trackDAO.delete(track.get());

        //Then
        assertTrue(trackDAO.findById(track.get().getTrackId()).isEmpty(), "Album must not exist after delete");
    }
}
