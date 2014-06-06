package giu.fbalance;

//import com.nineoldandroids.view.ViewHelper;

import giu.pdfWrapper.GiulioPDFjetWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity {
	GiulioAlertDialog ga;
	private boolean API11 = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_menu_layout);
		API11 = (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB);
//		if (!API11)
//			ViewHelper
//			.setAlpha(
//					((TextView) findViewById(R.id.mainmenu_beta_banner)),
//					0.05f);

		new_patient = (TextView) findViewById(R.id.main_menu_new_px);
		resume_patient = (TextView) findViewById(R.id.main_menu_resume_px);
		title = (TextView) findViewById(R.id.main_menu_welcome);
		tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

		new_patient.setTypeface(tf);
		resume_patient.setTypeface(tf);
		title.setTypeface(tf);

		new_patient.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainMenu.this, PatientActivity.class);
				startActivity(i);
			}
		});

		main_relative_layout = (RelativeLayout) findViewById(R.id.main_menu_main_relative_layout);
		container_linear_layout = (LinearLayout) findViewById(R.id.main_menu_container);

		patients = (ListView) findViewById(R.id.main_menu_filelistview);

		fileNames_ArrayList = getListNames(getFilesDir().getParentFile());
		names_ArrayList = getListNamesNoExtensions(getFilesDir()
				.getParentFile());
		listview_Adapter = new ArrayAdapter<>(thiscontext,
				R.layout.filelist_item, names_ArrayList);
		patients.setAdapter(listview_Adapter);
		patients.setDividerHeight(BalanceActivity.DIV_HEIGTH);
		listview_Adapter.notifyDataSetChanged();
		ga=new GiulioAlertDialog(thiscontext);
		ga.setLayout_id(R.layout.resume_choice_layout);
		
		resume_clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {

				fileNames_ArrayList = getListNames(getFilesDir()
						.getParentFile());
				names_ArrayList = getListNamesNoExtensions(getFilesDir()
						.getParentFile());
				listview_Adapter = new ArrayAdapter<>(thiscontext,
						R.layout.filelist_item, names_ArrayList);
				patients.setAdapter(listview_Adapter);
				patients.setDividerHeight(BalanceActivity.DIV_HEIGTH);
				listview_Adapter.notifyDataSetChanged();

				if (fileNames_ArrayList.size() == 0) {
				} else {
					new_patient.setEnabled(false);
					new_patient.setTextColor(getResources().getColor(
							R.color.lightergrey));

					if (API11)
						showPatientsToResume();
					else
						showPatientsToResumeOldApi();

					v.setClickable(false);

					patients.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							ga.show();
							((TextView)ga.findViewById(R.id.resume_title)).setText(fileNames_ArrayList.get(position).subSequence(0, fileNames_ArrayList.get(position).length()-5));

							WindowManager.LayoutParams lp = ga.getWindow().getAttributes();  
							lp.dimAmount=0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
							ga.getWindow().setAttributes(lp);
							thePosition=position;
							toDelete=names_ArrayList.get(position);
//							
							return true;
						}

					});
					patients.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(thiscontext,
									BalanceActivity.class);
							Bundle b = new Bundle();
							b.putString("filename", fileNames_ArrayList.get(position));
							b.putBoolean("resume", true);
							i.putExtras(b);
							startActivity(i);

						}
					});
				}
			}
		};
		resume_patient.setOnClickListener(resume_clickListener);
		SharedPreferences settings = getSharedPreferences("fbpref",
				Context.MODE_PRIVATE);
		boolean DISC_ACC = settings.getBoolean("disc_accepted", false);

		if (!DISC_ACC) {
			disclaimer = new GiulioAlertDialog(thiscontext);
			disclaimer.show();
			disclaimer.setCancelable(false);
			disclaimer.setContentView(R.layout.disclaimer_layout);
			TextView ok = (TextView) disclaimer
					.findViewById(R.id.disclaimer_ok_btn);
			ok.setClickable(true);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences settings = getSharedPreferences("fbpref",
							Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("disc_accepted", true);
					editor.commit();
					disclaimer.cancel();
				}
			});

			TextView no = (TextView) disclaimer
					.findViewById(R.id.disclaimer_no_btn);
			no.setClickable(true);
			no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});

		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onStart() {
		super.onStart();
		title.setVisibility(View.GONE);
		new_patient.setVisibility(View.GONE);
		resume_patient.setVisibility(View.GONE);

		logo = new ImageView(this);
		logo.setImageDrawable(getResources().getDrawable(R.drawable.icon));

		main_relative_layout.addView(logo);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) logo
				.getLayoutParams();
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		logo.setLayoutParams(params);

		if (API11) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					GAnimator.rotate(logo);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							main_relative_layout.removeView(logo);
							title.setVisibility(View.VISIBLE);
							new_patient.setVisibility(View.VISIBLE);
							resume_patient.setVisibility(View.VISIBLE);

							title.setRotationY(90f);
							new_patient.setRotationY(90f);
							resume_patient.setRotationY(90f);

							GAnimator.rotateBack(title);
							GAnimator.rotateBack(new_patient);
							GAnimator.rotateBack(resume_patient);
						}
					}, GAnimator.LONG_ANIM);
				}
			}, 1000);
		}
	}

	private List<String> getListNames(File parentDir) {
		ArrayList<String> inFiles = new ArrayList<String>();
		File[] files = parentDir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListNames(file));
			} else {
				if (file.getName().endsWith(".data")) {
					inFiles.add(file.getName());
				}
			}
		}
		return inFiles;
	}

	private List<String> getListNamesNoExtensions(File parentDir) {
		List<String> inFiles = new ArrayList<String>();

		inFiles = getListNames(parentDir);
		ArrayList<String> outFiles = new ArrayList<String>();

		for (int i = 0; i < inFiles.size(); i++) {
			outFiles.add(inFiles.get(i).substring(0,
					inFiles.get(i).length() - 5));
		}
		return outFiles;
	}

	@Override
	protected void onResume() {
		if (MENU_SHOWING)
			hidePatientsToResume();
		super.onResume();

	}

	public void onBackPressed() {

		if (MENU_SHOWING) {
			hidePatientsToResume();
		} else {
			if (System.currentTimeMillis() - TIMEPRESSED < 2000 && !EXITING) {
				if (API11)
					gracefulExit();
				else
					gracefulExitOldApi();
			} else {
				Toast.makeText(this, getResources().getString(R.string.tap_main),
						Toast.LENGTH_SHORT).show();
				TIMEPRESSED = System.currentTimeMillis();
			}
		}
	};

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showPatientsToResume() {
		MENU_SHOWING = true;
		container_linear_layout.getLayoutParams().height = (main_relative_layout
				.getHeight() - (int) title.getY() - title.getHeight() - getPixels(
						TypedValue.COMPLEX_UNIT_SP, 0));
		container_linear_layout.setVisibility(View.VISIBLE);

		patients.setAlpha(0f);
		patients.setVisibility(View.VISIBLE);

		GAnimator.slowFade(title, false);
		GAnimator.slowFade(new_patient, false);
		GAnimator.toggleButtonTitle(resume_patient);				



		resume_patient_pos = GAnimator.slowTranslateViewToY(resume_patient,
				title.getY());
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				GAnimator.slowFade(patients, true);
			}
		}, GAnimator.LONG_ANIM);


	}

	private void showPatientsToResumeOldApi() {
//		MENU_SHOWING = true;
//		container_linear_layout.getLayoutParams().height = (main_relative_layout
//				.getHeight() - (int) ViewHelper.getY(title) - title.getHeight() - getPixels(
//						TypedValue.COMPLEX_UNIT_SP, 0));
//		container_linear_layout.setVisibility(View.VISIBLE);
//
//		ViewHelper.setAlpha(patients, 0f);
//		patients.setVisibility(View.VISIBLE);
//
//		GAnimator.slowFadeOldApi(title, false);
//		GAnimator.slowFadeOldApi(new_patient, false);
//		GAnimator.slowFadeOldApi(patients, true);
//		GAnimator.toggleButtonTitle(resume_patient);
//
//		resume_patient_pos = GAnimator.slowTranslateViewToYOldApi(
//				resume_patient, ViewHelper.getY(title));
	}

	private void hidePatientsToResume() {

		new_patient.setEnabled(true);
		new_patient.setTextColor(getResources().getColor(R.color.lightgrey));

		if (API11) {
			GAnimator.slowFade(title, true);
			GAnimator.slowFade(new_patient, true);
			GAnimator.slowFadeWithEndAction(patients, false, new Runnable() {
				@Override
				public void run() {
					patients.setVisibility(View.GONE);
					container_linear_layout.setVisibility(View.GONE);
				}
			});
			GAnimator.slowTranslateViewToY(resume_patient, resume_patient_pos);

		} else {

			GAnimator.slowFadeOldApi(title, true);
			GAnimator.slowFadeOldApi(new_patient, true);

			GAnimator.slowFadeWithEndActionOldApi(patients, false,
					new Runnable() {
				@Override
				public void run() {
					patients.setVisibility(View.GONE);
					container_linear_layout.setVisibility(View.GONE);
				}
			});
			GAnimator.slowTranslateViewToYOldApi(resume_patient,
					resume_patient_pos);
		}

		GAnimator.toggleButtonTitle(resume_patient);
		resume_patient.setClickable(true);
		MENU_SHOWING = false;
	}

	private int getPixels(int unit, float size) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return (int) TypedValue.applyDimension(unit, size, metrics);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void gracefulExit() {
		EXITING=true;
		title.setText(thiscontext.getResources().getString(R.string.goodbye));
		GAnimator.slowFade(new_patient, false);
		GAnimator.slowFadeWithEndAction(resume_patient, false, new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					GAnimator.slowFade(title,false);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, GAnimator.LONG_ANIM);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		GAnimator.slowTranslateViewToY(title, resume_patient.getY());
	}

	private void gracefulExitOldApi() {
//		title.setText(thiscontext.getResources().getString(R.string.goodbye));
//		GAnimator.slowFadeOldApi(new_patient, false);
//		GAnimator.slowFadeWithEndActionOldApi(resume_patient, false,
//				new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(800);
//					GAnimator.slowFadeWithEndActionOldApi(title, false,
//							new Runnable() {
//						@Override
//						public void run() {
//							MainMenu.this.finish();
//						}
//					});
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		GAnimator.slowTranslateViewToYOldApi(title,
//				ViewHelper.getY(resume_patient));
	}

	private ArrayList<ArrayList<BalanceEntry>> loadPatient(String filename,Patient p){
		Patient loadedPatient;
		
		ArrayList <ArrayList<BalanceEntry>> out=new ArrayList<ArrayList<BalanceEntry>>();
		try {
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(getFilesDir()+"/"+filename));
			loadedPatient=(Patient)ois.readObject();
			out.add((ArrayList<BalanceEntry>)ois.readObject());
			out.add((ArrayList<BalanceEntry>)ois.readObject());
			ois.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(this,getResources().getString(R.string.patientFileNotFound), Toast.LENGTH_SHORT).show();
			return null;
		} catch (IOException e) {
			Toast.makeText(this,getResources().getString(R.string.patientFileError), Toast.LENGTH_SHORT).show();
			return null;
		} catch (ClassNotFoundException e) {
			Toast.makeText(this,getResources().getString(R.string.patientClassNotFound), Toast.LENGTH_SHORT).show();
			return null;
		}

		p.setName(loadedPatient.getName());
		p.setWeight(loadedPatient.getWeight());
		p.setFastingSince(loadedPatient.getFasting_since());
		p.setLastUpdated(loadedPatient.getLastUpdated());
		return out;
	}

	private String writePatient(String patient){
		
		Patient p=new Patient();
		ArrayList<ArrayList<BalanceEntry>> balances=loadPatient(patient,p);
//		return GiulioWriter.writePdf(balances.get(0),balances.get(1),p,thiscontext);
		try {
			return GiulioPDFjetWriter.writePdf(balances.get(0),balances.get(1),p,thiscontext);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean writePdfClr(ArrayList<BalanceEntry> inList, ArrayList<BalanceEntry> outList, Patient p,boolean share){
//
//		PDFWriter mPDFWriter = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);
//		mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_BOLDITALIC);
//
//		int LEFTMARGIN=8;
//		int SEPARATOR=13;
//		int FONTSIZE=13;
//		int TOPMARGIN=60;
//		SEPARATOR=(int) (FONTSIZE*1.5);
//		LEFTMARGIN=FONTSIZE;
//		int c=0,d=0;
//		String entryString;
//		
//		mPDFWriter.addText(PaperSize.FOLIO_WIDTH/4, PaperSize.FOLIO_HEIGHT-TOPMARGIN, FONTSIZE*2, thiscontext.getResources().getString(R.string.app_name));
//		mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA_BOLDOBLIQUE);
//
//		mPDFWriter.addText(PaperSize.FOLIO_WIDTH/4, PaperSize.FOLIO_HEIGHT-SEPARATOR-TOPMARGIN, FONTSIZE, p.getName());
//		mPDFWriter.addText(PaperSize.FOLIO_WIDTH/4, PaperSize.FOLIO_HEIGHT-SEPARATOR-TOPMARGIN-SEPARATOR, FONTSIZE, p.getWeight()+" kg");
//		mPDFWriter.addText(PaperSize.FOLIO_WIDTH/4, PaperSize.FOLIO_HEIGHT-SEPARATOR-TOPMARGIN-SEPARATOR*2, FONTSIZE, thiscontext.getResources().getString(R.string.fasting_since)+"  "+outList.get(0).getTimeString());
//
//		int LIST_TEXT_START=(int)(TOPMARGIN*3.5);
//				
//		mPDFWriter.addText((LEFTMARGIN*5), PaperSize.FOLIO_HEIGHT-LIST_TEXT_START+FONTSIZE/2, FONTSIZE*2,"IN" );
//		mPDFWriter.addText(PaperSize.FOLIO_WIDTH/2+(LEFTMARGIN*5), PaperSize.FOLIO_HEIGHT-LIST_TEXT_START+FONTSIZE/2, FONTSIZE*2,"OUT" );
//		
//
//		mPDFWriter.addLine(LEFTMARGIN*3,  PaperSize.FOLIO_HEIGHT-TOPMARGIN*3+SEPARATOR+5, PaperSize.FOLIO_WIDTH-LEFTMARGIN*3, PaperSize.FOLIO_HEIGHT-TOPMARGIN*3+SEPARATOR+5);
//		mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.HELVETICA);
//
//		for(BalanceEntry b : inList){
//			c++;
//			entryString=b.getTimeString()+", "+b.getValueString();
//			mPDFWriter.addText((int)(LEFTMARGIN*2.5), PaperSize.FOLIO_HEIGHT-c*SEPARATOR-LIST_TEXT_START, FONTSIZE,entryString );
//		}
//		
//		outList.remove(0);
//		for(BalanceEntry b : outList){
//			d++;
//			entryString=b.getTimeString()+", "+b.getValueString();
//			mPDFWriter.addText(PaperSize.FOLIO_WIDTH/2+(int)(LEFTMARGIN*2.5), PaperSize.FOLIO_HEIGHT-d*SEPARATOR-LIST_TEXT_START, FONTSIZE,entryString );
//		}
//		
//		int ee=Math.max(c,d);
//		
//		mPDFWriter.addRectangle(LEFTMARGIN*2, PaperSize.FOLIO_HEIGHT-(ee+1)*SEPARATOR-LIST_TEXT_START, PaperSize.FOLIO_WIDTH/2-LEFTMARGIN*3, (ee+1)*SEPARATOR);
//		mPDFWriter.addRectangle(PaperSize.FOLIO_WIDTH/2+LEFTMARGIN*2, PaperSize.FOLIO_HEIGHT-(ee+1)*SEPARATOR-LIST_TEXT_START, PaperSize.FOLIO_WIDTH-LEFTMARGIN*2-(PaperSize.FOLIO_WIDTH/2+LEFTMARGIN*2), (ee+1)*SEPARATOR);
//
//		int pageCount = mPDFWriter.getPageCount();
//		for (int i = 0; i < pageCount; i++) {
//			mPDFWriter.setCurrentPage(i);
//			mPDFWriter.addText( PaperSize.FOLIO_WIDTH-40,10, 8, Integer.toString(i + 1) + " / " + Integer.toString(pageCount));
//		}
//		
//        mPDFWriter.addRawContent("0.8 0.8 0.8 rg\n");
//		mPDFWriter.addText(PaperSize.FOLIO_WIDTH/2, 10, 10,"created with FluidBalance Android App");
//
//		String s = mPDFWriter.asString();
//
//		String fileName=p.getName()+".pdf";
//		String pdfContent=s;
//		String encoding="ISO-8859-1";
//
//		File newFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
//		try {
//			newFile.createNewFile();
//
//			try {
//				FileOutputStream pdfFile = new FileOutputStream(newFile);
//				pdfFile.write(pdfContent.getBytes(encoding));
//				pdfFile.close();
//				if(share){
//					Intent i=new Intent(android.content.Intent.ACTION_SEND);
//					i.setType("application/pdf");
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//					Uri u=Uri.fromFile(newFile);
//					i.putExtra(Intent.EXTRA_STREAM, u);
//					startActivity(Intent.createChooser(i, "Come vuoi condividere?"));
//					return true;
//				}
//
//			} catch(FileNotFoundException e) {
//				Log.i("G","ERROR "+Environment.getExternalStorageDirectory()  + "/" + fileName);
//			}
//		} catch(IOException e) {
//			Log.i("G","ERROR a "+Environment.getExternalStorageDirectory()  + "/" + fileName);
//		}
		return false;
		
	}

	public void resume_choice_onClick(final View view){
		String name = fileNames_ArrayList.get(thePosition);
		String path;
		switch (view.getId()) {
		case R.id.resume_resume_btn:

			Intent i = new Intent(thiscontext,
					BalanceActivity.class);
			Bundle b = new Bundle();
			b.putString("filename", name);
			b.putBoolean("resume", true);
			i.putExtras(b);
			startActivity(i);
			break;
		case R.id.resume_share_btn:

			 path=writePatient(name);
			if(path!=null){
				File newFile=new File(path);
				Intent iii=new Intent(android.content.Intent.ACTION_SEND);
				iii.setType("application/pdf");
				iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				Uri u=Uri.fromFile(newFile);
				iii.putExtra(Intent.EXTRA_STREAM, u);
				
				try {
					startActivity(Intent.createChooser(iii, thiscontext.getResources().getString(R.string.share)));
					Toast.makeText(thiscontext, String.format(thiscontext.getResources().getString(R.string.writeOk),newFile.getAbsolutePath()), Toast.LENGTH_LONG).show();

				} catch (Exception e) {
					Toast.makeText(thiscontext, String.format(thiscontext.getResources().getString(R.string.noPDFreaderFound),newFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			else{
				Toast.makeText(thiscontext, thiscontext.getResources().getString(R.string.errorExporting), Toast.LENGTH_SHORT).show();
			}
			

			break;
		case R.id.resume_export_btn:
			 path=writePatient(name);
			if(path!=null){
				Intent ii=new Intent(android.content.Intent.ACTION_VIEW);
				File newFile= new File(path);
				ii.setDataAndType(Uri.fromFile(newFile),"application/pdf");
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				try {
					startActivity(ii);
					Toast.makeText(thiscontext, String.format(thiscontext.getResources().getString(R.string.writeOk),newFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(thiscontext, String.format(thiscontext.getResources().getString(R.string.noPDFreaderFound),newFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			else{
				Toast.makeText(thiscontext, thiscontext.getResources().getString(R.string.errorExporting), Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.resume_delete_btn:
			new GiulioAlertDialog.Builder(thiscontext)
			.setMessage(
					thiscontext.getResources()
					.getString(R.string.remove))
					.setPositiveButton(
							thiscontext.getResources()
							.getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									if (!deleteFile(toDelete
											+ ".data"))
										Toast.makeText(
												thiscontext,
												thiscontext
												.getResources()
												.getString(
														R.string.errorDeleting),
														Toast.LENGTH_SHORT)
														.show();
									else {
										fileNames_ArrayList = getListNames(getFilesDir()
												.getParentFile());
										names_ArrayList = getListNamesNoExtensions(getFilesDir()
												.getParentFile());
										listview_Adapter = new ArrayAdapter<>(
												thiscontext,
												R.layout.filelist_item,
												names_ArrayList);
										patients.setAdapter(listview_Adapter);
										patients.setDividerHeight(BalanceActivity.DIV_HEIGTH);
										listview_Adapter
										.notifyDataSetChanged();
										if (listview_Adapter
												.getCount() == 0)
											hidePatientsToResume();
									}
								}
							})
							.setNegativeButton(
									getResources().getString(
											R.string.no), null).show();
			break;

		default:
			break;
			
		}
		ga.dismiss();
	}
	
	int thePosition;
	Typeface tf;
	View file_view;
	TextView new_patient, resume_patient, title, eraser;
	RelativeLayout main_relative_layout;
	LinearLayout container_linear_layout;
	Context thiscontext = this;
	List<String> fileNames_ArrayList, names_ArrayList;
	ArrayAdapter<String> listview_Adapter;
	ListView patients;
	float new_patient_pos, resume_patient_pos, container_pos;
	OnClickListener resume_clickListener;
	boolean MENU_SHOWING = false;
	boolean ERASER_SHOWING = false;
	boolean EXITING=false;
	long TIMEPRESSED;
	Handler uithread = new Handler();
	GiulioAlertDialog disclaimer;
	String toDelete;
	ImageView logo;
}
