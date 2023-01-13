package se.iths.persistency.model;

public class Album {
    Long albumId;
    String title;

    Long artistId;

    public Album(Long albumId, String title, Long artistId) {
        this.albumId = albumId;
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
}
