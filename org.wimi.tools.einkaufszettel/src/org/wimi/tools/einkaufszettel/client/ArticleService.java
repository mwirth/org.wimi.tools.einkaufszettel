package org.wimi.tools.einkaufszettel.client;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("article")
public interface ArticleService extends RemoteService {
  public void addArticle(String symbol) throws NotLoggedInException;
  public void removeArticle(String symbol) throws NotLoggedInException;
  public String[] getArticles() throws NotLoggedInException;
}