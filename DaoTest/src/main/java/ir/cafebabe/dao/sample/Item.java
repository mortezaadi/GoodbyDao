package ir.cafebabe.dao.sample;

import ir.cafebabe.dao.api.IEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Tbl_Item")
public class Item implements IEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue

	private Long id;
	
	private String product;
	private double price;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
}
