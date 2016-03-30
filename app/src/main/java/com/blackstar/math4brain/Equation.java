package com.blackstar.math4brain;

import android.content.Context;

public class Equation {

		public double num1, num2, num3;
		public int eqType, eqType2, diff, randCount, ans, numPrev=0, mAns=0;
		public String prevEquations = "", sign=""; 
		boolean ok = true, multi=false;
		Context cntx;
		
		Equation(int type, int difficult, Context ct){
			eqType = type;
			eqType2 = type;
			diff = difficult;
			cntx = ct;
			if(difficult<1) diff=1;
		}
		
		public String getEquation (){
			String equation;
			if (eqType == 1){
				if(num2<0){
					equation = ((int)num1+" - "+(int)Math.abs(num2));
				}else{
					equation = ((int)num1+" + "+(int)num2);
				}
				if(multi){
					return "("+equation+") "+sign+" "+(int)num3+" = ";
				}
				return equation+" = ";
			}
			if (eqType == 2){
				if(num1>-1){
					equation = ((int)num1+" x "+(int)num2);
				}else{
					equation = (Math.abs((int)num1)+" ÷ "+(int)num2);
				}
				if(multi){
					return "("+equation+") "+sign+" "+(int)num3+" = ";
				}
				return equation+" = ";
			}
			if (eqType == 3){
				equation = ((int)num1 +superscript((int)num2));
				if(multi){
					return "("+equation+") "+sign+" "+(int)num3+" = ";
				}
				return equation+" = ";
			}
			if (eqType == 4){
				equation = ((int)num2 +cntx.getString(R.string.percent_sign)+ (int)num1);
				if(multi){
					return "("+equation+") "+sign+" "+(int)num3+" = ";
				}
				return equation+" = ";
			}

			return "0";
		}
		
		public String superscript(int i){
			String ss;
			switch (i) {
	            case 1:  ss = "¹"; break;
	            case 2:  ss = "²"; break;
	            case 3:  ss = "³"; break;
	            case 4:  ss = "^4"; break;
	            case 5:  ss = "^5"; break;
	            case 6:  ss = "^6"; break;
	            case 7:  ss = "^7"; break;
	            case 8:  ss = "^8"; break;
	            case 9:  ss = "^9"; break;
	            case 0: ss = "º"; break;
	            default: ss = "º"; break;
			}
			return ss;
		}
		
		public String getAnswer(){
			if(getEquation().equals("3³ = "))ans = 27; //quick fix
			if(getEquation().equals("3² = "))ans = 9; //quick fix
			if(getEquation().equals("3¹ = "))ans = 3; //quick fix
			return ans+"";
		}
		
		public String getHint(){
			int n1 = (int) num1, n2 = (int)num2;
			String hnt="";
			if(multi){
				hnt = "("+mAns+") "+sign+" "+(int)num3+" = ";
			}else{
				if(eqType==2){
					if(n1>-1){
						if (n1%10 == 0) hnt = "("+(n1/10)+"x"+n2+") x 10 = ? \n"+n1*n2/10+" x 10 = ?";
						else if(n1>10) hnt = "("+(n1-n1%10)+"x"+n2+") + ("+n1%10+"x"+n2+") = ?";
						else if(n1==1) hnt = "1 x # = #";
						else if(n1==2) hnt = n2+" + "+n2+" = ?";
						else if(n1==3) hnt = n2+" + "+n2+" + "+n2+" = ?";
						else if(n1==4) hnt = n2+" + "+n2+" + "+n2+" + "+n2+" = ?";
						else if(n2==1) hnt = "# x 1 = #";
						else if(n2==2) hnt = n1+" + "+n1+" = ?";
						else if(n2==3) hnt = n1+" + "+n1+" + "+n1+" = ?";
						else if(n2==4) hnt = n1+" + "+n1+" + "+n1+" + "+n1+" = ?\n"+(n1+n1+n1)+" + "+n1+" = ?";
						else if(n2>10 ) hnt = "("+(n2-n2%10)+"x"+n1+") + ("+n2%10+"x"+n1+") = ?";
						else if (n2%10 == 0) hnt = "("+n1+"x"+(n2/10)+") x 10 = ? \n"+n2*n1/10+" x 10 = ?";
					}else{
						hnt = n2+" x ? = "+Math.abs(n1);
					}
				}
				if(eqType==3){
					if(n2==0)hnt = "#"+superscript(n2)+" = 1";
					if(n2==1)hnt = "#"+superscript(n2)+" = #";
					if(n2==2)hnt = n1+"x"+n1+" = ?";
					if(n2==3)hnt = n1+"x"+n1+"x"+n1+" = ?\n"+n1*n1+"x"+n1+" = ?";
					if(n2==4)hnt = n1+"x"+n1+"x"+n1+"x"+n1+" = ?\n"+(n1*n1*n1)+"x"+n1+" = ?";
				}if(eqType==4){
					if(n1==100) hnt = "#"+cntx.getString(R.string.percent_sign)+ n1+" = #";
					else if(n1==0) hnt = "#"+cntx.getString(R.string.percent_sign)+ n1+" = 0";
					else if(n2==75) hnt = "("+n1+" ÷ 4) x 3 = ?";
					else if(n2==50) hnt = n1+" ÷ 2 = ?";
					else if(n2==25) hnt = n1+" ÷ 4 = ?";
					else if(n2==10) hnt = n1+" ÷ 10 = ?";
					else if(n2%10==0) hnt = n2/10+" x (10"+cntx.getString(R.string.percent_sign)+n1+") = ?";
				}
			}
			return hnt;
		}
		
