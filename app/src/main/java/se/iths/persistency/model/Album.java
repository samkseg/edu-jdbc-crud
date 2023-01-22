package se.iths.persistency.model;

import java.util.Collection;
import java.util.HashMap;

public class Album {
    Long albumId;
    String title;
    Long artistId;
    HashMap <Long, Track> tracks = new HashMap<>();

    public Album(String title, Long artistId) {
        this.title = title;
        this.artistId = artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Track> getTracks() {
        return tracks.values();
    }

    public Track getTrack (long trackId){
        return tracks.get(trackId);
    }

    public void add(Track track) {
        tracks = tracks==null?new HashMap<Long, Track>():tracks;
        tracks.put(track.trackId, track);
    }

    public void addAll(Collection<Track> albumTracks) {
        tracks = new HashMap<Long, Track>();
        albumTracks.forEach(track -> tracks.put(track.trackId, track));
    }

    public void replace(Track track) {
        tracks.replace(track.trackId, track);
    }

    public void remove(Track track) {
        if (tracks==null) return;
        tracks.remove(track.getTrackId());
    }

    public void removeAll() {
        if (tracks==null) return;
        tracks.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.valueOf(albumId));
        sb.append(": ");
        sb.append(title);
        if (!tracks.isEmpty()) {
            sb.append("\n\t\tTracks:\n");
            for(Track track : tracks.values()) {
                sb.append("\t\t\t");
                sb.append(track);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
