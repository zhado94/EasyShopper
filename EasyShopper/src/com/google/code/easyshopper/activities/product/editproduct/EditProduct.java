package com.google.code.easyshopper.activities.product.editproduct;

import java.util.Currency;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.code.easyshopper.Logger;
import com.google.code.easyshopper.R;
import com.google.code.easyshopper.activities.ESTab;
import com.google.code.easyshopper.activities.RadioGroupWrapper;
import com.google.code.easyshopper.domain.CartProduct;
import com.google.code.easyshopper.domain.Price;

public class EditProduct implements ESTab {

	private final Activity activity;
	private ImageView productPictureView;
	private final CartProduct cartProduct;
	private View view;
	private UpdateValues updateOnExit;
	private Spinner currencySpinner;
	private ArrayAdapter<CurrencyItem> currencySpinnerAdapter;
	private EditText editPrice;
	private ProductImageManager imageCleaner;
	private ProductSaver productSaver;
	public static final String TAG = "edit_product";
	private static final String[] currencies = new String[] { "EUR", "USD" };
	private EditText productName;
	private final Refresher otherTabsRefresher;

	public EditProduct(CartProduct cartProduct, Activity activity, Refresher otherTabsRefresher) {
		this.cartProduct = cartProduct;
		this.activity = activity;
		this.otherTabsRefresher = otherTabsRefresher;
		this.productSaver=new ProductSaver(cartProduct, activity);
		this.imageCleaner = new ProductImageManager(cartProduct, activity, R.id.ProductSmallPicture, android.R.drawable.ic_menu_gallery);

		Logger.d(this, "EditProduct", "CartProduct: " + cartProduct);
	}


	public void updateValuesOnExit() {
		if (updateOnExit != null)
			updateOnExit.update();
	}

	public View getView() {
		if (view == null)
			view = activity.getLayoutInflater().inflate(R.layout.edit_product, null);
		return view;
	}

	public void setup() {

		TextView barcodeTextview = (TextView) activity.findViewById(R.id.ProductBarcode);
		barcodeTextview.setText(cartProduct.getFullBarcode());

		currencySpinner = (Spinner) activity.findViewById(R.id.EditCurrency);
		Price currentPrice = cartProduct.getPrice();
		editPrice = (EditText) activity.findViewById(R.id.EditPrice);
		Button saveButton = (Button) activity.findViewById(R.id.ProductCRUD_DoneButton);
		Button addToCart = (Button) activity.findViewById(R.id.AddToCartButton);
		productPictureView = (ImageView) activity.findViewById(R.id.ProductSmallPicture);
		productName = (EditText) activity.findViewById(R.id.ProductName);
		RadioGroup productPriceType = (RadioGroup) activity.findViewById(R.id.PriceType);
		
		PriceTypeRetriever priceTypeRetriever = new PriceTypeRetrieverFromRadio(new RadioGroupWrapper(productPriceType));

		productName.addTextChangedListener(new ButtonsEnablerWatcher(saveButton, addToCart, editPrice));
		editPrice.addTextChangedListener(new ButtonsEnablerWatcher(saveButton, addToCart, productName) );
		CurrencyRetriever currencyRetriever = new CurrencyRetriever() {
			
			public Currency currency() {
				Logger.d(this, "currency", "currency list: " + currencySpinnerAdapter.getCount());
				return currencySpinnerAdapter.getItem(currencySpinner.getSelectedItemPosition()).currency;
			}
		};
		SetKilosForProductListener setKilosForProductListener = new SetKilosForProductListener(priceTypeRetriever, cartProduct, currencyRetriever , activity);
		editPrice.addTextChangedListener(setKilosForProductListener );
		Refresher refresher = new Refresher() {
			
			public void refresh() {
				imageCleaner.refresh();
				otherTabsRefresher.refresh();
			}
		};
		ProductPriceTypeChangedListener productPriceTypeChangedListener = new ProductPriceTypeChangedListener(priceTypeRetriever, activity, cartProduct, currencyRetriever, refresher );
		productPriceType.setOnCheckedChangeListener(productPriceTypeChangedListener );
		saveButton.setOnClickListener(new SaveProductListener(productName, editPrice, currencyRetriever, productSaver, activity));
		addToCart.setOnClickListener(new AddToCartListener(cartProduct, productName, editPrice, currencyRetriever, productSaver, activity));

		((TextView) activity.findViewById(R.id.EditPriceDialog_MarketName)).setText(cartProduct.getShopping().getMarket().getName());

		currencySpinnerAdapter = new ArrayAdapter<CurrencyItem>(activity, android.R.layout.simple_dropdown_item_1line);
		currencySpinner.setAdapter(currencySpinnerAdapter);
		populateCurrencyCombo(currentPrice);

		if(cartProduct.getProduct().isPriceDefinedInBarcode()) {
			productPriceType.check(R.id.PriceTypeWeight);
		}

		// TODO check sul prezzo anzichè sul market
		addToCart.setEnabled(cartProduct.getShopping().getMarket() != null);
		productName.setText(cartProduct.getProduct().getName());
//		productPictureView.setImageDrawable(cartProduct.getProduct().getImage().getDrawableForProductDetails(activity));
		productPictureView.setOnClickListener(new PopupImageListener(R.id.ProductName, cartProduct, activity, new GrabImageLauncher(imageCleaner)));
		productPriceTypeChangedListener.onCheckedChanged(null, 0);
		setKilosForProductListener.onTextChanged(null, 0, 0, 0);
		editPrice.setText(currentPrice != null ? currentPrice.getAmount().getReadableAmount(1) : "");

	}

	public ImageManager getImageCleaner() {
		return imageCleaner;
	}


	public class CurrencyItem {
		public final Currency currency;

		public CurrencyItem(Currency instance) {
			this.currency = instance;
		}

		@Override
		public String toString() {
			return currency.getSymbol();
		}
	}


	private void populateCurrencyCombo(Price currentPrice) {
		CurrencyItem currentCurrency = null;
		currencySpinnerAdapter.clear();
		for (String currency : currencies) {
			CurrencyItem adapter = new CurrencyItem(Currency.getInstance(currency));
			if (currentPrice != null && currency.equals(currentPrice.getCurrency().getCurrencyCode())) {
				currentCurrency = adapter;
			}
			currencySpinnerAdapter.add(adapter);
		}
		currencySpinner.setSelection(currencySpinnerAdapter.getPosition(currentCurrency));
	}
}