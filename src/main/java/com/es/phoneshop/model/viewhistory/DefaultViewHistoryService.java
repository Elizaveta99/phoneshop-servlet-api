package com.es.phoneshop.model.viewhistory;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.web.ProductDetailsPageServlet;

import javax.servlet.http.HttpSession;

public class DefaultViewHistoryService implements ViewHistoryService {

    protected static final String VIEWHISTORY_SESSION_ATTRIBUTE = ProductDetailsPageServlet.class.getName() + ".viewhistory";

    private DefaultViewHistoryService() { }

    private static class SingletonHelper {
        private static final DefaultViewHistoryService INSTANCE = new DefaultViewHistoryService();
    }

    public static DefaultViewHistoryService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public synchronized ViewHistory getViewHistory(HttpSession session) {
        ViewHistory viewHistory = (ViewHistory) session.getAttribute(VIEWHISTORY_SESSION_ATTRIBUTE);
        if (viewHistory == null) {
            session.setAttribute(VIEWHISTORY_SESSION_ATTRIBUTE, viewHistory = makeNewViewHistory());
        }
        return viewHistory;
    }

    // for tests
    protected ViewHistory makeNewViewHistory() {
        return new ViewHistory();
    }

    @Override
    public synchronized void addProductToViewHistory(HttpSession session, Product product) {
        ViewHistory viewHistory = getViewHistory(session);
        viewHistory.getLastViewedProducts().remove(product);
        viewHistory.getLastViewedProducts().addFirst(product);
        if (viewHistory.getLastViewedProducts().size() > 3) {
            viewHistory.getLastViewedProducts().removeLast();
        }
    }
}
