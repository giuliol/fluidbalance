package giu.fbalance;

import giu.fbalance.SessionMonitorEditText.OnEditSessionCompleteListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class PatientActivity extends Activity {

	boolean API11=false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.patient_layout);
	    
		API11=(android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.HONEYCOMB);
//		if(!API11)
//			ViewHelper.setAlpha((TextView)findViewById(R.id.patient_beta_banner),0.05f);
		tf=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	    
	    title=(TextView)findViewById(R.id.patientAct_title);
	    title2=(TextView)findViewById(R.id.patientAct_title2);
	    weight_label=(TextView)findViewById(R.id.patientAct_weigth_label);
	    timepicker_label=(TextView)findViewById(R.id.patientAct_fast_label);
	    name_et=(SessionMonitorEditText)findViewById(R.id.patientAct_name_et);
	    weight_et=(SessionMonitorEditText)findViewById(R.id.patientAct_weight_et);
	    tp=(TimePicker)findViewById(R.id.patientAct_fast_timePicker);
	    
	    title.setTypeface(tf);
	    title2.setTypeface(tf);
	    
	    tp.setIs24HourView(true);
	    
	    timepicker_label.setTypeface(tf);
	    name_et.setTypeface(tf);
	    weight_et.setTypeface(tf);
	    weight_label.setTypeface(tf);
	    
	    Time temp=new Time();
	    temp.setToNow();
	    fasting_since.setToNow();
	    day=temp.monthDay;
	    month=temp.month;
	    tp.setCurrentHour(temp.hour);
	    tp.setCurrentMinute(temp.minute);
	    tp.setIs24HourView(true);
	    tp.setAddStatesFromChildren(true);
	    
	    ready=thiscontext.getResources().getString(R.string.ready);
	    weight_et.setOnEditSessionCompleteListener(new OnEditSessionCompleteListener() {
			@Override
			public void onEditSessionComplete(TextView v) {
				weight_sessionComplete=true;
				if(name_sessionComplete==true){
					if(API11)
						showNext();
					else
						showNextOldApi();
				}
			}
		});
	    
	    name_et.setOnEditSessionCompleteListener(new OnEditSessionCompleteListener() {
			@Override
			public void onEditSessionComplete(TextView v) {
				name_sessionComplete=true;
				if(weight_sessionComplete==true){
					if(API11)
						showNext();
					else
						showNextOldApi();
				}
			}
		});
	    
	    tp.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				if(lasthour==23 && hourOfDay==00){
					GAnimator.pulse(view,0);
					GAnimator.pulse(timepicker_label,0);
					fasting_since.monthDay++;
					if(fasting_since.monthDay>fasting_since.getActualMaximum(Time.MONTH_DAY)){
						fasting_since.month++;
						if(fasting_since.month>31){
							fasting_since.year++;
							fasting_since.month=0;
						}
						fasting_since.monthDay=1;
					}
					timepicker_label.setText(String.format(thiscontext.getResources().getString(R.string.fasting_since_dm),fasting_since.monthDay,thiscontext.getResources().getStringArray(R.array.monthNames)[fasting_since.month]));
				}
				else if(lasthour==00 && hourOfDay==23){
					GAnimator.pulse(view,0);
					GAnimator.pulse(timepicker_label,0);
					fasting_since.monthDay--;
					if(fasting_since.monthDay<1){
						fasting_since.month--;
						if(fasting_since.month<0){
							fasting_since.year--;
							fasting_since.month=31;
						}
						fasting_since.monthDay=fasting_since.getActualMaximum(Time.MONTH_DAY);
					}
					timepicker_label.setText(String.format(thiscontext.getResources().getString(R.string.fasting_since_dm),fasting_since.monthDay,thiscontext.getResources().getStringArray(R.array.monthNames)[fasting_since.month]));
				}
				lasthour=hourOfDay;

			}
		});
	    
	    title2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!areFieldsEmpty() && okFast()){
					try {
						Intent i=new Intent(PatientActivity.this,BalanceActivity.class);
						Bundle b=new Bundle();
						b.putString("name", name_et.getText().toString());
						b.putDouble("weight", Double.parseDouble(weight_et.getText().toString()));
						fasting_since.hour=tp.getCurrentHour();
						fasting_since.minute=tp.getCurrentMinute();
						b.putLong("fasting_millis",fasting_since.toMillis(false));
						i.putExtras(b);
						startActivity(i);
						finish();
					} catch (NumberFormatException e) {
						Toast.makeText(PatientActivity.this,thiscontext.getResources().getString(R.string.fillAllFields)+Resources.getSystem().getString(R.string.fastingError), Toast.LENGTH_SHORT).show();
					}
				}
				else if(!okFast() && !areFieldsEmpty()){
					Toast.makeText(PatientActivity.this,thiscontext.getResources().getString(R.string.fastingError), Toast.LENGTH_SHORT).show();
					if(API11)
						GAnimator.pulse(tp,1);
					else
						GAnimator.pulseOldApi(tp, 1);
				}
				else if(okFast() && areFieldsEmpty()){
					Toast.makeText(PatientActivity.this,thiscontext.getResources().getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show();
					if(name_et.getText().toString().matches(""))
						if(API11)
							GAnimator.pulse(name_et,1);
						else
							GAnimator.pulseOldApi(name_et,1);

					if(weight_et.getText().toString().matches(""))
						if(API11)
							GAnimator.pulse(weight_et,1);
						else
							GAnimator.pulseOldApi(weight_et,1);

				}
				else if(!okFast() && areFieldsEmpty()){
					Toast.makeText(PatientActivity.this,thiscontext.getResources().getString(R.string.fillAllFields)+thiscontext.getResources().getString(R.string.fastingError), Toast.LENGTH_SHORT).show();
					if(name_et.getText().toString().matches(""))
						if(API11)
							GAnimator.pulse(name_et,1);
						else
							GAnimator.pulseOldApi(name_et,1);

					if(weight_et.getText().toString().matches(""))
						if(API11)
							GAnimator.pulse(weight_et,1);
						else
							GAnimator.pulseOldApi(weight_et,1);

					if(API11)
						GAnimator.pulse(tp,1);
					else
						GAnimator.pulseOldApi(tp,1);
				}
			}
		});
	    title2.setClickable(false);
	}

	TextView title, timepicker_label, weight_label,title2;
	SessionMonitorEditText name_et,weight_et;
	Time fasting_since=new Time();
	TimePicker tp;
	Typeface tf;
	Context thiscontext=this;
	String ready;
	int lasthour,day,month;
	
	boolean weight_sessionComplete=false;
	boolean name_sessionComplete=false;
	
	private boolean areFieldsEmpty(){
		return ((name_et.getText().toString().matches("")) || (weight_et.getText().toString().matches("")));
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showNext(){
		title2.setAlpha(0f);
		title2.setVisibility(View.VISIBLE);
		GAnimator.slowFade(title,false);
		GAnimator.slowFadeWithEndAction(title2, true, new Runnable() {
			
			@Override
			public void run() {
				GAnimator.pulse(title2,1);
			}
		});
		
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(weight_label.getWindowToken(), 0);
	    title2.setClickable(true);
	}
	
	private void showNextOldApi(){
//		ViewHelper.setAlpha(title2,0f);
//		title2.setVisibility(View.VISIBLE);
//		GAnimator.slowFadeOldApi(title,false);
//		GAnimator.slowFadeWithEndActionOldApi(title2, true, new Runnable() {
//			
//			@Override
//			public void run() {
//				GAnimator.pulseOldApi(title2,1);
//			}
//		});
//		
//		InputMethodManager imm = (InputMethodManager)getSystemService(
//				Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(weight_label.getWindowToken(), 0);
//	    title2.setClickable(true);
	}
	
	private boolean okFast(){
		
//		fasting_since.setToNow();
		Time temp=new Time();
		temp.setToNow();
//		fasting_since.hour=tp.getCurrentHour();
//		fasting_since.minute=tp.getCurrentMinute();
		return (!fasting_since.after(temp));
	}
	
}
