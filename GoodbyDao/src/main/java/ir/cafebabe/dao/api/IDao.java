package ir.cafebabe.dao.api;

import ir.cafebabe.dao.cons.OrderType;

import java.io.Serializable;
import java.util.List;

/**
 * All DAO's Must Implement this interface
 * 
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a>
 *         (Mortezaadi@gmail.com)
 * 
 * @param <E>
 *          Entity extended IEntity
 * @param <I>
 *          Serializable Object as a PrimeryKey
 */
public interface IDao<E extends IEntity<I>, I extends Serializable> {

  /**
   * delete Object by its id.
   * 
   * @param id
   *          Serializable Object as a PrimeryKey
   * @throws UnsupportedOperationException
   *           if operation does not supported
   */
  void deleteByID(I id);

  /**
   * delete Objects by their ids.
   * 
   * @param ids
   *          Array of serializable Objects as a PrimeryKeys
   * @throws UnsupportedOperationException
   *           if operation does not supported
   */
  void deleteByIDs(@SuppressWarnings("unchecked") I... ids);

  /**
   * delete given {@link IEntity}
   * 
   * @param object
   *          Entity extended {@link IEntity}
   * @throws UnsupportedOperationException
   *           if operation does not supported
   */
  void delete(E object);

  /**
   * delete all given {@link IEntity}
   * 
   * @param objects
   *          Entities extended {@link IEntity}
   * @throws UnsupportedOperationException
   *           if operation does not supported
   */
  void delete(@SuppressWarnings("unchecked") E... objects);

  /**
   * delete all Object in database
   */
  void deleteAll();

  /**
   * Gets an Object as an example and fetches all object from database whose
   * property values are like the example object. query exclude ID value and
   * collections type. in next release Collection should be handled! P.S I hope!
   * :)
   * 
   * @param example
   *          sample object
   * @return List of Objects similar to provided example.
   */
  List<E> findByExample(E example);

  /**
   * Write to database and clear it.
   */
  void flushAndClear();

  /**
   * get an Object from database by its ID.
   * 
   * @param id
   *          Serializable Object as a PrimeryKey
   * @return returns Persisted Object. null if entity does not exsist
   * @see {@link #load(Serializable)}
   */
  E get(I id);

  /**
   * returns list of all T's by specified ID's
   * 
   * @param ids
   *          Serializable Objects as a PrimeryKeys
   * @return list of Objects
   */
  List<E> getAll(@SuppressWarnings("unchecked") I... ids);

  /**
   * get list of Objects from database by their ID's.
   * 
   * @param OrderBy
   *          property name of the entity used as a ORDER BY clause
   * @param order
   *          ASC or DESC {@link OrderType}
   * @param ids
   *          Serializable Objects as a PrimeryKeys
   * @return list of Objects
   */
  List<E> getAll(String orderBy, OrderType order, @SuppressWarnings("unchecked") I... ids);

  /**
   * get list of Objects from database.
   * 
   * @return list of Objects
   */
  List<E> getAll();

  /**
   * get list of Objects from database.
   * 
   * @param pageNo
   *          page number
   * @param pageSize
   *          size of the record per page
   * @return list of Objects
   */
  List<E> getAll(int pageNo, int pageSize);

  /**
   * get list of Objects from database.
   * 
   * @param OrderBy
   *          property name of the entity used as a ORDER BY clause
   * @param order
   *          ASC or DESC {@link OrderType}
   * @return list of Objects
   */
  List<E> getAll(String orderBy, OrderType order);

  /**
   * get list of Objects from database.
   * 
   * @param OrderBy
   *          property name of the entity used as a ORDER BY clause
   * @param order
   *          ASC or DESC {@link OrderType}
   * @param pageNo
   *          page number
   * @param pageSize
   *          size of the record per page
   * @return list of Objects
   */
  List<E> getAll(String orderBy, OrderType order, int pageNo, int pageSize);

  /**
   * fetch an Object from database.
   * 
   * @param id
   * @return Persisted Object * @throws EntityNotFoundException if entity not
   *         found.
   * @throws IllegalArgumentException
   *           if id is null.
   * @see {@link #get(Serializable)} and {@link #get(Serializable...))}
   */
  E load(I id);

  /**
   * Refresh a persistant object that may have changed in another
   * thread/transaction.
   * 
   * @param entity
   *          transient entity
   */
  void refresh(E entity);

  /**
   * save an Object extended {@link IEntity} to database. if Object's Id is not
   * null merges the entity.
   * 
   * @param object
   *          Object extended {@link IEntity}
   * @return persisted Object extended {@link IEntity}
   */
  E save(E object);

  /**
   * save Objects extended {@link IEntity} to database. if Object's Id is not
   * null merges the entity.
   * 
   * @param objects
   *          Objects extended {@link IEntity}
   */
  void save(@SuppressWarnings("unchecked") E... objects);

  /**
   * save List of Objects extended {@link IEntity} to database. if Object's Id
   * is not null merges the entity.
   * 
   * @param objects
   *          Objects extended {@link IEntity}
   */
  void save(List<E> objects);

  /**
   * Only update specified properties of the Object
   * 
   * @param object
   *          An dirty object.
   * @param properties
   *          array of property names.
   * @throws IllegalAccessException
   *           if access to field is restricted.
   * @throws IllegalArgumentException
   *           if null or invalid data passed in.
   */
  void updateOne(E object, String... properties) throws IllegalAccessException;

}
