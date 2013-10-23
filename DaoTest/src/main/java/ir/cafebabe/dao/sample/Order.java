package ir.cafebabe.dao.sample;

import java.util.Collection;

import ir.cafebabe.dao.api.IEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Tbl_Order")
public class Order implements IEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;

	private String customer;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Collection<Item> items;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Collection<Item> getItems() {
		return items;
	}

	public void setItems(Collection<Item> items) {
		this.items = items;
	}
}
