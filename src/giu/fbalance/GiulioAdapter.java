package giu.fbalance;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GiulioAdapter extends ArrayAdapter<String> {

	public GiulioAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		mInflater=new LayoutInflater(getContext()) {
			@Override
			public LayoutInflater cloneInContext(Context newContext) {
				return null;
			}
		};
	}

	LayoutInflater mInflater;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(getCount()!=0) {
			if(convertView==null)
				convertView=mInflater.inflate(getContext().getResources().getLayout(R.layout.list_item), null);
			if(frozenList.get(position))
				((TextView)convertView).setTextColor(convertView.getContext().getResources().getColor(R.color.gray));
			else
				((TextView)convertView).setTextColor(convertView.getContext().getResources().getColor(R.color.giulioBlue));
		}
		return super.getView(position, convertView, parent);
	}

	ArrayList<Boolean> frozenList=new ArrayList<Boolean>();

	
	public void add(String object,boolean isFrozen) {
		frozenList.add(isFrozen);
		super.add(object);
	}

	@Override
	public void clear() {
		frozenList.clear();
		super.clear();
	}

}
