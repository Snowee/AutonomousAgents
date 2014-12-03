package Assignment3;

import java.util.ArrayList;
import java.util.List;

public class ReturnsSaver {
	private int nReturns;
	private double sumOfRet;
	private List<Double> returns;
	
	public ReturnsSaver(){
		nReturns = 0;
		sumOfRet = 0;
		returns = new ArrayList<Double>();
	}
	
	public void addReturn(double ret){
		returns.add(ret);
		sumOfRet = sumOfRet + ret;
		nReturns++;
	}
	
	public double getAverageReturn(){
		return sumOfRet/(double) nReturns;
	}

}