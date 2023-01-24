# edu-jdbc-crud
Assignment for course in Database development

## Entity Relationship Diagram

```mermaid
erDiagram
    Artist ||--o{ Album : has
    Album ||--o{ Track : has

    Artist {
        int ArtistId
        varchar Name
    }
    
    Album {
        int AlbumId
        int ArtistId
        varchar Title

    }

    Track {
    	int TrackId
	int AlbumId
	varchar Name

    }

classDiagram
    Artist "1" o-- "0..*" Album : has
    Album "1" o-- "0..*" Track : has
    
    class Artist
    Artist : -long artistId
    Artist : -String name
    Artist : +getArtistId() long
    Artist : +getName() String
    Artist : +setArtistId(artistId)
    Artist : +setName(name)
    Artist : +getAlbums() Collection~Album~
    Artist : +add(album)
    Artist : +addAll(artistAlbums)
    Artist : +replace(album)
    Artist : +remove(album)
    Artist : +removeAll()
    
    class Album
    Album : -long albumId
    Album : -String title
    Album : -long artistId
    Album : +getAlbumId() long
    Album : +getTitle() String
    Album : +getTracks() Collection~Album~
    Album : +setAlbumId(albumId)
    Album : +setTitle(title)
    Album : +setArtistId(artistId)
    Album : +add(track)
    Album : +addAll(albumTracks)
    Album : +replace(track)
    Album : +remove(track)
    Album : +removeAll()

    class Track
    Track : -long trackId
    Track : -String name
    Track : -long albumId
    Track : +getTrackId() long
    Track : +getName() String
    Track : +getAlbumId() long
    Track : +setTrackId(trackId)
    Track : +setName(name)
    Track : +setAlbumId(albumId)
```

CRUD JDBC
1. Run db_auto.sql in your docker mysql container
2. gradle test
