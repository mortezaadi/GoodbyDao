package ir.cafebabe.dao.impl;

import ir.cafebabe.dao.api.IEntity;

import java.io.Serializable;

/**
 * This class allows you to create DAO without making concrete DAO. this class
 * is final and can't be extend for creating custom DAO use
 * {@link AbstractCdiDao}
 * 
 * <pre>
 * 
 * {
 *   &#064;code
 *   IDao&lt;Order, Long&gt; orderDao = new CdiGenericDao&lt;Order, Long&gt;(Order.class);
 * }
 * </pre>
 * 
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a>
 *         (Mortezaadi@gmail.com)
 * 
 * @param <E>
 *          Entity extend IEntity
 * @param <I>
 *          Serialized Object as a primary key
 */
public final class CdiGenericDao<E extends IEntity<I>, I extends Serializable>
        extends AbstractCdiDao<E, I> {

  public CdiGenericDao(final Class< ? extends IEntity<I>> clazz) {
    super(clazz);
  }

}
