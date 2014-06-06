package giu.fbalance;

import java.io.Serializable;

import android.text.format.Time;

public class Patient implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7216889672887657309L;
	
	private double weight;
	private long last_updated;
	private int age;
	private String name;
	private long fasting_since_millis;
	
	public void setLastUpdated(Time lu){
		last_updated=lu.toMillis(false);
	}
	
	public Time getLastUpdated(){
		Time lu=new Time();
		lu.set(last_updated);
		return lu;
	}
	public void setFastingSince(Time ifast){
		fasting_since_millis=ifast.toMillis(false);
	}
	
	public String getLastUpdatedString(){
		Time lu=new Time();
		lu.set(last_updated);
		return (String.format("%02d",lu.hour)+":"+String.format("%02d",lu.minute));

	}
	public void setName(String iname){
		name=iname;
	}
	
	public void setWeight(double iweight){
		weight=iweight;
	}
	

	public Patient(){
		name="DEFAULT_PATIENT";
		weight=100;
//		fasting_since.set(Time.EPOCH_JULIAN_DAY);
		fasting_since_millis=Long.MAX_VALUE;
		age=100;
	}
	
	public Patient(String iname, double iweight, Time ifasting){
		name=iname;
		weight=iweight;
		fasting_since_millis=ifasting.toMillis(false);
	}
	
	public double getWeight() {
		return weight;
	}
	
	public String getName() {
		return name;
	}
	
	public Time getFasting_since() {
		Time fasting_since=new Time();
		fasting_since.set(fasting_since_millis);
		return fasting_since;
	}
	
}
