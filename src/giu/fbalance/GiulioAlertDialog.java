package giu.fbalance;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RelativeLayout;

public class GiulioAlertDialog extends AlertDialog {

	 Context mContext;
	 boolean LAY_SET=false;
	 int layout_id;

	protected GiulioAlertDialog(Context context) {
		super(context);
		mContext=context;
	}
	
	public void setLayout_id(int layout_id) {
		this.layout_id = layout_id;
		LAY_SET=true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if(LAY_SET){
		  RelativeLayout ll=(RelativeLayout) LayoutInflater.from(mContext).inflate(layout_id, null);
		  setContentView(ll);
		}
	}

}
