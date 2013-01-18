package org.wimi.tools.einkaufszettel.client;

public class Article {

  private String articleName;
  private double quantity;
  private double price;

  public Article() {
  }

  public Article(String symbol, double price, double change) {
    this.articleName = symbol;
    this.quantity = price;
    this.price = change;
  }

  public String getArticleName() {
    return this.articleName;
  }

  public double getQuantity() {
    return this.quantity;
  }

  public double getPrice() {
    return this.price;
  }

  public double getChangePercent() {
    return 100.0 * this.price / this.quantity;
  }

  public void setArticleName(String articleName) {
    this.articleName = articleName;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public void setPrice(double price) {
    this.price = price;
  }
}
