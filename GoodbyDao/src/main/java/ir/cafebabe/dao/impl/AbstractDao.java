package ir.cafebabe.dao.impl;

import ir.cafebabe.dao.api.IDao;
import ir.cafebabe.dao.api.IEntity;
import ir.cafebabe.dao.cons.OrderType;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.Query;

/**
 * Abstract implementation of generic DAO.
 * 
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a>
 *         (Mortezaadi@gmail.com)
 * 
 * @param <E>
 *          entity type, it must implements {@link IEntity}
 * @param <I>
 *          entity's primary key, it must be serializable
 * 
 * @see IEntity
 */

public abstract class AbstractDao<E extends IEntity<I>, I extends Serializable>
        implements IDao<E, I> {

  protected EntityManager entityManager;

  protected Class< ? extends IEntity<I>> clazz;

  /**
   * this constructor is used when extending {@link AbstractDao}
   **/
  @SuppressWarnings(value = "unchecked")
  protected AbstractDao() {

    Type[] types = ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments();

    if (types[0] instanceof ParameterizedType) {
      // If the class has parameterized types, it takes the raw type.
      ParameterizedType type = (ParameterizedType) types[0];
      clazz = (Class<IEntity<I>>) type.getRawType();
    } else {
      clazz = (Class<IEntity<I>>) types[0];
    }
  }

  /**
   * Constructor with given {@link IEntity} implementation. Use for creating DAO
   * without extending this class.
   * 
   * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a>
   *         (Mortezaadi@gmail.com)
   * 
   * @param clazz
   *          class with will be accessed by DAO methods
   **/
  public AbstractDao(final Class< ? extends IEntity<I>> entityClass) {
    this.clazz = entityClass;
  }

  @Override
  public void deleteByID(final I id) {
    Query q = entityManager.createQuery("DELETE FROM " + clazz.getName()
            + " e WHERE e." + IEntity.PRIMERY_KEY_NAME + " = :id");
    q.setParameter("id", id);
    q.executeUpdate();
  }

  @Override
  public void deleteByIDs(@SuppressWarnings("unchecked") final I... ids) {
    delete(Arrays.asList(ids));
  }

  private void delete(final List<I> ids) {
    Query q = entityManager.createQuery("DELETE FROM " + clazz.getName()
            + " e WHERE e." + IEntity.PRIMERY_KEY_NAME + " IN :ids");
    q.setParameter("ids", ids);
    q.executeUpdate();
  }

  @Override
  public void delete(final E object) {
    entityManager.remove(object);
  }

  @Override
  public void delete(@SuppressWarnings("unchecked") final E... objects) {
    List<I> ids = new ArrayList<I>();
    for (E o : objects) {
      IEntity<I> obj = (IEntity<I>) o;
      if (obj.getId() != null) {
        ids.add(obj.getId());
      }
    }
    delete(ids);
  }

  @Override
  public void deleteAll() {
    Query q = entityManager.createQuery("DELETE FROM " + clazz.getName()
            + " e ");
    q.executeUpdate();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<E> findByExample(final E example, boolean ignoreZeroOrEmpty) {
    Map<String, Object> sample = getSample(example, ignoreZeroOrEmpty);
    // for performance reason its better to return as soon as possible
    if (sample.size() < 1) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT e FROM " + clazz.getName() + " e WHERE ");
    Iterator<Entry<String, Object>> iterator = sample.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry<String, Object> next = iterator.next();
      sb.append("e.");
      sb.append(next.getKey());
      sb.append(" = :");
      sb.append(next.getKey());
      if (iterator.hasNext()) {
        sb.append(" AND ");
      }
    }
    Query q = entityManager.createQuery(sb.toString());
    for (Entry<String, Object> entry : sample.entrySet()) {
      q.setParameter(entry.getKey(), entry.getValue());
    }
    return q.getResultList();
  }

  private Map<String, Object> getSample(final E example, boolean ignoreEmpty) {
    Field[] fields = example.getClass().getDeclaredFields();
    Map<String, Object> sample = new HashMap<String, Object>();

    for (Field field : fields) {

      if (field.getName().equals("serialVersionUID")) {
        continue;
      }

      if (isAnnotationPresent(field, Id.class)) {
        continue;
      }

      Object value = null;

      try {
        field.setAccessible(true);
        value = field.get(example);
      } catch (IllegalArgumentException e) {
        continue;
      } catch (IllegalAccessException e) {
        continue;
      }

      if (value == null) {
        continue;
      }

      if (value instanceof Collection) {
        continue;
      }

      if (ignoreEmpty) {
        if (value instanceof String && "".equals(value)) {
          continue;
        }
        if (value.toString().equals("0") || value.toString().equals("0.0")) {
          continue;
        }
      }
      sample.put(field.getName(), value);
    }
    return sample;
  }

  @Override
  public void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }

  @SuppressWarnings(value = "unchecked")
  @Override
  public E get(final I id) {
    return (E) entityManager.find(clazz, id);
  }

  @SuppressWarnings(value = "unchecked")
  @Override
  public List<E> getAll(final I... ids) {
    Query q = entityManager.createQuery("SELECT e FROM " + clazz.getName()
            + " e WHERE e." + IEntity.PRIMERY_KEY_NAME + " IN :ids ");
    q.setParameter("ids", Arrays.asList(ids));
    return q.getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<E> getAll(String orderBy, OrderType order, I... ids) {
    isValidField(orderBy);
    if (ids == null || ids.length == 0)
      throw new IllegalArgumentException("Invalid Ids: "
              + Arrays.toString(ids));
    if (order == null)
      order = OrderType.ASC;
    String qString = "SELECT e FROM " + clazz.getName()
            + " e WHERE e.id IN :ids ORDER BY e." + orderBy + " "
            + order.toString();
    Query q = entityManager.createQuery(qString);
    q.setParameter("ids", Arrays.asList(ids));
    return q.getResultList();
  }

  @SuppressWarnings(value = "unchecked")
  @Override
  public List<E> getAll() {
    Query q = entityManager.createQuery("SELECT e FROM " + clazz.getName()
            + " e");
    return q.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<E> getAll(int pageNo, int pageSize) {
    validNumber(pageNo);
    validNumber(pageSize);
    Query q = entityManager.createQuery("SELECT e FROM " + clazz.getName()
            + " e");
    q.setMaxResults(pageSize);
    q.setFirstResult((pageNo - 1) * pageSize);
    return q.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<E> getAll(String orderBy, OrderType order) {
    isValidField(orderBy);
    if (order == null)
      order = OrderType.ASC;
    String qString = "SELECT e FROM " + clazz.getName() + " e ORDER BY e."
            + orderBy + " " + order.toString();
    Query q = entityManager.createQuery(qString);
    return q.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<E> getAll(String orderBy, OrderType order, int pageNo,
          int pageSize) {
    isValidField(orderBy);
    validNumber(pageNo);
    validNumber(pageSize);
    if (order == null)
      order = OrderType.ASC;
    String qString = "SELECT e FROM " + clazz.getName() + " e ORDER BY e."
            + orderBy + " " + order.toString();
    Query q = entityManager.createQuery(qString);
    q.setMaxResults(pageSize);
    q.setFirstResult((pageNo - 1) * pageSize);
    return q.getResultList();
  }

  /**
   * Get entity manager.
   * 
   * @return entity manager
   */
  protected EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public E load(I id) throws EntityNotFoundException {
    E entity = get(id);
    if (entity == null) {
      throw new EntityNotFoundException("entity " + clazz + "#" + id
              + " was not found");
    }
    return entity;
  }

  @Override
  public void refresh(final E entity) {
    entityManager.refresh(entity);
  }

  @Override
  public E save(final E object) {
    if (object.getId() != null) {
      return entityManager.merge(object);
    } else {
      entityManager.persist(object);
      return object;
    }
  }

  @Override
  public void save(@SuppressWarnings("unchecked") final E... objects) {
    for (E object : objects) {
      save(object);
    }
  }

  @Override
  public void save(List<E> objects) {
    for (E object : objects) {
      save(object);
    }
  }

  /**
   * Set entity manager.
   * 
   * @param entityManager
   *          entity manager
   */
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  private void validNumber(int number) {
    if (number < 1)
      throw new IllegalArgumentException("Page number or size cannot be : "
              + number);
  }

  private void isValidField(String propertyName) {
    try {
      clazz.getDeclaredField(propertyName);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);

    }

  }

  private boolean isAnnotationPresent(Field field,
          Class< ? extends Annotation> cl) {
    if (field.isAnnotationPresent(cl))
      return true;
    return false;
  }

  /**
   * Only update specified properties of the Object
   * 
   * @param object
   *          An dirty object.
   * @param properties
   *          array of property names. for now Collection properties in not
   *          supported.
   */
  @Override
  public void updateOne(E object, String... properties) {
    if (object.getId() == null) {
      throw new RuntimeException("Not a Persisted entity");
    }
    if (properties == null || properties.length == 0) {
      entityManager.merge(object);
      return;
    }

    // for performance reason its better to mix getting fields, their values
    // and making query all in one loop
    // in one iteration
    StringBuilder sb = new StringBuilder();
    sb.append("Update " + clazz.getName() + " SET ");

    // cache of fieldName --> value
    Map<String, Object> cache = new HashMap<String, Object>();

    for (String prop : properties) {
      try {
        Field field = object.getClass().getDeclaredField(prop);
        field.setAccessible(true);
        Object value = field.get(object);
        if (value instanceof Collection) {
          // value = new LinkedList<>((Collection< ? extends Object>) value);
          throw new RuntimeException("Collection property is not suppotred.");
        }
        cache.put(prop, value);

        // ignore first comma
        if (cache.size() > 1) {
          sb.append(" ,");
        }
        sb.append(prop);
        sb.append(" = :");
        sb.append(prop);

      } catch (Exception e) { // TODO: use fine grain exceptions
                              // FIX: NEXT RELEASE I hope :)
        throw new RuntimeException(e);
      }
    }

    // this means nothing will be updated so hitting db is unnecessary
    if (cache.size() == 0)
      return;

    sb.append(" WHERE id = " + object.getId());
    Query query = entityManager.createQuery(sb.toString());
    for (Entry<String, Object> entry : cache.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
    query.executeUpdate();
  }

  @Override
  public Number count() {
    return count(IEntity.PRIMERY_KEY_NAME, false);
  }

  @Override
  public Number count(String property, boolean distinct) {
    isValidField(property);
    String subQuery = distinct ? "COUNT (DISTINCT e." + property + ")"
            : "COUNT (e." + property + ")";
    Query q = entityManager.createQuery("SELECT " + subQuery + " FROM "
            + clazz.getName() + " e");
    Number result = (Number) q.getSingleResult();
    return result;
  }

  @Override
  public Number average(String property) {
    isValidField(property);
    String subQuery = "AVG(e." + property + ")";
    Query q = entityManager.createQuery("SELECT " + subQuery + " FROM "
            + clazz.getName() + " e");
    Number result = (Number) q.getSingleResult();
    return result;
  }
  @Override
  public Number sum(String property) {
    isValidField(property);
    String subQuery = "SUM(e." + property + ")";
    Query q = entityManager.createQuery("SELECT " + subQuery + " FROM "
            + clazz.getName() + " e");
    Number result = (Number) q.getSingleResult();
    return result;
  }

  @Override
  public Number max(String property) {
    isValidField(property);
    String subQuery = "MAX(e." + property + ")";
    Query q = entityManager.createQuery("SELECT " + subQuery + " FROM "
            + clazz.getName() + " e");
    Number result = (Number) q.getSingleResult();
    return result;
  }

  @Override
  public Number min(String property) {
    isValidField(property);
    String subQuery = "MIN(e." + property + ")";
    Query q = entityManager.createQuery("SELECT " + subQuery + " FROM "
            + clazz.getName() + " e");
    Number result = (Number) q.getSingleResult();
    return result;
  }

  // private String fieldToGetter(Field field) {
  // String name = field.getName();
  // return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
  // }
  //
  // private String fieldToSetter(Field field) {
  // String name = field.getName();
  // return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
  // }

}
