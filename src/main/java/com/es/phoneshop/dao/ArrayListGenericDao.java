package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ArrayListGenericDao<T extends Item> implements GenericDao<T>{
    protected List<T> itemList;
    protected long itemId;
    protected final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    protected ArrayListGenericDao() {
        itemList = new ArrayList<>();
    }

    protected void setItemList(List<T> itemList) {
        this.itemList = itemList;
    }

    @Override
    public T getItem(Long id) throws ItemNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return itemList.stream()
                    .filter((item) -> id.equals(item.getId()))
                    .findAny()
                    .orElseThrow(ItemNotFoundException::new);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(T item) throws ItemNotFoundException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            Long id = item.getId();
            if (id != null) {
                itemList.remove(getItem(id));
                itemList.add(item);
            }
            else {
                item.setId(++itemId);
                itemList.add(item);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Long id) throws ItemNotFoundException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            T item = getItem(id);
            itemList.remove(item);
        } finally {
            writeLock.unlock();
        }
    }
}
