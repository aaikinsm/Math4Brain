package com.blackstar.math4brain;

import android.content.res.Resources;


public class Tips {
	int imgID = 0;
	public String getTip(boolean pro, Resources res){
        String[] tips = res.getStringArray(R.array.tips);
		String tip;
		int i;
		int max=tips.length;
		if (pro) i = (int) (Math.random()*max);
		else i = (int) (Math.random()*(0.7*max)+(0.15*max));
		tip = tips[i].substring(3);
		try{
            imgID = Integer.parseInt(tips[i].substring(0,2));
        }catch(Exception e){imgID=-1;}
		return tip;
	}

	public int getImgResource(){
		switch(imgID){
			case 1 :return R.drawable.t1;
			case 4 :return R.drawable.t4;
			case 6 :return R.drawable.t6;
			case 7 :return R.drawable.t7;
			case 10 :return R.drawable.t10;
			case 12 :return R.drawable.t4;
			case 14 :return R.drawable.t14;
			case 15 :return R.drawable.t15;
			case 17 :return R.drawable.t17;
			case 19 :return R.drawable.t19;
			case 22 :return R.drawable.t22;
			case 24 :return R.drawable.t24;
			case 25 :return R.drawable.t25;
			case 27 :return R.drawable.t27;
			case 30 :return R.drawable.t14;
			case 31 :return R.drawable.t31;
			case 34 :return R.drawable.t34;
			case 37 :return R.drawable.t37;
			case 39 :return R.drawable.t39;
			case 40 :return R.drawable.t40;
			case 41 :return R.drawable.t41;
			case 44 :return R.drawable.t44;
			case 45 :return R.drawable.t19;
			case 48 :return R.drawable.t31;
			case 49 :return R.drawable.t49;
			case 52 :return R.drawable.t52;
			case 54 :return R.drawable.t54;
			case 56 :return R.drawable.t17;
			case 58 :return R.drawable.t58;
			case 59 :return R.drawable.t59;
			case 61 :return R.drawable.t61;
			case 64 :return R.drawable.t58;
			case 66 :return R.drawable.t66;
			case 67 :return R.drawable.t67;
			case 70 :return R.drawable.t70;
			default:
				return R.drawable.tip;
		}
	}

}
