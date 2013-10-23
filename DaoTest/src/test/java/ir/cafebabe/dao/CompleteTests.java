package ir.cafebabe.dao;

import java.util.Arrays;
import java.util.List;

import ir.cafebabe.dao.api.IDao;
import ir.cafebabe.dao.cons.OrderType;
import ir.cafebabe.dao.sample.Item;
import ir.cafebabe.dao.sample.Order;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "CompleteTests-context.xml" })
@TransactionConfiguration(defaultRollback = true)
public class CompleteTests {

	@Resource(name = "itemDao")
	IDao<Item, Long> itemDao;
	@Resource(name = "orderDao")
	IDao<ir.cafebabe.dao.sample.Order, Long> orderDao;

	@Test
	@Transactional
	public void testSaveOne() {
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);

		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);

		itemDao.save(tv);
		Assert.assertNotNull(tv.getId());

		Order o = new Order();
		o.setCustomer("Customer1");
		o.setItems(Arrays.asList(new Item[] { mobile, tv }));
		orderDao.save(o);

		Assert.assertNotNull(o.getId());
	}

	@Test
	@Transactional
	public void testSaveMany() {
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);

		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);

		itemDao.save(tv, mobile);
		Assert.assertNotNull(tv.getId());
		Assert.assertNotNull(mobile.getId());

		Order o1 = new Order();
		o1.setCustomer("Customer1");
		o1.setItems(Arrays.asList(new Item[] { mobile, tv }));

		Order o2 = new Order();
		o2.setCustomer("Customer2");
		o2.setItems(Arrays.asList(new Item[] { mobile, tv }));
		orderDao.save(o1, o2);

		Assert.assertNotNull(o1.getId());
		Assert.assertNotNull(o2.getId());
	}

	@Test
	@Transactional
	public void testGet() {
		/* init */
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);

		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);

		itemDao.save(tv, mobile);

		Order o1 = new Order();
		o1.setCustomer("Customer1");
		o1.setItems(Arrays.asList(new Item[] { mobile, tv }));

		Order o2 = new Order();
		o2.setCustomer("Customer2");
		o2.setItems(Arrays.asList(new Item[] { mobile, tv }));

		Order o3 = new Order();
		o3.setCustomer("Customer3");
		o3.setItems(Arrays.asList(new Item[] { mobile, tv }));
		orderDao.save(o1, o2, o3);
		/* End of init */

		List<Item> all = itemDao.getAll();
		Assert.assertEquals(2, all.size());
		Order order = orderDao.get(o2.getId());
		Assert.assertEquals(order.getCustomer(), o2.getCustomer());

		List<Order> all2 = orderDao.getAll(o1.getId(), o3.getId());
		Assert.assertEquals(o1, all2.get(0));
		Assert.assertEquals(o3, all2.get(1));

		List<Order> page1 = orderDao.getAll(1, 1);
		Assert.assertEquals(1, page1.size());
		Assert.assertEquals(page1.get(0), o1);
		List<Order> page2 = orderDao.getAll(2, 1);
		Assert.assertEquals(1, page2.size());
		Assert.assertEquals(page2.get(0), o2);
		List<Order> page3 = orderDao.getAll(3, 1);
		Assert.assertEquals(1, page3.size());
		Assert.assertEquals(page3.get(0), o3);

		List<Order> all_asc = orderDao.getAll("customer", OrderType.ASC);
		List<Order> all_desc = orderDao.getAll("customer", OrderType.DESC);
		Assert.assertEquals(all_asc.get(0), all_desc.get(2));
		Assert.assertEquals(all_asc.get(1), all_desc.get(1));
		Assert.assertEquals(all_asc.get(2), all_desc.get(0));

		List<Order> all3 = orderDao.getAll("customer", OrderType.ASC,
				o2.getId(), o3.getId(), o1.getId());
		List<Order> all4 = orderDao.getAll("customer", OrderType.DESC,
				o2.getId(), o3.getId(), o1.getId());
		Assert.assertArrayEquals(all_asc.toArray(), all3.toArray());
		Assert.assertArrayEquals(all_desc.toArray(), all4.toArray());

		List<Order> p1_asc = orderDao.getAll("customer", OrderType.ASC, 1, 1);
		Assert.assertEquals(p1_asc.get(0), o1);
		List<Order> p2_asc = orderDao.getAll("customer", OrderType.ASC, 2, 1);
		Assert.assertEquals(p2_asc.get(0), o2);
		List<Order> p3_asc = orderDao.getAll("customer", OrderType.ASC, 3, 1);
		Assert.assertEquals(p3_asc.get(0), o3);

		List<Order> p1_desc = orderDao.getAll("customer", OrderType.DESC, 1, 1);
		Assert.assertEquals(p1_desc.get(0), o3);
		List<Order> p2_desc = orderDao.getAll("customer", OrderType.DESC, 2, 1);
		Assert.assertEquals(p2_desc.get(0), o2);
		List<Order> p3_desc = orderDao.getAll("customer", OrderType.DESC, 3, 1);
		Assert.assertEquals(p3_desc.get(0), o1);
	}

	@Test
	@Transactional
	public void testDeleteByID() {
		// initialize
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);
		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);
		itemDao.save(tv, mobile);
		Assert.assertEquals(2, itemDao.getAll().size());
		// end init

		itemDao.deleteByID(tv.getId());
		Assert.assertEquals(1, itemDao.getAll().size());
	}

	@Test
	@Transactional
	public void testDelete() {
		// initialize
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);
		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);
		itemDao.save(tv, mobile);
		Assert.assertEquals(2, itemDao.getAll().size());
		// end init

		itemDao.delete(tv);
		Assert.assertEquals(1, itemDao.getAll().size());
	}

	@Test
	@Transactional
	public void testDeleteVarParam() {
		// initialize
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);
		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);
		itemDao.save(tv, mobile);
		Assert.assertEquals(2, itemDao.getAll().size());
		// end init

		itemDao.delete(tv, mobile);
		Assert.assertEquals(0, itemDao.getAll().size());
	}

	@Test
	@Transactional
	public void testDeleteAll() {
		// initialize
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);
		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);
		itemDao.save(tv, mobile);
		Assert.assertEquals(2, itemDao.getAll().size());
		// end init

		itemDao.deleteAll();
		Assert.assertEquals(0, itemDao.getAll().size());
	}

	@Test
	@Transactional
	public void testUpdate() {
		// initialize
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);
		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);
		itemDao.save(tv, mobile);
		Assert.assertEquals(2, itemDao.getAll().size());
		// end init
		
		try {
			Item mobile2 = new Item();
			mobile2.setId(mobile.getId());
			mobile2.setPrice(470D);
			itemDao.updateOne(mobile2, "price");
			itemDao.flushAndClear();
		} catch (IllegalArgumentException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalAccessException e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(2, itemDao.getAll().size());
		Item item = itemDao.get(mobile.getId());
		Assert.assertEquals(item.getPrice(), 470D,2);
		Assert.assertEquals(item.getProduct(), mobile.getProduct());
	}

	@Test
	@Transactional
	public void testFindByExample() {
		// initialize
		Item mobile = new Item();
		mobile.setProduct("Samsung");
		mobile.setPrice(200D);
		Item tv = new Item();
		tv.setProduct("Sony");
		tv.setPrice(370D);
		itemDao.save(tv, mobile);
		Assert.assertEquals(2, itemDao.getAll().size());
		// end init

		Item mobile2 = new Item();
		mobile2.setProduct("Samsung");
		mobile2.setPrice(200D);
		List<Item> findByExample = itemDao.findByExample(mobile2);
		Assert.assertEquals(1,findByExample.size());
		Assert.assertEquals(mobile.getId(),findByExample.get(0).getId());
	}
}