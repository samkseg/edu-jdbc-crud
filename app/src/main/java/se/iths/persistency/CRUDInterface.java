package se.iths.persistency;

import se.iths.persistency.model.Album;
import se.iths.persistency.model.Artist;

import java.util.Collection;
import java.util.Optional;

public interface CRUDInterface<T> {
    public Collection<T> findAll() throws Exception;
    public Optional<T> findById(long id) throws Exception;
    public Optional<T> create(T object) throws Exception;
    public Optional<T> update(T object) throws Exception;
    public boolean delete( T object) throws Exception;
}
