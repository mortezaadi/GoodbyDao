package ir.cafebabe.dao.api;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public abstract class BaseDao<I extends Serializable> implements IEntity<I> {

	@Id @GeneratedValue
	private I id;

	public I getId() {
		return id;
	}

	public void setId(I id) {
		this.id = id;
	}
	
}
