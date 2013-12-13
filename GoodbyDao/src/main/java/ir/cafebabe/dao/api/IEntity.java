package ir.cafebabe.dao.api;

import java.io.Serializable;

/**
 * All Entities must implement this interface
 * 
 * @author <a href="http://cafebabe.ir"> morteza adigozalpour </a>
 *         (Mortezaadi@gmail.com)
 * 
 * @param <I>
 *          Serializable Object as a Primery Key
 */
public interface IEntity<I extends Serializable> extends Serializable {

  String PRIMERY_KEY_NAME = "id";

  I getId();

  void setId(I id);
}
