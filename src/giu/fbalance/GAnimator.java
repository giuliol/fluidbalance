package giu.fbalance;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

public class GAnimator {
	public static final int LONG_ANIM=175;
	public static final int SHORT_ANIM=175;

	public static void toggleButtonTitle(TextView view){
		if(view.isClickable()){
			view.setBackgroundColor(Color.argb(0,0,0,0));
			view.setTextColor(view.getContext().getResources().getColor(R.color.giulioBlue));
		}
		else{
			view.setBackgroundDrawable(view.getContext().getResources().getDrawable(R.drawable.pressable));
			view.setTextColor(view.getContext().getResources().getColor(R.color.lightgrey));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static float slowTranslateViewToX(View view, float pos){
		float start, end;
		start=view.getX();
		end=pos;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "X", start, end );
		anim.setDuration(LONG_ANIM);
		anim.start();
		return start;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static float slowTranslateViewToY(View view, float pos){
		float start, end;
		start=view.getY();
		end=pos;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "Y", start, end );
		anim.setDuration(LONG_ANIM);
		anim.start();
		return start;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static float slowFade(View view, boolean in_or_out){
		float start=view.getAlpha();
		float end;
		if(in_or_out)
			end=1f;
		else
			end=0f;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "alpha", start, end );
		anim.setDuration(LONG_ANIM);
		anim.start();
		return start;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static float fastTranslateViewToY(View view, float pos){
		float start, end;
		start=view.getY();
		end=pos;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "Y", start, end );
		anim.setDuration(SHORT_ANIM);
		anim.start();
		return start;
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static float fastFade(View view, boolean in_or_out){
		float start=view.getAlpha();
		float end;
		if(in_or_out)
			end=1f;
		else
			end=0f;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "alpha", start, end );
		anim.setDuration(SHORT_ANIM);
		anim.start();
		return start;
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void slowFadeTop(View view, boolean in_or_out){

		float end;
		if(!in_or_out){
			end=-view.getHeight();
		}
		else{
			end=0;
		}
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "Y", end);
		anim.setDuration(LONG_ANIM);
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void slowFadeTop(View view, boolean in_or_out,boolean clear){

		float end;

		if(!in_or_out)
			end=-view.getHeight();
		else
			end=0;

		GiulioAnimatorListener ani=new GiulioAnimatorListener();
		ani.setView(view);

		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "Y", end);
		anim.setDuration(LONG_ANIM);
		anim.addListener(ani);
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void slowTranslateViewToYwithEndAction(View view,float pos, final Runnable act){
		float start, end;
		start=view.getY();
		end=pos;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "Y", start, end );
		anim.setDuration(SHORT_ANIM);
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				act.run();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void slowFadeWithEndAction(View view, boolean in_or_out, final Runnable act){
		float start=view.getAlpha();
		float end;
		if(in_or_out)
			end=1f;
		else
			end=0f;
		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "alpha", start, end );
		anim.setDuration(LONG_ANIM);
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				act.run();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void pulse(View view,int count){

		ObjectAnimator anim=ObjectAnimator.ofFloat(view, "Alpha", 1f,0f,1f);		
		anim.setDuration(LONG_ANIM);
		anim.setRepeatCount(count);
		anim.start();
	}
	
	public static void pulseOldApi(View view,int count){

//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "Alpha", 1f,0f,1f);		
//		anim.setDuration(LONG_ANIM);
//		anim.setRepeatCount(count);
//		anim.start();
	}

	public static float slowFadeOldApi(View view, boolean in_or_out){
//		float start=ViewHelper.getAlpha(view);
//		float end;
//		if(in_or_out)
//			end=1f;
//		else
//			end=0f;
//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "alpha", start, end );
//		anim.setDuration(LONG_ANIM);
//		anim.start();
//		
////		
//		
//		return start;
		return 0;
	}
	
	public static float slowTranslateViewToYOldApi(View view, float pos){
//		float start, end;
//		start=ViewHelper.getY(view);
//		end=pos;
//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "Y", start, end );
//		anim.setDuration(LONG_ANIM);
//		anim.start();
//		return start;
		return 0;
	}
	
	public static float slowTranslateViewToXOldApi(View view, float pos){
//		float start, end;
//		start=ViewHelper.getX(view);
//		end=pos;
//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "X", start, end );
//		anim.setDuration(LONG_ANIM);
//		anim.start();
//		return start;
		return 0;
	}
	
	public static void slowFadeWithEndActionOldApi(View view, boolean in_or_out, final Runnable act){
//		float start, end;
//		if(in_or_out){
//			start=0f;
//			end=1f;
//		}
//		else{
//			start=1f;
//			end=0f;
//		}
//		
//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "alpha", start, end );
//		anim.setDuration(LONG_ANIM);
//		new Handler().postDelayed(act,anim.getDuration());
//		anim.start();
	}
	
	public static void slowFadeTopOldApi(final View view, boolean in_or_out,boolean clear){
//
//		float end;
//
//		if(!in_or_out)
//			end=-view.getHeight();
//		else
//			end=0;
//
//
//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "Y", end);
//		anim.setDuration(LONG_ANIM);
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				view.setVisibility(View.GONE);
//				((RelativeLayout)view).removeAllViews();
//			}
//		},anim.getDuration());
//
//		anim.start();
	}

	public static void slowFadeTopOldApi( View view, boolean in_or_out){
//
//		float end;
//		if(!in_or_out)
//			end=-view.getHeight();
//		else
//			end=0;
//		com.nineoldandroids.animation.ObjectAnimator anim=com.nineoldandroids.animation.ObjectAnimator.ofFloat(view, "Y", end);
//		anim.setDuration(LONG_ANIM);
//		anim.start();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void rotate(View view){
		ObjectAnimator anim=ObjectAnimator.ofFloat(view,"rotationY", 0f,90f);
		anim.setDuration(LONG_ANIM);
		anim.start();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void rotateBack(View view){
		ObjectAnimator anim=ObjectAnimator.ofFloat(view,"rotationY", 90f,0f);
		anim.setDuration(LONG_ANIM);
		anim.start();
	}


}
