package giu.fbalance;
//TODO https://github.com/47deg/android-swipelistview
import giu.swipedismiss.SwipeDismissListViewTouchListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BalanceActivity extends Activity {
	final public static int DIV_HEIGTH=18;

	boolean API11=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_layout);

		API11=(android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.HONEYCOMB);
		Locale.setDefault(new Locale("en", "US"));

		//		if(API11)
		init_Listeners();

		init_patient();
		init();

		start_updater();
	}

	private void save_state(){
		ObjectOutputStream oos;
		if(!currentPatient.getName().matches("null"))
			try {
				oos = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir()+"/"+currentPatient.getName()+".data")));
				oos.writeObject(currentPatient);
				oos.writeObject(balanceIn_EntryArrayList);
				oos.writeObject(balanceOut_EntryArrayList);
				oos.flush();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else{
			Log.i("g","error! patient name matches null!");
		}

		
	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.i("G","onresume");
		checkBalance();
	}
	private void init_patient() {

		Bundle b=new Bundle();
		b=getIntent().getExtras();
		Time fasting_since=new Time();
		fasting_since.set(b.getLong("fasting_millis"));
		currentPatient=new Patient(b.getString("name"),b.getDouble("weight"),fasting_since);

		if(b.getBoolean("resume")){
			HAS_THIRST=true;
			try {
				String filename=b.getString("filename");
				ObjectInputStream ois=new ObjectInputStream(new FileInputStream(getFilesDir()+"/"+filename));
				currentPatient=(Patient)ois.readObject();
				balanceIn_EntryArrayList=(ArrayList<BalanceEntry>)ois.readObject();
				balanceOut_EntryArrayList=(ArrayList<BalanceEntry>)ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				Toast.makeText(this,getResources().getString(R.string.patientFileNotFound), Toast.LENGTH_SHORT).show();
				finish();
			} catch (IOException e) {
				Toast.makeText(this,getResources().getString(R.string.patientFileError), Toast.LENGTH_SHORT).show();
				finish();
			} catch (ClassNotFoundException e) {
				Toast.makeText(this,getResources().getString(R.string.patientClassNotFound), Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		else
			HAS_THIRST=false;

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void init_Listeners(){

		in_btn_Listener=new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!MENU_SHOWING){
					dropDownMenu(inputChoice_ListView);
				}
				else{
					pullUpMenu();
				}
			}
		};

		out_btn_Listener=new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!MENU_SHOWING){
					dropDownMenu(outputChoice_ListView);
					MENU_SHOWING=true;
				}
				else{
					pullUpMenu();
					MENU_SHOWING=false;
				}
			}				
		};

		inputChoice_ItemClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String [] choices=getResources().getStringArray(R.array.fluidInputs);

				if(position<8){

					io_layout.setAlpha(0f);
					container_RelativeLayout.removeAllViews();
					GAnimator.fastFade(io_layout, true);

					container_RelativeLayout.addView((io_layout));
					((TextView)io_layout.findViewById(R.id.io_title)).setText(choices[position]);
					((TextView)io_layout.findViewById(R.id.io_title)).setTypeface(tf);
					((TextView)io_layout.findViewById(R.id.io_edit_ok_btn)).setOnClickListener(io_ok_btn_inputClickListener);
					entryTitleView=((TextView)io_layout.findViewById(R.id.io_title));
					io_amount_et=(EditText)io_layout.findViewById(R.id.io_edit_amount_et);
					io_amount_et.requestFocus();
				}
				else{

					io_edit_layout.setAlpha(0f);

					container_RelativeLayout.removeAllViews();

					GAnimator.fastFade(io_edit_layout, true);

					container_RelativeLayout.addView((io_edit_layout));
					((TextView)io_edit_layout.findViewById(R.id.io_edit_ok_btn)).setOnClickListener(io_ok_btn_inputClickListener);
					((TextView)io_edit_layout.findViewById(R.id.io_edit_title_et)).setTypeface(tf);
					entryTitleView=((TextView)io_edit_layout.findViewById(R.id.io_edit_title_et));
					io_amount_et=(EditText)io_edit_layout.findViewById(R.id.io_edit_amount_et);
					io_amount_et.requestFocus();
				}

				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(io_amount_et, 0);
			}
		};

		outputChoice_ItemClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final String [] choices=getResources().getStringArray(R.array.fluidOutputs);
				if(position>0){
					container_RelativeLayout.removeAllViews();

					io_layout.setAlpha(0f);

					container_RelativeLayout.addView((io_layout));

					GAnimator.fastFade(io_layout, true);

					((TextView)io_layout.findViewById(R.id.io_title)).setText(choices[position]);
					((TextView)io_layout.findViewById(R.id.io_title)).setTypeface(tf);
					((TextView)io_layout.findViewById(R.id.io_edit_ok_btn)).setOnClickListener(io_ok_btn_outputClickListener);
					entryTitleView=((TextView)io_layout.findViewById(R.id.io_title));
					io_amount_et=(EditText)io_layout.findViewById(R.id.io_edit_amount_et);
					io_amount_et.requestFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(io_amount_et, 0);
				}
				else{
					container_RelativeLayout.removeAllViews();
					now.setToNow();
					((TimePicker)incision_layout.findViewById(R.id.incision_timePicker)).setCurrentHour(now.hour);
					((TimePicker)incision_layout.findViewById(R.id.incision_timePicker)).setCurrentMinute(now.minute);
					incisionChoice_ListView.setAlpha(0f);

					container_RelativeLayout.addView((incisionChoice_ListView));
					GAnimator.fastFade(incisionChoice_ListView, true);
				}
			}
		};

		incision_edit_ok_btn_ClickListener=new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					rate=Double.parseDouble(incision_edit_et.getText().toString());
					entryName=(getResources().getString(R.string.incision)+", "+(int)rate+" ml/kg·h");
					Time incisionTime=new Time();
					incisionTime.setToNow();
					incisionTime.hour=incision_edit_TimePicker.getCurrentHour();
					incisionTime.minute=incision_edit_TimePicker.getCurrentMinute();
					now.setToNow();
					if(!incisionTime.after(now) && !incisionTime.before(last_incision)){
						BalanceEntry incision=new BalanceEntry(entryName,incisionTime,true,(currentPatient.getWeight()/3600d)*rate);
						freeze_other_incisions(incisionTime);
						balanceOut_EntryArrayList.add(incision);
						balanceOut_ListView_Adapter.add(incision.getEntryString(),false);
						balanceOut_ListView_Adapter.notifyDataSetChanged();
						pullUpMenu();
						synchronized (balanceMutex) {
							checkBalance();
						}
						last_incision.set(incisionTime);
					}
					else{
						Toast.makeText(BalanceActivity.this,getResources().getString(R.string.incisionTimeErrorString), Toast.LENGTH_SHORT).show();
						if(API11)
							GAnimator.pulse(incision_edit_TimePicker, 1);
						else{
							GAnimator.pulseOldApi(incision_edit_TimePicker, 1);
						}

					}
				} catch (NumberFormatException e) {
					Toast.makeText(BalanceActivity.this,getResources().getString(R.string.incisionNameErrorString), Toast.LENGTH_SHORT).show();
					if(API11)
						GAnimator.pulse(incision_edit_et, 1);
					else{
						GAnimator.pulseOldApi(incision_edit_et, 1);
					}
				}
			}
		};


		incisionChoice_ItemClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String [] choices=getResources().getStringArray(R.array.incisions);
				final String [] rates=getResources().getStringArray(R.array.incisionModifiers);

				container_RelativeLayout.removeAllViews();
				incision_title.setTypeface(tf);

				if(position==5){
					incision_edit_title.setText(getResources().getString(R.string.incision));
					incision_edit_et.setText("");
					container_RelativeLayout.addView(incision_edit_layout);
				}
				else{
					rate=Double.parseDouble(rates[position]); //TODO qui!!!
					incision_title.setText(getResources().getString(R.string.incision)+", "+choices[position]);
					container_RelativeLayout.addView(incision_layout);
				}
			}
		};


		io_ok_btn_inputClickListener=new OnClickListener() {

			@Override
			public void onClick(View v) {
				entryName=entryTitleView.getText().toString();
				if(entryName.matches("")){
					Toast.makeText(BalanceActivity.this, getResources().getString(R.string.entryNameErrorString), Toast.LENGTH_SHORT).show();
					if(API11)
						GAnimator.pulse(entryTitleView,1);
					else{
						GAnimator.pulseOldApi(entryTitleView,1);
					}
				}
				else{
					try {
						amount=Double.parseDouble(io_amount_et.getText().toString());

						now.setToNow();
						toAdd=new BalanceEntry(entryName, amount, now, true);

						balanceIn_EntryArrayList.add(toAdd);
						balanceIn_ListView_Adapter.add(toAdd.getEntryString());
						balanceIn_ListView_Adapter.notifyDataSetChanged();

						io_amount_et.setText("");
						entryTitleView.setText("");

					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					pullUpMenu();
					synchronized (balanceMutex) {
						checkBalance();
					}
				}
			}
		};

		io_ok_btn_outputClickListener= new OnClickListener() {

			@Override
			public void onClick(View v) {
				entryName=entryTitleView.getText().toString();
				try {
					amount=Double.parseDouble(io_amount_et.getText().toString());

					now.setToNow();
					toAdd=new BalanceEntry(entryName, amount, now, true);

					balanceOut_EntryArrayList.add(toAdd);
					balanceOut_ListView_Adapter.add(toAdd.getEntryString(),false);
					balanceOut_ListView_Adapter.notifyDataSetChanged();

					io_amount_et.setText("");
					entryTitleView.setText("");

				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				pullUpMenu();
				synchronized (balanceMutex) {
					checkBalance();
				}
			}
		};



		incision_ok_btn_ClickListener=new OnClickListener() {

			@Override
			public void onClick(View v) {
				entryName=incision_title.getText().toString();
				Time incisionTime=new Time();
				incisionTime.setToNow();
				incisionTime.hour=incision_TimePicker.getCurrentHour();
				incisionTime.minute=incision_TimePicker.getCurrentMinute();

				now.setToNow();
				if(!incisionTime.after(now) && !incisionTime.before(last_incision)){
					BalanceEntry incision=new BalanceEntry(entryName,incisionTime,true,(currentPatient.getWeight()/3600d)*rate);
					freeze_other_incisions(incisionTime);
					balanceOut_EntryArrayList.add(incision);
					balanceOut_ListView_Adapter.add(incision.getEntryString(),false);
					balanceOut_ListView_Adapter.notifyDataSetChanged();
					pullUpMenu();
					synchronized (balanceMutex) {
						checkBalance();
					}

					last_incision.set(incisionTime);
				}
				else{
					Toast.makeText(BalanceActivity.this,getResources().getString(R.string.incisionTimeErrorString), Toast.LENGTH_SHORT).show();
					if(API11)
						GAnimator.pulse(incision_TimePicker, 1);
					else{
						GAnimator.pulseOldApi(incision_TimePicker, 1);
					}
				}
			}
		};

		balanceIO_ItemLongClickListener=new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean MODIFIABLE=true;

				if(parent.equals(balanceOut_listview) && balanceOut_EntryArrayList.get(position).isDynamic())
					MODIFIABLE=false;

				if(MODIFIABLE){	
					final GiulioAlertDialog ga=new GiulioAlertDialog(thiscontext);
					ga.show();
					ga.setContentView(R.layout.io_edit_layout);
					if(parent.equals(balanceIn_listview))
						temp=((BalanceEntry)balanceIn_EntryArrayList.get(position));
					else
						temp=((BalanceEntry)balanceOut_EntryArrayList.get(position));

					String name=temp.getEntryName();
					ga.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					((EditText)ga.findViewById(R.id.io_edit_title_et)).setText(name);
					((EditText)ga.findViewById(R.id.io_edit_amount_et)).setText(String.format("%6.2f",temp.getAmount()));
					((EditText)ga.findViewById(R.id.io_edit_amount_et)).setTextSize(TypedValue.COMPLEX_UNIT_SP, thiscontext.getResources().getDimension(R.dimen.text_big));
					((EditText)ga.findViewById(R.id.io_edit_amount_et)).setMaxEms(5);
					((EditText)ga.findViewById(R.id.io_edit_amount_et)).setMinEms(5);

					((TextView)ga.findViewById(R.id.io_edit_ok_btn)).setClickable(true);
					((TextView)ga.findViewById(R.id.io_edit_ml)).setVisibility(View.GONE);
					((TextView)ga.findViewById(R.id.io_edit_ok_btn)).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							temp.setName(((EditText)ga.findViewById(R.id.io_edit_title_et)).getText().toString());
							temp.setAmount(Double.parseDouble(((EditText)ga.findViewById(R.id.io_edit_amount_et)).getText().toString()));
							synchronized (balanceMutex) {
								checkBalance();
							}

							ga.dismiss();
						}
					});
					ga.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
					WindowManager.LayoutParams lp = ga.getWindow().getAttributes();  
					lp.dimAmount=0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
					ga.getWindow().setAttributes(lp);

				}
				return true;
			}
		};

	}

	private void init(){

		tf=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		title_RelativeLayout=(RelativeLayout)findViewById(R.id.title_rlayout);
		global_RelativeLayout=(RelativeLayout)findViewById(R.id.global_layout);
		container_RelativeLayout=(RelativeLayout)findViewById(R.id.container_layout);
		incision_layout=(RelativeLayout)getLayoutInflater().inflate(R.layout.incision_layout,container_RelativeLayout,false);
		incision_edit_layout=(RelativeLayout)getLayoutInflater().inflate(R.layout.incision_edit_layout,container_RelativeLayout,false);
		io_layout= (RelativeLayout)getLayoutInflater().inflate(R.layout.io_layout,container_RelativeLayout,false);
		io_edit_layout= (RelativeLayout)getLayoutInflater().inflate(R.layout.io_edit_layout,container_RelativeLayout,false);
		clock=(DigitalClock)findViewById(R.id.clock);
		clock.setTypeface(tf);
		title=(TextView)findViewById(R.id.title);
		title.setTypeface(tf);
		in_btn=(TextView)findViewById(R.id.in_btn);
		in_btn.setTypeface(tf);
		out_btn=(TextView)findViewById(R.id.out_btn);
		out_btn.setTypeface(tf);
		updated_tv=(TextView)findViewById(R.id.updated);
		updated_tv.setTypeface(tf);

		balance_tv=(TextView)findViewById(R.id.balance_tv);
		balance_tv.setTypeface(tf);	

		in_btn.setOnClickListener(in_btn_Listener);
		out_btn.setOnClickListener(out_btn_Listener);

		choiceIn_ArrayList=new ArrayList<String>();
		for(int i=0;i<getResources().getStringArray(R.array.fluidInputs).length;i++){
			choiceIn_ArrayList.add(getResources().getStringArray(R.array.fluidInputs)[i]);
		}

		choiceOut_ArrayList=new ArrayList<String>();
		for(int i=0;i<getResources().getStringArray(R.array.fluidOutputs).length;i++){
			choiceOut_ArrayList.add(getResources().getStringArray(R.array.fluidOutputs)[i]);
		}

		choiceIn_ListView_Adapter=new ArrayAdapter<String>(BalanceActivity.this,R.layout.choice_item,getResources().getStringArray(R.array.fluidInputs));
		inputChoice_ListView=new ListView(BalanceActivity.this);
		inputChoice_ListView.setSelector(android.R.color.transparent);
		inputChoice_ListView.setAdapter(choiceIn_ListView_Adapter);
		inputChoice_ListView.setDividerHeight(DIV_HEIGTH);
		inputChoice_ListView.setOnItemClickListener(inputChoice_ItemClickListener);

		choiceOut_ListView_Adapter=new ArrayAdapter<String>(BalanceActivity.this,R.layout.choice_item,getResources().getStringArray(R.array.fluidOutputs));
		outputChoice_ListView=new ListView(BalanceActivity.this);
		outputChoice_ListView.setSelector(android.R.color.transparent);
		outputChoice_ListView.setAdapter(choiceOut_ListView_Adapter);
		outputChoice_ListView.setDividerHeight(DIV_HEIGTH);
		outputChoice_ListView.setOnItemClickListener(outputChoice_ItemClickListener);

		incisionChoice_ArrayList=new ArrayList<IncisionChoiceEntry>();
		for(int i=0;i<getResources().getStringArray(R.array.incisions).length;i++){
			incisionChoice_ArrayList.add(new IncisionChoiceEntry(getResources().getStringArray(R.array.incisions)[i],getResources().getStringArray(R.array.incisionsDescriptions)[i]));
		}

		incisionChoice_adapter=new TwoLinesAdapter(BalanceActivity.this, incisionChoice_ArrayList);
		incisionChoice_ListView=new ListView(BalanceActivity.this);
		incisionChoice_ListView.setSelector(android.R.color.transparent);

		incisionChoice_ListView.setAdapter(incisionChoice_adapter);
		incisionChoice_ListView.setDividerHeight(DIV_HEIGTH);
		incisionChoice_ListView.setOnItemClickListener(incisionChoice_ItemClickListener);

		if(balanceIn_EntryArrayList==null)
			balanceIn_EntryArrayList=new ArrayList<BalanceEntry>();
		if(balanceOut_EntryArrayList==null)
			balanceOut_EntryArrayList=new ArrayList<BalanceEntry>();

		balanceIn_ArrayList=new ArrayList<String>();
		balanceIn_ListView_Adapter=new ArrayAdapter<>(this, R.layout.list_item, balanceIn_ArrayList);

		balanceOut_ArrayList=new ArrayList<String>();
		balanceOut_ListView_Adapter=new GiulioAdapter(this, R.layout.list_item, balanceOut_ArrayList);

		balanceIn_listview=(ListView)findViewById(R.id.balanceIn_listview);
		balanceIn_listview.setAdapter(balanceIn_ListView_Adapter);
		balanceIn_listview.setDivider(getResources().getDrawable(R.drawable.divider));
		balanceIn_listview.setSelector(android.R.color.transparent);

		balanceOut_listview=(ListView)findViewById(R.id.balanceOut_listview);
		balanceOut_listview.setAdapter(balanceOut_ListView_Adapter);
		balanceOut_listview.setDivider(getResources().getDrawable(R.drawable.divider));
		balanceOut_listview.setSelector(android.R.color.transparent);

		balanceIn_listview.setOnItemLongClickListener(balanceIO_ItemLongClickListener);
		balanceOut_listview.setOnItemLongClickListener(balanceIO_ItemLongClickListener);

		SwipeDismissListViewTouchListener inBalanceListView_TouchListener=new SwipeDismissListViewTouchListener(balanceIn_listview, new SwipeDismissListViewTouchListener.DismissCallbacks() {
			@Override
			public void onDismiss(ListView listView, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					balanceIn_ListView_Adapter.remove(balanceIn_ListView_Adapter.getItem(position));
					balanceIn_EntryArrayList.remove(position);
				}
				balanceIn_ListView_Adapter.notifyDataSetChanged();
				synchronized (balanceMutex) {
					checkBalance();
				}


			}
			@Override
			public boolean canDismiss(int position) {
				return balanceIn_EntryArrayList.get(position).isRemovable();
			}
		});
		balanceIn_listview.setOnTouchListener(inBalanceListView_TouchListener);
		balanceIn_listview.setOnScrollListener(inBalanceListView_TouchListener.makeScrollListener());

		SwipeDismissListViewTouchListener outBalanceListView_TouchListener=new SwipeDismissListViewTouchListener(balanceOut_listview, new SwipeDismissListViewTouchListener.DismissCallbacks() {
			@Override
			public void onDismiss(ListView listView, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					balanceOut_ListView_Adapter.remove(balanceOut_ListView_Adapter.getItem(position));
					balanceOut_EntryArrayList.remove(position);
				}
				balanceOut_ListView_Adapter.notifyDataSetChanged();
				synchronized (balanceMutex) {
					checkBalance();
				}

			}
			@Override
			public boolean canDismiss(int position) {
				return balanceOut_EntryArrayList.get(position).isRemovable();
			}
		});
		balanceOut_listview.setOnTouchListener(outBalanceListView_TouchListener);
		balanceOut_listview.setOnScrollListener(outBalanceListView_TouchListener.makeScrollListener());

		incision_ok_btn=(TextView)incision_layout.findViewById(R.id.incision_ok_btn);
		incision_TimePicker=(TimePicker)incision_layout.findViewById(R.id.incision_timePicker);
		incision_TimePicker.setIs24HourView(true);

		incision_title=(TextView)incision_layout.findViewById(R.id.incision_title);

		incision_ok_btn.setOnClickListener(incision_ok_btn_ClickListener);

		incision_edit_ok_btn=(TextView)incision_edit_layout.findViewById(R.id.incision_edit_ok_btn);
		incision_edit_et=(EditText)incision_edit_layout.findViewById(R.id.incision_edit_et);
		incision_edit_et.setTypeface(tf);
		incision_edit_ok_btn.setOnClickListener(incision_edit_ok_btn_ClickListener);
		incision_edit_TimePicker=(TimePicker)incision_edit_layout.findViewById(R.id.incision_edit_timePicker);
		incision_edit_TimePicker.setIs24HourView(true);
		incision_edit_title=(TextView)incision_edit_layout.findViewById(R.id.incision_edit_title);
		incision_edit_title.setTypeface(tf);

		last_incision.set(Time.EPOCH_JULIAN_DAY);

		if(!HAS_THIRST)
			set_thirst(currentPatient.getFasting_since());
	}

	private void dropDownMenu(ListView menu){

		MENU_SHOWING=true;
		container_RelativeLayout.setVisibility(View.INVISIBLE);
		container_RelativeLayout.removeAllViews();

		container_RelativeLayout.addView(menu);

		RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)container_RelativeLayout.getLayoutParams();
		params.height=(global_RelativeLayout.getHeight()-title_RelativeLayout.getHeight()-updated_tv.getHeight());
		//		params.topMargin=-container_RelativeLayout.getHeight()

		container_RelativeLayout.setLayoutParams(params);

		container_RelativeLayout.setY(-container_RelativeLayout.getHeight());

		container_RelativeLayout.setVisibility(View.VISIBLE);

		if(API11){
			GAnimator.slowFadeTop(container_RelativeLayout,true);
			GAnimator.slowTranslateViewToY(title_RelativeLayout, (global_RelativeLayout.getHeight()-title_RelativeLayout.getHeight()-updated_tv.getHeight()));
		}
		else{
			GAnimator.slowFadeTopOldApi(container_RelativeLayout,true);
			GAnimator.slowTranslateViewToYOldApi(title_RelativeLayout, (global_RelativeLayout.getHeight()-title_RelativeLayout.getHeight()-updated_tv.getHeight()));

		}
		MENU_SHOWING=true;
	}

	private void pullUpMenu(){

		if(API11){
			GAnimator.slowFadeTop(container_RelativeLayout,false,true);
			GAnimator.slowTranslateViewToY(title_RelativeLayout, 0);
		}
		else{
			GAnimator.slowFadeTopOldApi(container_RelativeLayout,false,true);
			GAnimator.slowTranslateViewToYOldApi(title_RelativeLayout, 0);
		}

		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(container_RelativeLayout.getWindowToken(), 0);
		MENU_SHOWING=false;
	}

	public void onBackPressed() {
		if(MENU_SHOWING)
			pullUpMenu();
		else
			if(System.currentTimeMillis()-TIMEPRESSED<2000){
				RUNNING=false;
				checkBalance();
				super.onBackPressed();
			}
			else{
				Toast.makeText(this, getResources().getString(R.string.tap), Toast.LENGTH_SHORT).show();
				TIMEPRESSED=System.currentTimeMillis();
			}
	};

	private void set_thirst(Time fast_since){

		BalanceEntry thirst=new BalanceEntry(getResources().getString(R.string.thirst),fast_since,false,(currentPatient.getWeight()/3600d)); 
		balanceOut_EntryArrayList.add(thirst);
		balanceOut_ListView_Adapter.add(thirst.getEntryString(),false);
		balanceOut_ListView_Adapter.notifyDataSetChanged();
	}

	private void freeze_other_incisions(Time frozenTime){

		for(int i=0;i<balanceOut_EntryArrayList.size();i++){
			if(balanceOut_EntryArrayList.get(i).isDynamic() && balanceOut_EntryArrayList.get(i).isRemovable()){
				balanceOut_EntryArrayList.get(i).freeze(frozenTime);
			}
		}
	}

	private void start_updater(){
		updater_thread=new Thread(new Runnable() {
			@Override
			public void run() {
				while(RUNNING){
					uiHandle.post(new Runnable() {
						@Override
						public void run() {
							synchronized (balanceMutex) {
								checkBalance();	
							}
						}
					});
					try {
						Thread.sleep(UPDATER_SLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		updater_thread.start();
	}

	private void updateTitle(){
		now.setToNow();
		double fast=(now.toMillis(false)-currentPatient.getFasting_since().toMillis(false))/3600000d;
		title.setText(currentPatient.getName()+", "+currentPatient.getWeight()+" kg");

		if(fast<1)
			updated_tv.append("\n"+getResources().getString(R.string.fasting_less1hr));
		else if(fast<2)
			updated_tv.append("\n"+getResources().getString(R.string.fasting_more1hr));
		else 
			updated_tv.append("\n"+getResources().getString(R.string.fasting_more)+(int)fast+getResources().getString(R.string.hour));
	}


	private void checkBalance(){

		balance=0;
		balanceIn_ListView_Adapter.clear();
		for(int i=0;i<balanceIn_EntryArrayList.size();i++){
			balanceIn_EntryArrayList.get(i).update();
			balanceIn_ListView_Adapter.add(balanceIn_EntryArrayList.get(i).getEntryString());
			balance+=balanceIn_EntryArrayList.get(i).getAmount();
		}

		balanceIn_ListView_Adapter.notifyDataSetChanged();

		balanceOut_ListView_Adapter.clear();
		for(int i=0;i<balanceOut_EntryArrayList.size();i++){
			balanceOut_EntryArrayList.get(i).update();
			balanceOut_ListView_Adapter.add(balanceOut_EntryArrayList.get(i).getEntryString(),balanceOut_EntryArrayList.get(i).isFrozen());
			balance-=balanceOut_EntryArrayList.get(i).getAmount();
		}

		balanceOut_ListView_Adapter.notifyDataSetChanged();

		balance_tv.setText(Html.fromHtml("<big>"+String.format("%6.2f",balance)+"</big><small> mL</small>"));
		if(Math.abs(balance)>WARN_THRESH && Math.abs(balance)<DANG_THRESH)
			balance_tv.setTextColor(getResources().getColor(R.color.warning));
		else if(Math.abs(balance)>DANG_THRESH)
			balance_tv.setTextColor(getResources().getColor(R.color.danger));
		else
			balance_tv.setTextColor(getResources().getColor(R.color.lightgrey));

		now.setToNow();
		updated_tv.setText(getResources().getString(R.string.updated)+String.format("%02d",now.hour)+":"+String.format("%02d",now.minute));
		updateTitle();
		currentPatient.setLastUpdated(now);
		save_state();
	}

	public void about(View v){
		GiulioAlertDialog ga=new GiulioAlertDialog(this);
		ga.show();
		ga.setContentView(R.layout.about_us_layout);
	}


	final static private int UPDATER_SLEEP=30000;
	final static private double WARN_THRESH=350d;
	final static private double DANG_THRESH=350d;

	private int toDelete;
	Object balanceMutex=new Object();
	boolean SHOW_INCISION=false;
	boolean MENU_SHOWING=false;
	int choiceType=3;
	Typeface tf;
	BalanceEntry temp;
	DigitalClock clock;
	TextView title,in_btn,out_btn,balance_tv,io_ok_btn,entryTitleView,updated_tv,incision_ok_btn,incision_title, incision_edit_ok_btn, incision_edit_title;
	ListView balanceIn_listview, balanceOut_listview;
	RelativeLayout title_RelativeLayout, global_RelativeLayout, container_RelativeLayout,io_layout,io_edit_layout, incision_layout, incision_edit_layout;
	ArrayAdapter<String> choiceIn_ListView_Adapter, choiceOut_ListView_Adapter, balanceIn_ListView_Adapter;
	GiulioAdapter balanceOut_ListView_Adapter;
	TwoLinesAdapter incisionChoice_adapter;
	ArrayList<IncisionChoiceEntry> incisionChoice_ArrayList;
	ArrayList<String> choiceIn_ArrayList,choiceOut_ArrayList, balanceIn_ArrayList, balanceOut_ArrayList;
	ArrayList<BalanceEntry> balanceIn_EntryArrayList,balanceOut_EntryArrayList;
	OnClickListener in_btn_Listener, out_btn_Listener;
	ListView inputChoice_ListView, outputChoice_ListView, incisionChoice_ListView;
	OnItemClickListener inputChoice_ItemClickListener, outputChoice_ItemClickListener, incisionChoice_ItemClickListener;
	OnItemLongClickListener balanceIO_ItemLongClickListener;
	OnClickListener io_ok_btn_inputClickListener, io_ok_btn_outputClickListener,incision_ok_btn_ClickListener, incision_edit_ok_btn_ClickListener;
	String entryName;
	double rate, balance,amount=0;
	double thirst=0;
	BalanceEntry toAdd;
	EditText io_amount_et, incision_edit_et;
	Time now=new Time();
	Time last_incision=new Time();
	long TIMEPRESSED=0;
	TimePicker incision_TimePicker, incision_edit_TimePicker;
	static boolean RUNNING=true;
	boolean HAS_THIRST=false;
	Thread updater_thread;
	Handler uiHandle=new Handler();
	Patient currentPatient;
	DisplayMetrics dm=new DisplayMetrics();
	String toRemove;
	Context thiscontext=this;

}
