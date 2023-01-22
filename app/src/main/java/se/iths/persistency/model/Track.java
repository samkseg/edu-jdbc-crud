package se.iths.persistency.model;

public class Track {
    long trackId;
    String name;
    long albumId;

    public Track (String name, long albumId) {
        this.name = name;
        this.albumId = albumId;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return trackId + ": " + name;
    }
}
