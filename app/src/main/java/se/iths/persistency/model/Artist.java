package se.iths.persistency.model;

import java.util.ArrayList;
import java.util.Collection;

public class Artist {
    Long artistId;
    String name;
    Collection<Album> albums = new ArrayList<>();

    public Artist(String name) {
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
    public void addAll(Collection<Album> artistAlbums) {
        albums = albums==null?new ArrayList<Album>():albums;
        albums = artistAlbums;
    }

    public void remove(Album album) {
        if (albums==null) return;
        albums.remove(album);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.valueOf(artistId));
        sb.append(": ");
        sb.append(name);
        if (!albums.isEmpty()) {
            sb.append("\nAlbums:\n");
            for(Album album : albums) {
                sb.append("\t");
                sb.append(album);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public void removeAll() {
        if (albums==null) return;
        albums.clear();
    }
}
