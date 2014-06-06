package giu.fbalance;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.View;
import android.widget.RelativeLayout;

public class GiulioAnimatorListener implements AnimatorListener{

	View view;
	
	public void setView(View view) {
		this.view = view;
	}
	
	@Override
	public void onAnimationStart(Animator animation) {
		
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		view.setVisibility(View.GONE);
		((RelativeLayout)view).removeAllViews();
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		
	}
	
}