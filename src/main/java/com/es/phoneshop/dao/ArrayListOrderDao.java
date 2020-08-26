package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.order.Order;

import java.util.concurrent.locks.Lock;

public class ArrayListOrderDao extends ArrayListGenericDao<Order> implements OrderDao {

    private static class SingletonHelper {
        private static final ArrayListOrderDao INSTANCE = new ArrayListOrderDao();
    }
    public static ArrayListOrderDao getInstance() {
        return ArrayListOrderDao.SingletonHelper.INSTANCE;
    }

    @Override
    public Order getOrderBySecureId(String secureId) throws ItemNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return itemList.stream()
                    .filter((item) -> secureId.equals(item.getSecureId()))
                    .findAny()
                    .orElseThrow(ItemNotFoundException::new);
        } finally {
            readLock.unlock();
        }
    }
}
