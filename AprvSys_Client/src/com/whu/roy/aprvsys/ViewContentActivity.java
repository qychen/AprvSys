package com.whu.roy.aprvsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whu.roy.aprvsys.Contract;
import com.whu.roy.aprvsys.MainActivity.ReceiverListener;

public class ViewContentActivity extends ActionBarActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	List<View> mListViews;
	ActionBar actionBar;
	ListView lv_aprvd, lv_myupld;
	List<Contract> contracts = new ArrayList<Contract>(), contracts_upld = new ArrayList<Contract>();
	ContractListAdapter ConAdapter, ConAdapter_upld;
	
	private View View_person_info, View_to_be_aprvd, View_my_upload;
	
	private static final int PENDING = 0, APPROVED = 1, DENIED = 2;	

	private static final int ForAprvResult = 0, ForUpldResult = 1; 
	
	// 定义Handler对象  
    private Handler handler = new Handler() {  
        @Override  
        // 当有消息发送出来的时候就执行Handler的这个方法  
        public void handleMessage(Message msg) {  
        	switch (msg.what)
        	{
	        	case ForAprvResult:
	        	{
	        		//View_to_be_aprvd.invalidate();
	        		ConAdapter.notifyDataSetChanged();
	        		break;
	        	}
	        	case ForUpldResult:        	
	        	{
	        		new AlertDialog.Builder(ViewContentActivity.this)
	        		.setTitle("系统")
	        		.setMessage(msg.obj.toString())
	        		.setPositiveButton("确认", null)
	        		.show();
	        		break;
	        	}        	
        	}
            super.handleMessage(msg);           
        }
    };  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_content);

		// Set up the action bar.
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		InitViewPager();
		InitToBeAprvdSection();		
		InitMyUploadSection();
		InitPersonalInfoSection();
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode)
		{
		case (ForAprvResult) :
		{
			if(resultCode == RESULT_OK)
			{
				int loc = intent.getIntExtra("LocInList", 0);
				Contract con = contracts.get(loc);
				con.status = intent.getIntExtra("Result", 0);
				con.comment = intent.getStringExtra("Comment");
				contracts.set(loc, con);
				

        		ConAdapter.notifyDataSetChanged();
				/*
		        Message msg = Message.obtain();
		        msg.what = ForAprvResult;
		        msg.obj = null;
		        handler.sendMessage(msg);
		        		*/
			} 
			break;
		}
		case (ForUpldResult) :
		{
			if(resultCode == RESULT_OK)
			{
				Contract con = (Contract) intent.getSerializableExtra("Contract");
				
				contracts_upld.add(con);
				

        		ConAdapter_upld.notifyDataSetChanged();
				/*
		        Message msg = Message.obtain();
		        msg.what = ForAprvResult;
		        msg.obj = null;
		        handler.sendMessage(msg);
		        		*/
			} 

			break;
		}
		}
		
	}
	
	
	private void InitViewPager()
	{
		//Create the List that contains different views for each tab
		mListViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		View_person_info = mInflater.inflate(R.layout.view_person_info, null);
		View_to_be_aprvd = mInflater.inflate(R.layout.view_to_be_aprvd, null);
		View_my_upload = mInflater.inflate(R.layout.view_my_upload, null);
		mListViews.add(View_person_info);
		mListViews.add(View_to_be_aprvd);
		mListViews.add(View_my_upload);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(mListViews);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		//use view_to_be_aprvd page to be the default 
		mViewPager.setCurrentItem(1);
		
	}
	
	public void InitToBeAprvdSection()
	{
		//initialization of to_be_aprvd section		
		lv_aprvd = (ListView) View_to_be_aprvd.findViewById(R.id.ToBeAprvdListView);

		/*
		Contract con1 = new Contract(PENDING, "TITLE1", "aaa", "Today is Sunday", "03/24");
		Contract con2 = new Contract(APPROVED, "车辆前轮采购合同", "王小明", "这是一个有关于汽车前轮采购的合同，包括报价等信息", "03/22", 
				40000, "NA8891B", "定额合同", "DF0221", "东风汽车有限公司", 
				"DDWD12", "螺纹钢", "CNY", "5.1%", "货到付款", "李佳", "王小明", "一年", 
				"武汉市武昌区武汉大学文理学部计算机学院", "如在两年内不能完成则退货！如在两年内不能完成则退货！如在两年内不能完成则退货！", "无");
		Contract con3 = new Contract(DENIED, "TITLE3", "bbb", "Hello my name is Roy", "03/14");
		*/
		//contracts = new ArrayList<Contract>();
		//contracts.add(con1);
		//contracts.add(con2);
		//contracts.add(con3);		
		
		JSONArray AprvConList;
		
		try {
			AprvConList = new JSONArray(getIntent().getStringExtra("AprvConList"));
			//Contract[] AprvCons = new Contract[AprvConList.length()];
			int i =0;
			for(i=0;i<AprvConList.length();i++)
			{
				JSONObject aprvobj = AprvConList.getJSONObject(i);
				/*
				new Contract(APPROVED, "车辆前轮采购合同", "王小明", "这是一个有关于汽车前轮采购的合同，包括报价等信息", "03/22", 
						40000, "NA8891B", "定额合同", "DF0221", "东风汽车有限公司", 
						"DDWD12", "螺纹钢", "CNY", "5.1%", "货到付款", "李佳", "王小明", "一年", 
						"武汉市武昌区武汉大学文理学部计算机学院", "如在两年内不能完成则退货！如在两年内不能完成则退货！如在两年内不能完成则退货！", "无");
						Contract(int s, String title, String au, String con, String time, 
						int sum, String number, String category, String pronum, 
						String proname, String goodnum, String goodname, 
						String sum_cat, String rate, String payment, String signname, 
						String dealname, String period, String site, String punish, String ps)
						*/
				Contract aprvcon = new Contract(aprvobj.getInt("status"), aprvobj.getString("title"), aprvobj.getString("author"), 
						aprvobj.getString("content"), aprvobj.getString("time"), aprvobj.getInt("sum"), aprvobj.getString("number"), 
						aprvobj.getString("category"), aprvobj.getString("pronum"), aprvobj.getString("proname"), aprvobj.getString("goodnum"), 
						aprvobj.getString("goodname"), aprvobj.getString("sum_cat"), aprvobj.getString("rate"), 
						aprvobj.getString("payment"), aprvobj.getString("signname"), aprvobj.getString("dealname"), 
						aprvobj.getString("period"), aprvobj.getString("site"), aprvobj.getString("punish"), aprvobj.getString("ps"));
				aprvcon.Id = aprvobj.getInt("id");
				aprvcon.comment = aprvobj.getString("comment");
				contracts.add(aprvcon);
			}
		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ConAdapter = new ContractListAdapter(this,R.layout.contract_list_item, contracts);
		lv_aprvd.setAdapter(ConAdapter);
		
		lv_aprvd.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				Contract con = contracts.get(position);				
        		Intent intent = new Intent(ViewContentActivity.this, ContractDetailsActivity.class);
        		Bundle bd = new Bundle();
        		bd.putSerializable("Contract", con);
        		bd.putInt("From", 0);
        		bd.putInt("LocationInList", position);
        		intent.putExtras(bd);
        		startActivityForResult(intent, ForAprvResult);
        		
			}
		});
		
	}
	
	public void InitMyUploadSection()
	{
		LinearLayout ll = (LinearLayout) View_my_upload.findViewById(R.id.LinearLayout_newcon);
		ll.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		Bundle bd = new Bundle();
        		bd.putString("Username", getIntent().getStringExtra("Username"));
        		Intent intent = new Intent(ViewContentActivity.this, NewContractActivity.class);
        		intent.putExtras(bd);
        		startActivityForResult(intent, ForUpldResult);
        	}
        });  
		
		lv_myupld = (ListView) View_my_upload.findViewById(R.id.MyUpldListView);
		//contracts_upld = new ArrayList<Contract>();
		
		JSONArray UpldConList;
		
		try {
			UpldConList = new JSONArray(getIntent().getStringExtra("UpldConList"));
			int i =0;
			for(i=0;i<UpldConList.length();i++)
			{
				JSONObject upldobj = UpldConList.getJSONObject(i);
				Contract upldcon = new Contract(upldobj.getInt("status"), upldobj.getString("title"), upldobj.getString("author"), 
						upldobj.getString("content"), upldobj.getString("time"), upldobj.getInt("sum"), upldobj.getString("number"), 
						upldobj.getString("category"), upldobj.getString("pronum"), upldobj.getString("proname"), upldobj.getString("goodnum"), 
						upldobj.getString("goodname"), upldobj.getString("sum_cat"), upldobj.getString("rate"), 
						upldobj.getString("payment"), upldobj.getString("signname"), upldobj.getString("dealname"), 
						upldobj.getString("period"), upldobj.getString("site"), upldobj.getString("punish"), upldobj.getString("ps"));
				upldcon.Id = upldobj.getInt("id");
				upldcon.comment = upldobj.getString("comment");
				
				contracts_upld.add(upldcon);
			}
		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ConAdapter_upld = new ContractListAdapter(this,R.layout.contract_list_item, contracts_upld);
		lv_myupld.setAdapter(ConAdapter_upld);
		
		lv_myupld.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				Contract con = contracts_upld.get(position);				
        		Intent intent = new Intent(ViewContentActivity.this, ContractDetailsActivity.class);
        		Bundle bd = new Bundle();
        		bd.putSerializable("Contract", con);
        		bd.putString("Username", getIntent().getStringExtra("Username"));
        		bd.putInt("From", 1);
        		intent.putExtras(bd);
        		startActivity(intent);
        		
			}
		});
		
	}
	
	public void InitPersonalInfoSection()
	{
		TextView tv_info_username = (TextView) View_person_info.findViewById(R.id.tv_info_username);
		tv_info_username.setText(getIntent().getStringExtra("Username"));
		TextView tv_info_email = (TextView) View_person_info.findViewById(R.id.tv_info_email);
		tv_info_email.setText(getIntent().getStringExtra("Email"));
		TextView tv_info_company = (TextView) View_person_info.findViewById(R.id.tv_info_company);
		tv_info_company.setText(getIntent().getStringExtra("Company"));
		TextView tv_info_realname = (TextView) View_person_info.findViewById(R.id.tv_info_realname);
		tv_info_realname.setText(getIntent().getStringExtra("RealName"));
		TextView tv_info_description = (TextView) View_person_info.findViewById(R.id.tv_info_description);
		tv_info_description.setText(getIntent().getStringExtra("Description"));
	}
	
	public class ContractListAdapter extends ArrayAdapter<Contract>
	{
		private int layoutID;

		public ContractListAdapter(Context context, int resource, List<Contract> list) {
			super(context, resource, list);
			this.layoutID = resource;
		}		

	    @Override  
	    public View getView(int position, View convertView, ViewGroup parent){  
	    	Contract contract = getItem(position);  
	        RelativeLayout contractsItem = new RelativeLayout(getContext());  
	        String inflater = Context.LAYOUT_INFLATER_SERVICE;   
	        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);   
	        vi.inflate(layoutID, contractsItem, true);  
	        ImageView ivStatus = (ImageView)contractsItem.findViewById(R.id.ImageView_status);   
	        TextView tvAuthor = (TextView)contractsItem.findViewById(R.id.tv_author);  
	        TextView tvTitle = (TextView)contractsItem.findViewById(R.id.tv_title);
	        TextView tvContent = (TextView)contractsItem.findViewById(R.id.textView_content);
	        TextView tvDate = (TextView)contractsItem.findViewById(R.id.textView_date);  
	        tvAuthor.setText(contract.author);  
	        tvTitle.setText(contract.title);  
	        tvContent.setText(contract.content);  
	        tvDate.setText(contract.time);  
	        //tvStatus.setText(contract.status);
	        switch (contract.status)
	        {
		        case PENDING: ivStatus.setImageResource(R.drawable.read); break;
		        case APPROVED: ivStatus.setImageResource(R.drawable.yes); break;
		        case DENIED: ivStatus.setImageResource(R.drawable.no); break;
	        }
	        return contractsItem;  
	    } 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_content, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

    public class SectionsPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public SectionsPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }


}
