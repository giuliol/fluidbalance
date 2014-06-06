package giu.fbalance;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BalanceItemAdapter extends ArrayAdapter<BalanceEntry> {
	 
    private final Context context;
    public static final int IN=1;
    public static final int OUT=0;
    private int type;
    LayoutInflater inflater;

    
    private final ArrayList<BalanceEntry> itemsArrayList;

    public BalanceItemAdapter(Context context, ArrayList<BalanceEntry> itemsArrayList,int itype, ViewGroup parent) {

        super(context, R.layout.two_item_list_item, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        
        type=itype;
     // 1. Create inflater 
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        

    }

    
    static class ViewHolder {
        public TextView time, value;
      }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View rowView=convertView;
    	if(rowView==null){
            // 2. Get rowView from inflater
            if(type==IN)
            	 rowView = inflater.inflate(R.layout.balance_in_list_item, parent, false);
            else
            	 rowView = inflater.inflate(R.layout.balance_out_list_item, parent, false);
            
            ViewHolder viewH=new ViewHolder();
            viewH.time=(TextView)rowView.findViewById(R.id.balance_list_item_time);
            viewH.value=(TextView)rowView.findViewById(R.id.balance_list_item_value);
            rowView.setTag(viewH);

    	}

//        // 3. Get the two text view from the rowView
//        TextView timeView = (TextView) rowView.findViewById(R.id.balance_list_item_time);
//        TextView valueView = (TextView) rowView.findViewById(R.id.balance_list_item_value);

    	ViewHolder vhold=(ViewHolder)rowView.getTag();
    	
        // 4. Set the text for textView 
        vhold.time.setText(itemsArrayList.get(position).getTimeString()+"\n"+String.format("%6.1f",itemsArrayList.get(position).getAmount())+"ml");
        vhold.value.setText(itemsArrayList.get(position).getEntryName());
        if(itemsArrayList.get(position).isFrozen()){
        	vhold.time.setTextColor(Color.GRAY);
        	vhold.value.setTextColor(Color.GRAY);
        }
        // 5. retrn rowView
        return rowView;
    }
}