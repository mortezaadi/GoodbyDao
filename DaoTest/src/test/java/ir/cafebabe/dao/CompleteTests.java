package ir.cafebabe.dao;

import ir.cafebabe.dao.api.IDao;
import ir.cafebabe.dao.cons.OrderType;
import ir.cafebabe.dao.sample.Item;
import ir.cafebabe.dao.sample.Order;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

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
        itemDao.save(tv);
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

        List<Order> all_asc = orderDao.getAll("customer", null);
        List<Order> all_desc = orderDao.getAll("customer", OrderType.DESC);
        Assert.assertEquals(all_asc.get(0), all_desc.get(2));
        Assert.assertEquals(all_asc.get(1), all_desc.get(1));
        Assert.assertEquals(all_asc.get(2), all_desc.get(0));

        List<Order> all3 = orderDao.getAll("customer", null, o2.getId(),
                o3.getId(), o1.getId());
        List<Order> all4 = orderDao.getAll("customer", OrderType.DESC,
                o2.getId(), o3.getId(), o1.getId());
        Assert.assertArrayEquals(all_asc.toArray(), all3.toArray());
        Assert.assertArrayEquals(all_desc.toArray(), all4.toArray());

        List<Order> p1_asc = orderDao.getAll("customer", null, 1, 1);
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
    public void testDeleteWithUnsavedEntity() {
        // initialize
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(tv);
        Assert.assertEquals(1, itemDao.getAll().size());
        // end init
        itemDao.delete(tv, mobile);
        Assert.assertEquals(0, itemDao.getAll().size());
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

        Item mobile2 = new Item();
        mobile2.setId(mobile.getId());
        mobile2.setPrice(470D);
        mobile2.setProduct("LG");
        itemDao.updateOne(mobile2, "price", "product");
        itemDao.flushAndClear();

        Assert.assertEquals(2, itemDao.getAll().size());
        Item item = itemDao.get(mobile.getId());
        Assert.assertEquals(item.getPrice(), 470D, 2);
        Assert.assertEquals(item.getProduct(), mobile2.getProduct());
    }

    @Test(expected = Exception.class)
    @Transactional
    public void testGetAllInvalidArgument() {
        orderDao.getAll("customer", OrderType.ASC, null);
    }

    @Test(expected = RuntimeException.class)
    @Transactional
    public void testUpateOneInvalidArgument() {
        orderDao.updateOne(new Order(), null);
    }

    @Test
    @Transactional
    public void testUpateOneWithoutProperties() {
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(tv, mobile);
        tv.setPrice(1010D);
        itemDao.updateOne(tv, new String[] {});
        Item item = itemDao.get(tv.getId());
        Assert.assertEquals(tv.getPrice(), item.getPrice(), 0);
    }

    @Test(expected = RuntimeException.class)
    @Transactional
    public void testUpateOneWithCollectionProperties() {
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
        o.setItems(Arrays.asList(mobile, tv));
        orderDao.save(o);
        Assert.assertNotNull(o.getId());
        orderDao.flushAndClear();

        Item tablet = new Item();
        tablet.setProduct("Tablet");
        itemDao.save(tablet);
        o.setItems(Arrays.asList(tablet));

        orderDao.updateOne(o, "items");
    }

    @Test(expected = IllegalArgumentException.class)
    @Transactional
    public void testInvalidProperty() {
        orderDao.getAll("XXX", OrderType.ASC);
    }

    @Test(expected = EntityNotFoundException.class)
    @Transactional
    public void testLaodException() {
        itemDao.load(100L);
    }

    @Test
    @Transactional
    public void testLaod() {
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(tv, mobile);
        Item load = itemDao.load(tv.getId());
        Assert.assertEquals(tv.getProduct(), load.getProduct());
    }

    @Test(expected = IllegalArgumentException.class)
    @Transactional
    public void testInvalidPageNumber() {
        orderDao.getAll("customer", OrderType.ASC, 0, 0);
    }

    @Test
    @Transactional
    public void testSaveList() {
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(Arrays.asList(tv, mobile));
        Assert.assertNotNull(tv.getId());
        Assert.assertNotNull(mobile.getId());
    }

    @Test
    @Transactional
    public void testDeleteByIDs() {
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(tv, mobile);
        orderDao.deleteByIDs(tv.getId(), mobile.getId());
        List<Order> all = orderDao.getAll();
        Assert.assertEquals(0, all.size());
    }

    @Test
    @Transactional
    public void testCount() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(2010D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(tv, mobile, tablet);
        Number count = itemDao.count();
        Assert.assertEquals(3L, count);
    }

    @Test
    @Transactional
    public void testCountDistinct() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(2010D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(200D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(370D);
        itemDao.save(tv, mobile, tablet);
        Number count = itemDao.count("product", true);
        Assert.assertEquals(2L, count);
    }

    @Test
    @Transactional
    public void testAverage() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(300D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(300D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(300D);
        itemDao.save(tv, mobile, tablet);
        itemDao.flushAndClear();
        Number count = itemDao.average("price");
        Assert.assertEquals(300D, count);
    }

    @Test
    @Transactional
    public void testSum() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(300D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(300D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(300D);
        itemDao.save(tv, mobile, tablet);
        itemDao.flushAndClear();
        Number sum = itemDao.sum("price");
        Assert.assertEquals(900D, sum);
    }

    @Test
    @Transactional
    public void testMax() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(300D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(400D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(800D);
        itemDao.save(tv, mobile, tablet);
        itemDao.flushAndClear();
        Number max = itemDao.max("price");
        Assert.assertEquals(800D, max);
    }

    @Test
    @Transactional
    public void testMin() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(300D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(400D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(800D);
        itemDao.save(tv, mobile, tablet);
        itemDao.flushAndClear();
        Number min = itemDao.min("price");
        Assert.assertEquals(300D, min);
    }

    @Test
    @Transactional
    public void testByExample() {
        Item tablet = new Item();
        tablet.setProduct("Samsung");
        tablet.setPrice(300D);
        Item mobile = new Item();
        mobile.setProduct("Samsung");
        mobile.setPrice(400D);
        Item tv = new Item();
        tv.setProduct("Sony");
        tv.setPrice(800D);
        itemDao.save(tv, mobile, tablet);
        itemDao.flushAndClear();
        Item example = new Item();
        example.setPrice(400D);
        List<Item> findByExample = itemDao.findByExample(example, true);
        Assert.assertEquals("Samsung", findByExample.iterator().next()
                .getProduct());
        example.setPrice(0D);
        example.setProduct("Samsung");
        findByExample = itemDao.findByExample(example, true);
        Assert.assertEquals(2, findByExample.size());

    }
}