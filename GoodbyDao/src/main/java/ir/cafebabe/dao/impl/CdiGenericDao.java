package ir.cafebabe.dao.impl;

import ir.cafebabe.dao.api.IEntity;

import java.io.Serializable;

/**
 * This class allows you to create DAO without making concrete DAO.
 * this class is final and can't be extend for creating custom DAO use {@link AbstractCdiDao}
 * <pre>
 * {@code 
 * 		IDao<Order, Long> orderDao = new CdiGenericDao<Order, Long>(Order.class);
 * }
 * </pre>
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a> (Mortezaadi@gmail.com)
 * 
 * @param <E> Entity extend IEntity
 * @param <I> Serialized Object as a primary key
 */
public final class CdiGenericDao<E extends IEntity<I>, I extends Serializable>
		extends AbstractCdiDao<E, I> {

	public CdiGenericDao(Class<? extends IEntity<I>> clazz) {
		super(clazz);
	}

}
