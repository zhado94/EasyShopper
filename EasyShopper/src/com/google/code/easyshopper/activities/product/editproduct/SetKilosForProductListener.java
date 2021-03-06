package com.google.code.easyshopper.activities.product.editproduct;

import java.text.DecimalFormat;
import java.util.Currency;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.google.code.easyshopper.R;
import com.google.code.easyshopper.domain.Amount;
import com.google.code.easyshopper.domain.CartProduct;
import com.google.code.easyshopper.utility.StringUtils;

public class SetKilosForProductListener implements TextWatcher {

	private final PriceTypeRetriever priceTypeRetriever;
	private final Activity activity;
	private final CartProduct cartProduct;

	public SetKilosForProductListener(PriceTypeRetriever priceTypeRetriever, CartProduct cartProduct, Activity activity) {
		this.priceTypeRetriever = priceTypeRetriever;
		this.cartProduct = cartProduct;
		this.activity = activity;
	}

	public void afterTextChanged(Editable arg0) {
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(! priceTypeRetriever.priceIsInBarcode()) return;
		String priceByKilos = StringUtils.editTextToString((EditText) activity.findViewById(R.id.EditPrice));
		if(priceByKilos.length() == 0) return;
		Currency currency = cartProduct.getPrice().getCurrency();
		Amount productPrice = cartProduct.calculatePriceAmount(currency);
		Amount priceAmountByKilos=new Amount().setCurrency(currency).setFromReadableAmount(priceByKilos);
		TextView kilosLabel = (TextView) activity.findViewById(R.id.ProductWeightLabel);
		if(priceAmountByKilos.getAmount() == 0) {
			kilosLabel.setText(R.string.N_A);
		} else
		{
			double kilos = (double) productPrice.getAmount() / (double) priceAmountByKilos.getAmount();
			kilosLabel.setText(new DecimalFormat("#.##").format(kilos));
		}
		
	}

}
