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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.whu.roy.aprvsys.Contract;

public class NewContractActivity extends Activity {

	EditText et_title, et_signname, et_date, et_sum, et_content;
	String author;
	JSONObject con;	
	
	// 定义Handler对象  
    private Handler handler = new Handler() {  
        @Override  
        // 当有消息发送出来的时候就执行Handler的这个方法  
        public void handleMessage(Message msg) {  
        	switch (msg.what)
        	{
        	case 0:
        	{
        		new AlertDialog.Builder(NewContractActivity.this)
        		.setTitle("错误")
        		.setMessage(msg.obj.toString())
        		.setPositiveButton("确认", null)
        		.show();
        		break;
        	}	
        	case 1:
        	{
        		new AlertDialog.Builder(NewContractActivity.this)
        		.setTitle("系统")
        		.setMessage(msg.obj.toString())
        		.setPositiveButton("确认", new DialogInterface.OnClickListener() {
       			 
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
        				try {
							Contract upldcon = new Contract(0, con.getString("title"), con.getString("author"), 
									con.getString("content"), con.getString("date"), Integer.parseInt(con.getString("sum")), con.getString("connum"), 
									con.getString("cat"), con.getString("pronum"), con.getString("proname"), con.getString("goodnum"), 
									con.getString("goodname"), con.getString("sumcat"), con.getString("rate"), 
									con.getString("payment"), con.getString("signname"), con.getString("author"), 
									con.getString("period"), con.getString("site"), con.getString("punish"), con.getString("ps"));
						                    	
		        	        //关闭页面 并更新手机端界面
		        	        Intent upldresult = new Intent();
		            		Bundle bd = new Bundle();
		            		bd.putSerializable("Contract", upldcon);
		            		upldresult.putExtras(bd);
		        	        setResult(RESULT_OK, upldresult);
		        	        finish();
	        	        
        				} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
		setContentView(R.layout.activity_new_contract);
		
		Initialization();
	}
	
	public void Initialization()
	{
		author = getIntent().getStringExtra("Username");
		Button SubConBtn = (Button) findViewById(R.id.button_subcon);
		SubConBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et_title = (EditText) findViewById(R.id.EditText_title);
				et_signname = (EditText) findViewById(R.id.EditText_signname);
				et_date = (EditText) findViewById(R.id.EditText_date);
				et_sum = (EditText) findViewById(R.id.EditText_sum);
				et_content = (EditText) findViewById(R.id.EditText_content);
				if ((TextUtils.isEmpty(et_title.getText())) || TextUtils.isEmpty(et_signname.getText()) 
						|| TextUtils.isEmpty(et_date.getText()) ||
						TextUtils.isEmpty(et_sum.getText()) || TextUtils.isEmpty(et_content.getText()))
				{
        	        Message msg = Message.obtain();
        	        msg.what = 0;
        	        msg.obj = "必填项不可为空";
        	        handler.sendMessage(msg);	
					
				}
				else
				{      
					new Thread() {
						 @Override  
		                    public void run() {
					try {
                    Socket socket = new Socket(com.whu.roy.aprvsys.MainActivity.HOST, 
                    		com.whu.roy.aprvsys.MainActivity.PORT);  
        			
        			//获得输出流
        			PrintStream ClientToServer = new PrintStream(socket.getOutputStream(), true, "utf-8"); 
        			//output.print("Hello World from Android" + "[FINAL]");
        			
        			//上传登陆信息
        			JSONObject obj_upload = new JSONObject();
        			obj_upload.put("Op", "Upload");
        			con = new JSONObject();
        			con.put("author", author);
        			con.put("title", et_title.getText());
        			con.put("signname", et_signname.getText());
        			con.put("date", et_date.getText());
        			con.put("sum", et_sum.getText());
        			con.put("content", et_content.getText());
        			con.put("connum", ((EditText)findViewById(R.id.EditText_connum)).getText());
        			con.put("cat", ((EditText)findViewById(R.id.EditText_cat)).getText());
        			con.put("pronum", ((EditText)findViewById(R.id.EditText_pronum)).getText());
        			con.put("proname", ((EditText)findViewById(R.id.EditText_proname)).getText());
        			con.put("goodnum", ((EditText)findViewById(R.id.EditText_goodnum)).getText());
        			con.put("goodname", ((EditText)findViewById(R.id.EditText_goodname)).getText());
        			con.put("sumcat", ((EditText)findViewById(R.id.EditText_sumcat)).getText());
        			con.put("rate", ((EditText)findViewById(R.id.EditText_rate)).getText());
        			con.put("payment", ((EditText)findViewById(R.id.EditText_payment)).getText());
        			con.put("period", ((EditText)findViewById(R.id.EditText_period)).getText());
        			con.put("site", ((EditText)findViewById(R.id.EditText_site)).getText());
        			con.put("punish", ((EditText)findViewById(R.id.EditText_punish)).getText());
        			con.put("ps", ((EditText)findViewById(R.id.EditText_ps)).getText());      			
        			
        			obj_upload.put("RequestData", con);
        			//obj_upload.put("SignName", et_signname.getText());
        			ClientToServer.print(obj_upload.toString() + "[FINAL]");
        			
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
	        	        msg.obj = "待签订人用户名不存在";
	        	        handler.sendMessage(msg);	        	        	
        	        }
        	        else
        	        {
	        	        Message msg = Message.obtain();
	        	        msg.what = 1;
	        	        msg.obj = "上传成功";
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
				
					}
					}.start();
			}
				}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_contract, menu);
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
