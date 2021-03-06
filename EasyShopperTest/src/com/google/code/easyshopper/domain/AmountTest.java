package com.google.code.easyshopper.domain;

import java.util.Currency;

import android.test.AndroidTestCase;


public class AmountTest extends AndroidTestCase {

	public void testReadableAmount() {
		Amount amount=new Amount(1234567l, Currency.getInstance("EUR"));
		assertEquals("12345.67", amount.getReadableAmount(1));
	}
	public void testReadableAmountOnLastDigit() {
		Amount amount=new Amount(12345607l, Currency.getInstance("EUR"));
		assertEquals("123456.07", amount.getReadableAmount(1));
	}
	
	
	public void testReadableAmountShouldBeAlwaysAsTwoDigits() {
		Amount amount=new Amount(123456700l, Currency.getInstance("EUR"));
		assertEquals("1234567.00", amount.getReadableAmount(1));
		amount.setAmount(123456710);
		assertEquals("1234567.10", amount.getReadableAmount(1));
	}
	
	public void testSetFromReadableAmount() {
		Amount amount=new Amount(0l, Currency.getInstance("EUR"));
		amount.setFromReadableAmount("123456.78987");
		assertEquals(12345678, amount.getAmount());
	}
	
	public void testShouldSetNullAmount() {
		Amount amount=new Amount(10l, Currency.getInstance("EUR"));
		amount.setFromReadableAmount("");
		assertEquals("", amount.getReadableAmount(1));
	}
	
	public void testShouldDisplayEmptyStringForNullAmount() {
		Amount amount=new Amount(null, Currency.getInstance("EUR"));
		assertEquals("", amount.getReadableAmount(1));
	}
	
	public void testSetFromReadableAmountShouldParseIntegerValues() {
		Amount amount=new Amount(0l, Currency.getInstance("EUR"));
		amount.setFromReadableAmount("123456");
		assertEquals(12345600, amount.getAmount());
		amount.setFromReadableAmount("123456.1");
		assertEquals(12345610, amount.getAmount());
	}
}
