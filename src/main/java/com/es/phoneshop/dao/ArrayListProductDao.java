package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.model.product.Product;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {

    private List<Product> productList;
    private long productId;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private ArrayListProductDao() {
        productList = new ArrayList<>();
    }

    private static class SingletonHelper {
        private static final ArrayListProductDao INSTANCE = new ArrayListProductDao();
    }

    public static ArrayListProductDao getInstance() {
        return SingletonHelper.INSTANCE;
    }

    protected void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private long countWordsAmount(String queryProduct, Product product) {
        if (StringUtils.isBlank(queryProduct)) {
            return 0;
        } else {
            return Arrays.stream(queryProduct.split(" "))
                    .filter(product.getDescription()::contains)
                    .count();
        }
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return productList.stream()
                    .filter((product) -> id.equals(product.getId()))
                    .findAny()
                    .orElseThrow(ProductNotFoundException::new);
        } finally {
            readLock.unlock();
        }
    }

    private Comparator<Product> getComparator(String queryProduct, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator;
        if (sortField == SortField.DESCRIPTION) {
            comparator = Comparator.comparing(Product::getDescription);
        } else if (sortField == SortField.PRICE) {
            comparator = Comparator.comparing(Product::getPrice);
        } else {
            comparator = Comparator.comparing(product -> countWordsAmount(queryProduct, product), Comparator.reverseOrder());
        }
        if (sortOrder == SortOrder.DESC) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    @Override
    public List<Product> findProducts(String queryProduct, SortField sortField, SortOrder sortOrder) {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return productList.stream()
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getStock() > 0)
                    .filter(product -> StringUtils.isBlank(queryProduct) || countWordsAmount(queryProduct, product) > 0)
                    .sorted(getComparator(queryProduct, sortField, sortOrder))
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Product product) throws ProductNotFoundException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            Long id = product.getId();
            if (id != null) {
                productList.remove(getProduct(id));
                productList.add(product);
            }
            else {
                product.setId(++productId);
                productList.add(product);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Long id) throws ProductNotFoundException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            Product product = getProduct(id);
            productList.remove(product);
        } finally {
            writeLock.unlock();
        }
    }

}