package ir.cafebabe.dao.impl;

import java.io.Serializable;

import ir.cafebabe.dao.api.IEntity;

/**
 * This class allows you to create DAO without making concrete DAO.
 * this class is final and can't be extend for creating custom DAO use {@link AbstractDao}
 * <pre>
 * {@code 
 * 		IDao<Order, Long> orderDao = new CdiGenericDao<Order, Long>(Order.class);
 * }
 * </pre>
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a> (Mortezaadi@gmail.com)
 * @param <E> Entity extend IEntity
 * @param <I> Serialized Object as a primary key
 */
public final class GenericDao<E extends IEntity<I>, I extends Serializable> extends AbstractDao<E,I> {

	
	public GenericDao(Class<? extends IEntity<I>> clazz) {
		super(clazz);
	}

}
