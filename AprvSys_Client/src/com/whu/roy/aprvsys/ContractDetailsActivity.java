package com.whu.roy.aprvsys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.whu.roy.aprvsys.Contract;

public class ContractDetailsActivity extends Activity {
	
	private Contract con;
	
	EditText t1;
	
	// 定义Handler对象  
    private Handler handler = new Handler() {  
        @Override  
        // 当有消息发送出来的时候就执行Handler的这个方法  
        public void handleMessage(Message msg) {  
        	switch (msg.what)
        	{
	        	case 0:
	        	{
	        		new AlertDialog.Builder(ContractDetailsActivity.this)
	        		.setTitle("系统")
	        		.setMessage(msg.obj.toString())
	        		.setPositiveButton("确认", null)
	        		.show();
	        		break;
	        	}	
	        	case 1:
	        	{
	        		new AlertDialog.Builder(ContractDetailsActivity.this)
	        		.setTitle("系统")
	        		.setMessage(msg.obj.toString())
	        		.setPositiveButton("确认", new DialogInterface.OnClickListener() {
	        			 
	                    @Override
	                    public void onClick(DialogInterface dialog, int i) {
	                    	
		        	        //关闭页面 并更新手机端界面
		        	        Intent aprvresult = new Intent();
		            		Bundle bd = new Bundle();
		        	        bd.putString("Comment", t1.getText().toString());
		        	        bd.putInt("LocInList", getIntent().getIntExtra("LocationInList", 0));
    	        			RadioButton rb = (RadioButton) findViewById(R.id.radioMale);
    	        			if(rb.isChecked())
    	        				bd.putInt("Result", 1);
    	        			else 
    	        				bd.putInt("Result", 2);
		        	        aprvresult.putExtras(bd);
		        	        setResult(RESULT_OK, aprvresult);
		        	        finish();
	                    }
	                })
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
		setContentView(R.layout.activity_contract_details);
		
		con = (Contract) getIntent().getSerializableExtra("Contract");
		TextView tv_title = (TextView) findViewById(R.id.tv_title1);
		TextView tv_author = (TextView) findViewById(R.id.tv1);
		TextView tv_time = (TextView) findViewById(R.id.tv_time);
		
		tv_title.setText(con.title);
		tv_author.setText(con.author);
		tv_time.setText(con.time);		

		TextView textView_num = (TextView) findViewById(R.id.textView_num);
		textView_num.setText(con.number);
		TextView textView_cat = (TextView) findViewById(R.id.textView_cat);
		textView_cat.setText(con.category);
		TextView textView_pro_num = (TextView) findViewById(R.id.textView_pro_num);
		textView_pro_num.setText(con.pronum);
		TextView textView_pro_name = (TextView) findViewById(R.id.textView_pro_name);
		textView_pro_name.setText(con.proname);
		TextView textView_good_num = (TextView) findViewById(R.id.textView_good_num);
		textView_good_num.setText(con.goodnum);
		TextView textView_good_name = (TextView) findViewById(R.id.textView_good_name);
		textView_good_name.setText(con.goodname);
		TextView textView_sum = (TextView) findViewById(R.id.textView_sum);
		textView_sum.setText(Integer.toString(con.sum));
		TextView textView_sum_cat = (TextView) findViewById(R.id.textView_sum_cat);
		textView_sum_cat.setText(con.sum_cat);
		TextView textView_rate = (TextView) findViewById(R.id.textView_rate);
		textView_rate.setText(con.rate);
		TextView textView_payment = (TextView) findViewById(R.id.textView_payment);
		textView_payment.setText(con.payment);
		TextView textView_sign_name = (TextView) findViewById(R.id.textView_sign_name);
		textView_sign_name.setText(con.signname);	
		TextView textView_deal_name = (TextView) findViewById(R.id.textView_deal_name);
		textView_deal_name.setText(con.dealname);	
		TextView textView_period = (TextView) findViewById(R.id.textView_period);
		textView_period.setText(con.period);		
		TextView textView_site = (TextView) findViewById(R.id.textView_site);
		textView_site.setText(con.site);		
		TextView textView_content = (TextView) findViewById(R.id.textView_content);
		textView_content.setText(con.content);	
		TextView textView_punish = (TextView) findViewById(R.id.textView_punish);
		textView_punish.setText(con.punish);	
		TextView textView_ps = (TextView) findViewById(R.id.textView_ps);
		textView_ps.setText(con.ps);	
		
		Button button_subaprv = (Button) findViewById(R.id.button_subaprv);
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
		TextView textView_IsAprv = (TextView) findViewById(R.id.textView_IsAprv);
		
		int status = con.status, from = getIntent().getIntExtra("From", 0);
		if (from == 0)
		if (status == 0)
		{
			textView_IsAprv.setVisibility(View.GONE);
			button_subaprv.setText("提交结果");
			button_subaprv.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
				{       	
	                new Thread() {  
	                    @Override  
	                    public void run() {
	                        try {  
	                        	
	    	                    Socket socket = new Socket(com.whu.roy.aprvsys.MainActivity.HOST, com.whu.roy.aprvsys.MainActivity.PORT);  
	    	        			
	    	        			//获得输出流
	    	        			PrintStream ClientToServer = new PrintStream(socket.getOutputStream(), true, "utf-8"); 
	    	        			//output.print("Hello World from Android" + "[FINAL]");
	    	        			
	    	        			//更新合同信息
	    	        			JSONObject obj_login = new JSONObject();
	    	        			obj_login.put("Op", "UpdateAprvResult");
	    	        			JSONObject obj = new JSONObject();
	    	        			t1 = (EditText) findViewById(R.id.edit_text);
	    	        			obj.put("Comment", t1.getText().toString());
	    	        			obj.put("ContractId", con.Id);
	    	        			RadioButton rb = (RadioButton) findViewById(R.id.radioMale);
	    	        			if(rb.isChecked())
	    	        				obj.put("Result", "1");
	    	        			else 
	    	        				obj.put("Result", "2");
	    	        			obj_login.put("RequestData", obj);
	    	        			ClientToServer.print(obj_login.toString() + "[FINAL]");
	    	        			
	    	                    // 获得输入流
	    	        			InputStream in = socket.getInputStream();
	    	        	        BufferedReader ServerToClient = new BufferedReader(  
	    	        	                new InputStreamReader(in));  
	    	        	        String line = ServerToClient.readLine(); 		        	        
	    	        	        JSONObject ResponseJSON = new JSONObject(line);
	    	        	        String result = ResponseJSON.getString("Op");	        	        
	    	        	        
	    	        	        if (result.toString().equals("Wrong"))
	    	        	        {
	    		        	        Message msg = Message.obtain();
	    		        	        msg.what = 0;
	    		        	        msg.obj = "与服务器通信失败，请重试";
	    		        	        handler.sendMessage(msg);	        	        	
	    	        	        }
	    	        	        else
	    	        	        {
	    		        	        Message msg = Message.obtain();
	    		        	        msg.what = 1;
	    		        	        msg.obj = "结果提交成功";
	    		        	        handler.sendMessage(msg);	 
	    		        	        	    		        	        
	    	        	        }   
	    	        	        
	    	        	        ServerToClient.close();	                    
	    	                    ClientToServer.close(); 
	    	                    socket.close();
	    	        	        
	                        } catch (UnknownHostException e) {  
	                            // TODO Auto-generated catch block  
	                            e.printStackTrace();  
	                        } catch (IOException e) {  
	                            // TODO Auto-generated catch block  
	                            e.printStackTrace();  
	                        } catch (JSONException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}  
	                        //handler.sendEmptyMessage(0); 
	                    }  
	                }.start();  
				}
	        });  
			
		}
		else
		{
			textView_IsAprv.setVisibility(View.GONE);
			button_subaprv.setText("已审批");
			button_subaprv.setEnabled(false);
			if (status == 1)
				rg.check(R.id.radioMale);
			else rg.check(R.id.radioFemale);
			rg.setEnabled(false);	
			findViewById(R.id.radioMale).setEnabled(false);
			findViewById(R.id.radioFemale).setEnabled(false);
			//findViewById(R.id.textView17).setVisibility(View.GONE);
			EditText et = (EditText) findViewById(R.id.edit_text);
			et.setHint("");
			et.setText(con.comment);
			et.setEnabled(false);
		}
		else if (status == 0)
		{ 
			View l = findViewById(R.id.LinearLayout_aprv);
			l.setVisibility(View.GONE);			
		}
		else
		{
			textView_IsAprv.setVisibility(View.GONE);
			button_subaprv.setVisibility(View.GONE);
			if (status == 1)
				rg.check(R.id.radioMale);
			else rg.check(R.id.radioFemale);
			rg.setEnabled(false);	
			findViewById(R.id.radioMale).setEnabled(false);
			findViewById(R.id.radioFemale).setEnabled(false);
			//findViewById(R.id.textView17).setVisibility(View.GONE);
			EditText et = (EditText) findViewById(R.id.edit_text);
			et.setHint("");
			et.setText(con.comment);
			et.setEnabled(false);
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contract_details, menu);
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
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
	        setResult(RESULT_CANCELED, null);
	        finish();
  
        }  

        return false;  
    }
	
	
}







