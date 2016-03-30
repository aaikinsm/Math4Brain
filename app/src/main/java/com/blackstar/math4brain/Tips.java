package com.blackstar.math4brain;

import android.content.res.Resources;


public class Tips {
	
	public String getTip(boolean pro, Resources res){
        String[] tips = res.getStringArray(R.array.tips);
		String tip;
		int i;
		int max=tips.length;
		if (pro) i = (int) (Math.random()*max);
		else i = (int) (Math.random()*(0.7*max)+(0.15*max));
		tip = tips[i];
		return tip;
	}

}
