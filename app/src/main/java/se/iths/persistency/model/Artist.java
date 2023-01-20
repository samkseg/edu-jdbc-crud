package se.iths.persistency.model;

import java.util.Collection;
import java.util.HashMap;

public class Artist {
    Long artistId;
    String name;
    HashMap<Long, Album> albums = new HashMap<>();

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
        return albums.values();
    }

    public Album getAlbum (long albumId){
        return albums.get(albumId);
    }

    public void add(Album album) {
        albums = albums==null?new HashMap<Long, Album>():albums;
        albums.put(album.albumId, album);
    }

    public void addAll(Collection<Album> artistAlbums) {
        albums = new HashMap<Long, Album>();
        artistAlbums.forEach(album -> albums.put(album.albumId, album));
    }

    public void replace(Album album) {
        albums.replace(album.albumId, album);
    }

    public void remove(Album album) {
        if (albums==null) return;
        albums.remove(album.getAlbumId());
    }

    public void removeAll() {
        if (albums==null) return;
        albums.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.valueOf(artistId));
        sb.append(": ");
        sb.append(name);
        if (!albums.isEmpty()) {
            sb.append("\nAlbums:\n");
            for(Album album : albums.values()) {
                sb.append("\t");
                sb.append(album);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
