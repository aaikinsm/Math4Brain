package com.blackstar.math4brain;

import android.content.Context;

import java.text.DecimalFormat;



public class GameSettings{
	public double clock=60.00;
	public int difficulty=3, score=0, wrong=0, inputTimer=0, equationType=123, sound = 1, music = 1, vibrate = 1, microphone = 0,
			numOfEquations = 10, level, points; 
	boolean timeUp = false, start = false;

	public String getClock(){
		 DecimalFormat df = new DecimalFormat("#.##");
		 if (clock<0){
			 clock=0;
		 }
		 return df.format(clock).replace(",",".");
	}
	
	public int getPoints(){
		int sc = (int)((0.5 + (difficulty*0.5)) *score)-wrong;
		if (equationType==1234) sc = (int)(sc*1.1);
		return sc;
	}
	
	public String getPointCalculation(Context cntx){
		String ca = "\n________________________________\n";
		int diff = (int)((0.5 + (difficulty*0.5)) * score), total=(diff-wrong), allBonus = (int)(total*1.1);
		ca += score+" "+cntx.getString(R.string.correct)+" x "+(0.5 + (difficulty*0.5))+" ("+cntx.getString(R.string.difficulty)+") = "+diff+"\n"
				+diff+" - "+wrong+" ("+cntx.getString(R.string.wrong)+") = "+total;
		if (equationType == 1234) ca += "\n"+total+"  +"+(allBonus-total)+" ("+cntx.getString(R.string.all_bonus)+") = "+allBonus;
		ca += "\n________________________________\n";
		return ca;
	}

}
