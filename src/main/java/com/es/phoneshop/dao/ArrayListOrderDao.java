package com.es.phoneshop.dao;

import com.es.phoneshop.model.order.Order;

public class ArrayListOrderDao extends ArrayListGenericDao<Order> implements OrderDao {

    private static class SingletonHelper {
        private static final ArrayListOrderDao INSTANCE = new ArrayListOrderDao();
    }
    public static ArrayListOrderDao getInstance() {
        return ArrayListOrderDao.SingletonHelper.INSTANCE;
    }

}
