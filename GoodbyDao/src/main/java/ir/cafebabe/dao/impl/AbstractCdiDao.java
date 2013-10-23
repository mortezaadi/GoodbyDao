package ir.cafebabe.dao.impl;

import ir.cafebabe.dao.api.IEntity;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Implementation supporting Contexts and Dependency Injection.
 * 
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a> (Mortezaadi@gmail.com)
 * 
 * @param <E> entity type, it must implements at least <code>IEntity</code>
 * @param <I> entity's primary key, it must be serializable
 * 
 * @see IEntity
 * 
 */
public abstract class AbstractCdiDao<E extends IEntity<I>, I extends Serializable> extends AbstractDao<E, I> {

	public AbstractCdiDao() {
		super();
	}
	
	public AbstractCdiDao(Class<? extends IEntity<I>> clazz) {
		super(clazz);
	}
  /**
   * Set entity manager.
   * 
   * @param entityManager entity manager
   */
  @Override
  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    super.setEntityManager(entityManager);
  }

}
