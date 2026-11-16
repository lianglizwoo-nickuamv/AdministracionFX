package ni.edu.uam.administracionfx.dao;

import java.util.List;

public interface IDao {

    <T> List<T> getAll(String namedQuery, Class<T> clazz);
    <T> List<T> getByQuery(String namedQuery, Class<T> clazz, Object... params);
    <T> void insert(T entity);
    <T> T update(T entity);
    <T> void remove(T entity);
    <T> T findById(Class<T> clazz, Object id);

}