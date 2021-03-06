package com.google.code.easyshopper.db.domaincreators;

import android.database.Cursor;

import com.google.code.easyshopper.db.MarketDBAdapter.Columns;
import com.google.code.easyshopper.db.helpers.MyCursor;
import com.google.code.easyshopper.domain.Market;

public class MarketCreator implements DomainObjectCreator<Market>{

	public Market create(Cursor cursor) {
		MyCursor query=new MyCursor(cursor);
		Market market = new Market(query.getLong(Columns.id.column()));
		market.setName(query.getString(Columns.name.column()));
		market.setLongitude(query.getInt(Columns.geo_long.column()));
		market.setLatitude(query.getInt(Columns.geo_lat.column()));
		return market;
	}

}
