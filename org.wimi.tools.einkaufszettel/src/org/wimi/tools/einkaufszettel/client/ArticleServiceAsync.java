package org.wimi.tools.einkaufszettel.client;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArticleServiceAsync {
  public void addArticle(String symbol, AsyncCallback<Void> async);
  public void removeArticle(String symbol, AsyncCallback<Void> async);
  public void getArticles(AsyncCallback<String[]> async);
}