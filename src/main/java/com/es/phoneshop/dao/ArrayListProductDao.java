package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class ArrayListProductDao extends ArrayListGenericDao<Product> implements ProductDao {

    private static class SingletonHelper {
        private static final ArrayListProductDao INSTANCE = new ArrayListProductDao();
    }

    public static ArrayListProductDao getInstance() {
        return SingletonHelper.INSTANCE;
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
            return itemList.stream()
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
    public void delete(Long id) throws ItemNotFoundException {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            Product product = getItem(id);
            itemList.remove(product);
        } finally {
            writeLock.unlock();
        }
    }
}