package giu.fbalance;

import java.io.Serializable;

import android.text.format.Time;

public class BalanceEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2715432134264955954L;
	private String name;
	private double amount;
	private Long entered_millis;
	boolean removable , dynamic;
	private Long updated_millis;
	private double rate;
	private long time_frozen;
	private boolean frozen=false;

	public void setName(String iname){
		name=iname;
	}

	public void setAmount(double iamount){
		amount=iamount;
	}

	public BalanceEntry(){
		name="default_entry";
		amount=0.0d;
		rate=0d;

		entered_millis=0L;

		removable=true;
		dynamic=false;

		updated_millis=0L;
	}

	public void freeze(Time frozenTime){
		if(!frozen){
			update();
			frozen=true;
			time_frozen=frozenTime.toMillis(false);
		}
	}

	public String getFrozenTimeString(){

		Time frozen=new Time();
		frozen.set(time_frozen);
		return (String.format("%02d",frozen.hour)+":"+String.format("%02d",frozen.minute));

	}

	public BalanceEntry(String iname, double iamount, Time ientered, boolean iremovable){
		name=iname;
		amount=iamount;
		removable=iremovable;

		entered_millis=ientered.toMillis(false);
	}

	public BalanceEntry(String iname, Time ientered, boolean iremovable, double irate){
		name=iname;
		amount=0;
		removable=iremovable;

		entered_millis=ientered.toMillis(false);

		updated_millis=0L;

		dynamic=true;

		rate=irate;
		initialize();
	}

	public String getEntryName(){
		return name;
	}

	public double getAmount(){
		return amount;
	}

	public void update(){
		if(dynamic && !frozen){
			long last=updated_millis;
			Time now=new Time();
			now.setToNow();
			updated_millis=now.toMillis(false);
			double elapsedSec=(updated_millis-last)/1000d;
			amount+=elapsedSec*rate;
		}
	}

	public void initialize(){
		long last=entered_millis;
		Time now=new Time();
		now.setToNow();
		updated_millis=now.toMillis(false);
		double elapsedSec=(updated_millis-last)/1000d;
		amount+=elapsedSec*rate;
	}

	public boolean isDynamic(){
		return dynamic;
	}

	public boolean isRemovable(){
		return removable;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public long getTimeMillis(){
		return entered_millis;
	}
	public String getEntryString(){
		Time entered=new Time();
		entered.set(entered_millis);
		return (String.format("%02d",entered.hour)+":"+String.format("%02d",entered.minute)+", "+name+" "+String.format("%6.2f",amount)+" ml");
	}

	public String getTimeString(){
		Time entered=new Time();
		entered.set(entered_millis);
		return (String.format("%02d",entered.hour)+":"+String.format("%02d",entered.minute));
	}

	public String getValueString(){
		return (name+" "+String.format("%6.2f",amount)+" ml");
	}
}