		public void createNew(){
			multi=false;
			int difficulty= diff, eq2=0;
			String eq = eqType2+"";
			//All or other combinations
			int pos = (int) ( Math.random()*(eq.length()));
			eqType = Integer.parseInt(eq.substring(pos,pos+1));			
			if(eq.length()>1 && (difficulty>3)){
				int pos2 = (int) ( Math.random()*(eq.length()));
				eq2 = Integer.parseInt(eq.substring(pos2,pos2+1));
				if(eq2 == 1 || eq2 == 2) difficulty--;
			}

			//Addition & Subtraction
			if (eqType == 1){
				int uBound = (int) Math.pow(difficulty,3)+4, lBound=difficulty*2;
				randCount = lBound + (int) ( Math.random()*(uBound - lBound) );
				ans = lBound + (int) ( Math.random()*(uBound - lBound) );
				num1 = randCount; num2 = ans - num1;
			}
			//Multiplication & Division
			else if (eqType == 2){
				boolean search = true;
				int choice = (int) ( Math.random()*(2) );
				int uBound = difficulty*3+3, lBound=difficulty;
				while (search){
					randCount = lBound + (int) ( Math.random()*(uBound - lBound) );
					if(randCount==0) randCount=1;
					ans = lBound + (int) ( Math.random()*(Math.pow(difficulty,2)*10 - lBound) );
					if (ans%randCount == 0 && choice==0){
						num1 = randCount; num2 = ans/randCount;
						search=false;
					}else if(ans%randCount==0){
						num2 = randCount; num1 = ans; ans = (int) (num1/num2);
						num1 = 0-num1;
						search=false;
					}
				}
			}
			//Powers
			else if (eqType == 3){
				int uBound = difficulty+3, lBound=0;
				num1 = lBound + (int) ( Math.random()*(uBound - lBound) );
				num2 = lBound + (int) ( Math.random()*(difficulty+1 - lBound) );
				if(num2>4) num2=4;//eliminate very difficult powers
				ans = (int) Math.pow(num1, num2);
				//adjust difficulty
				if(difficulty > 1 && ans == 0){
					ok=false;
				}else if(difficulty > 2 && ans <= 2){
					ok=false;
				}else if(difficulty > 3 && ans <= 6){
					ok=false;
				}
			}
			//Percent
			else if (eqType == 4){
				int per;
				switch (difficulty) {
		            case 1:  per = 50; break;
		            case 2:  per = 25; break;
		            case 3:  per = 15; break;
		            case 4:  per = 10; break;
		            case 5:  per = 5; break;
		            default: per = 50; break;
				}
				boolean search = true;
				while (search){
					int uBound = 111, lBound=0;
					num1 = lBound + (int) ( Math.random()*(uBound - lBound) );
					num1 = (int)(num1/10)*10;
					num2 = per * (1 + (int) ( Math.random()*(110/per - 1) ));
					double d1 = (num1*(num2/100)), d2 = (int) (num1*(num2/100));
					if (d1 == d2){
						ans = (int) (num1*(num2/100));
						if (ans%5== 0){
							if (!((difficulty>2)&&((num1==0)||(num1==100)||(num2==100)||(num2==0)))){//eliminate easy questions
								search = false;
							}
						}
					}
				}
			}
			//Multi Equations
			if (eq2 == 1){
					int uBound = (int) Math.pow(difficulty,2), lBound=difficulty;
					num3 = lBound + (int) ( Math.random()*(uBound - lBound) );
					if (num1%4 == 0){
						if ((ans-num3)>=0){
							sign="-";
							mAns = ans;
							ans=(int) (ans-num3);
							multi=true;
						}
					}else if(num1%4 == 1){
						sign="+";
						mAns = ans;
						ans=(int) (ans+num3);
						multi=true;
					}
			}
			else if(eq2 == 2){
					int uBound = difficulty+2, lBound=difficulty/2;
					num3 = lBound + (int) ( Math.random()*(uBound - lBound) );
					if(num2%4==0){
						if(ans%num3 == 0){
							sign="÷";
							mAns = ans;
							ans=(int)(ans/num3);
							multi=true;
						}
					}else if(num2%4==1){
						sign="x";
						mAns = ans;
						ans= (int)(ans*num3);
						multi=true;
					}
			}
			//Decrease repeated questions
			if(!ok || prevEquations.contains(getEquation())){
				ok = true;
				if(numPrev>30){
					prevEquations = "";
					numPrev = 0;
				}				
				numPrev++;
				createNew();
			}
			else{ 
				prevEquations += getEquation()+",";
				numPrev ++;
			}
		}
}
