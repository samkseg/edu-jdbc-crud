package se.iths.persistency.model;

import java.util.ArrayList;
import java.util.Collection;

public class Artist {
    Long artistId;
    String name;
    Collection<Album> albums;

    public Artist(Long artistId, String name) {
        this.artistId = artistId;
        this.name = name;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public Long getId() {
        return artistId;
    }

    public void setId(Long artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Album> getAlbums() {
        return albums;
    }

    public void add(Album album) {
        albums = albums==null?new ArrayList<Album>():albums;
        albums.add(album);
    }

    public void remove(Album album) {
        if (albums==null) return;
        albums.remove(album);
    }
}
