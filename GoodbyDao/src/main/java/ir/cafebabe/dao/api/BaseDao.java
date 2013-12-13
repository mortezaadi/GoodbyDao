package ir.cafebabe.dao.api;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public abstract class BaseDao<I extends Serializable> implements IEntity<I> {

  @Id
  @GeneratedValue
  private I id;

  public final I getId() {
    return id;
  }

  public final void setId(final I entityId) {
    this.id = entityId;
  }

}
