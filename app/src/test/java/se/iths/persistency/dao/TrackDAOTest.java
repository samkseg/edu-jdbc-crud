package se.iths.persistency.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.iths.App;
import se.iths.persistency.model.Track;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrackDAOTest {
    static TrackDAO trackDAO = new TrackDAO();

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
        Optional<Track> track1 = trackDAO.findById(3510);
        trackDAO.delete(track1.get());
        Optional<Track> track2 = trackDAO.findById(3511);
        trackDAO.delete(track2.get());
        Optional<Track> track3 = trackDAO.findById(3512);
        trackDAO.delete(track3.get());
    }

    @Test
    public void shouldCreateTrack() throws SQLException {
        Track track = new Track("A title", 347L);

        Optional<Track> persistentTrack = trackDAO.create(track);

        assertNotNull( persistentTrack.get().getTrackId(), "Track id must not be null after create!");
        assertTrue(persistentTrack.get().getTrackId() >0, "Track id must be greater than 0 after create!");
    }

    @Test
    public void shouldNotCreateTrackWithoutAlbum(){
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            trackDAO.create(new Track("any title", 0L));
        });
    }


    @Test
    public void shouldFindAllTracks() throws SQLException {
        Collection<Track> tracks = trackDAO.findAll();

        assertNotNull(tracks, "Track must not be null after find all!");
        assertTrue(tracks.size() > 0 , "Track must exist after find all!");
    }

    @Test
    public void shouldFindAllTracksForArtist() throws SQLException {
        Long albumId = 347L;

        Collection<Track> tracks = trackDAO.findByAlbumId(albumId);

        assertNotNull(tracks, "Tracks must not be null after find all!");
        assertTrue(tracks.size() > 0 , "Tracks must exist after find all!");
    }

    @Test
    public void shouldFindTrackById() throws SQLException {
        Long existingId = 347L;
        Optional<Track> track1 = trackDAO.create(new Track("A title", existingId));

        Optional<Track> track = trackDAO.findById(track1.get().getTrackId());

        assertTrue(track.isPresent());
    }

    @Test
    public void shouldNotFindTrackByFaultyId() throws SQLException {
        Long nonExistingId = -1L;

        Optional<Track> track = trackDAO.findById(nonExistingId);

        assertTrue(track.isEmpty(), "Tracks must not be found with faulty id!");
    }

    @Test
    public void shouldUpdateTrack() throws SQLException {
        Long existingId = 347L;
        String oldTitle = "An old title";
        String newTitle = "A new title";
        Optional<Track> track = trackDAO.create(new Track(oldTitle, existingId));

        track.get().setName(newTitle);
        track = trackDAO.update(track.get());

        assertEquals(newTitle, track.get().getName());
    }

    @Test
    public void shouldDeleteTrack() throws SQLException {
        Long existingId = 347L;
        Optional<Track> track = trackDAO.create(new Track("A title", existingId));

        trackDAO.delete(track.get());

        assertTrue(trackDAO.findById(track.get().getTrackId()).isEmpty(), "Track must not exist after delete");
    }
}
