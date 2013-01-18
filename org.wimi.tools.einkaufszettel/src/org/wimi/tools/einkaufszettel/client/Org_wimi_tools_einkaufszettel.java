package org.wimi.tools.einkaufszettel.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Org_wimi_tools_einkaufszettel implements EntryPoint {
	private static final int REFRESH_INTERVAL = 5000; // ms
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<String> buylist = new ArrayList<String>();
	private final ArticleServiceAsync articleService = GWT
			.create(ArticleService.class);

	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		createArticleWatcher();
	}
	
	private void createArticleWatcher()
	{
		// Create table for stock data.
		stocksFlexTable.setText(0, 0, "Artikel");
		// stocksFlexTable.setText(0, 1, "Menge");
		// stocksFlexTable.setText(0, 2, "Preis");
		stocksFlexTable.setText(0, 3, "entfernen");

		// Add styles to elements in the stock list table.
		stocksFlexTable.setCellPadding(6);
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.addStyleName("watchList");
		// stocksFlexTable.getCellFormatter().addStyleName(0, 1,
		// "watchListNumericColumn");
		// stocksFlexTable.getCellFormatter().addStyleName(0, 2,
		// "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 3,
				"watchListRemoveColumn");

		loadArticles();
		// Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.addStyleName("addPanel");

		// Assemble Main panel.
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("einkaufszettel").add(mainPanel);

		// Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);

		// Setup timer to refresh list automatically.
		// Timer refreshTimer = new Timer() {
		// @Override
		// public void run() {
		// refreshWatchList();
		// }
		// };
		// refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addArticle();
			}
		});

		// Listen for keyboard events in the input box.
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					addArticle();
				}
			}
		});

	}

	/**
	 * Add stock to FlexTable. Executed when the user clicks the addStockButton
	 * or presses enter in the newSymbolTextBox.
	 */
	private void addArticle() {
		final String symbol = newSymbolTextBox.getText().trim();
		newSymbolTextBox.setFocus(true);

		// articlename must be between 1 and 40 chars that are numbers, letters,
		// or dots.
//		if (!symbol.matches("^[0-9a-zA-Z\\.]{1,40}$")) {
		if(symbol.length() > 20) {
			Window.alert("'" + symbol + "' is not a valid symbol.");
			newSymbolTextBox.selectAll();
			return;
		}

		newSymbolTextBox.setText("");

		// Don't add the stock if it's already in the table.
		if (buylist.contains(symbol)) {
			Window.alert("articel is already in the list");
			return;
		}

		addStock(symbol);
	}

	private void addStock(final String symbol) {
		articleService.addArticle(symbol, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
			}

			public void onSuccess(Void ignore) {
				displayStock(symbol);
			}
		});
	}

	private void displayStock(final String symbol) {

		// Add the stock to the table.
		int row = stocksFlexTable.getRowCount();
		buylist.add(symbol);
		stocksFlexTable.setText(row, 0, symbol);
		// stocksFlexTable.setWidget(row, 2, new Label());
		// stocksFlexTable.getCellFormatter().addStyleName(row, 1,
		// "watchListNumericColumn");
		// stocksFlexTable.getCellFormatter().addStyleName(row, 2,
		// "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 3,
				"watchListRemoveColumn");

		// Add a button to remove this stock from the table.
		Button removeStockButton = new Button("x");
		removeStockButton.addStyleDependentName("remove");
		removeStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeStock(symbol);
				// int removedIndex = buylist.indexOf(symbol);
				// buylist.remove(removedIndex);
				// stocksFlexTable.removeRow(removedIndex + 1);
			}
		});
		stocksFlexTable.setWidget(row, 3, removeStockButton);

		// Get the stock price.
		refreshWatchList();

	}

	private void removeStock(final String symbol) {
		articleService.removeArticle(symbol, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
			}

			public void onSuccess(Void ignore) {
				undisplayStock(symbol);
			}
		});
	}

	/**
	 * Generate random stock prices.
	 */
	private void refreshWatchList() {
		final double MAX_PRICE = 100.0; // $100.00
		final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

		Article[] prices = new Article[buylist.size()];
		for (int i = 0; i < buylist.size(); i++) {
			double price = Random.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE
					* (Random.nextDouble() * 2.0 - 1.0);

			prices[i] = new Article(buylist.get(i), price, change);
		}

		updateTable(prices);
	}

	/**
	 * Update the Price and Change fields all the rows in the stock table.
	 * 
	 * @param prices
	 *            Stock data for all rows.
	 */
	private void updateTable(Article[] prices) {
		// for (int i = 0; i < prices.length; i++) {
		// updateTable(prices[i]);
		// }
		//
		// // Display timestamp showing last refresh.
		// lastUpdatedLabel.setText("Last update : "
		// + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
		//
	}

	/**
	 * Update a single row in the stock table.
	 * 
	 * @param article
	 *            Stock data for a single row.
	 */
	private void updateTable(Article article) {
		// Make sure the stock is still in the stock table.
		if (!buylist.contains(article.getArticleName())) {
			return;
		}

		int row = buylist.indexOf(article.getArticleName()) + 1;

		// Format the data in the Price and Change fields.
		// String price = NumberFormat.getFormat("#,##0.00").format(
		// article.getQuantity());
		// NumberFormat changeFormat =
		// NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		// String changeText = changeFormat.format(article.getPrice());
		// String changePercentText =
		// changeFormat.format(article.getChangePercent());

		// Populate the Price and Change fields with new data.
		// stocksFlexTable.setText(row, 1, price);
		// Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
		// changeWidget.setText(changeText + " (" + changePercentText + "%)");

		// Change the color of text in the Change field based on its value.
		String changeStyleName = "noChange";
		if (article.getChangePercent() < -0.1f) {
			changeStyleName = "negativeChange";
		} else if (article.getChangePercent() > 0.1f) {
			changeStyleName = "positiveChange";
		}

		// changeWidget.setStyleName(changeStyleName);
	}

	private void loadArticles() {
		articleService.getArticles(new AsyncCallback<String[]>() {
			public void onFailure(Throwable error) {
			}

			public void onSuccess(String[] symbols) {
				displayArticles(symbols);
			}
		});
	}

	private void displayArticles(String[] symbols) {
		for (String symbol : symbols) {
			displayStock(symbol);
		}
	}

	// private void checkLoggedIn() throws NotLoggedInException {
	// if (getUser() == null) {
	// throw new NotLoggedInException("Not logged in.");
	// }
	// }

	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
//		if (error instanceof NotLoggedInException) {
//			Window.Location.replace(loginInfo.getLogoutUrl());
//		}
	}

	private void undisplayStock(String symbol) {
		int removedIndex = buylist.indexOf(symbol);
		buylist.remove(removedIndex);
		stocksFlexTable.removeRow(removedIndex + 1);
	}
}
