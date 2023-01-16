package se.iths.persistency;

import se.iths.persistency.model.Artist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface CRUDInterface<T> {
    public Collection<T> findAll() throws SQLException;
    public Optional<T> findById(long id) throws SQLException;
    public Optional<T> create(T object) throws SQLException;
    public Optional<T> update(T object) throws SQLException;
    public boolean delete( T object) throws Exception;
}
