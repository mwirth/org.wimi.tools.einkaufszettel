package org.wimi.tools.einkaufszettel.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.wimi.tools.einkaufszettel.client.ArticleService;
import org.wimi.tools.einkaufszettel.client.NotLoggedInException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ArticleServiceImpl extends RemoteServiceServlet implements
		ArticleService {

	private static final Logger LOG = Logger.getLogger(ArticleServiceImpl.class
			.getName());
	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	private Object dummyObject;

	public void addArticle(String symbol) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
//			pm.makePersistent(new Article(getUser(), symbol));
			pm.makePersistent(new Article(symbol));
		} finally {
			pm.close();
		}
	}

	public void removeArticle(String symbol) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			long deleteCount = 0;
			Query q = pm.newQuery(Article.class);
//			q.declareParameters("com.google.appengine.api.users.User u");
			List<Article> stocks = (List<Article>) q.execute();
//			List<Article> stocks = (List<Article>) q.execute(dummyObject);
			for (Article stock : stocks) {
				if (symbol.equals(stock.getSymbol())) {
					deleteCount++;
					pm.deletePersistent(stock);
				}
			}
			if (deleteCount != 1) {
				LOG.log(Level.WARNING, "removeStock deleted " + deleteCount
						+ " Stocks");
			}
		} finally {
			pm.close();
		}
	}

	public String[] getArticles() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		List<String> symbols = new ArrayList<String>();
		try {
			Query q = pm.newQuery(Article.class);
//			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
//			List<Article> stocks = (List<Article>) q.execute(getUser());
			List<Article> stocks = (List<Article>) q.execute();
			for (Article stock : stocks) {
				symbols.add(stock.getSymbol());
			}
		} finally {
			pm.close();
		}
		return (String[]) symbols.toArray(new String[0]);
	}

	private void checkLoggedIn() throws NotLoggedInException {
//		if (getUser() == null) {
//			throw new NotLoggedInException("Not logged in.");
//		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		return currentUser;
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}
}
