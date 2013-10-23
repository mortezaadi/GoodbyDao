package ir.cafebabe.dao.sample;

import java.util.logging.Level;
import java.util.logging.Logger;

import ir.cafebabe.dao.impl.AbstractCdiDao;

public class ItemDao extends AbstractCdiDao<Item, Long> {
	
	public void additionalDaoOperation() {
		Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, "Operation get executed");
		Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, entityManager.toString());
	}
}
