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

public class NewUserActivity extends Activity {

	EditText et_username, et_pswd, et_pswdcfm, et_email;
	JSONObject user;
	
	// 定义Handler对象  
    private Handler handler = new Handler() {  
        @Override  
        // 当有消息发送出来的时候就执行Handler的这个方法  
        public void handleMessage(Message msg) {  
        	switch (msg.what)
        	{
        	case 0:
        	{
        		new AlertDialog.Builder(NewUserActivity.this)
        		.setTitle("错误")
        		.setMessage(msg.obj.toString())
        		.setPositiveButton("确认", null)
        		.show();
        		break;
        	}	
        	case 1:
        	{
        		new AlertDialog.Builder(NewUserActivity.this)
        		.setTitle("系统")
        		.setMessage(msg.obj.toString())
        		.setPositiveButton("确认", new DialogInterface.OnClickListener() {       			 
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
		        	        //关闭页面
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
		setContentView(R.layout.activity_new_user);
		
		Initialization();
	}
	
	public void Initialization()
	{
		Button SubUserBtn = (Button) findViewById(R.id.button_subuser);
		SubUserBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et_username = (EditText) findViewById(R.id.EditText_username);
				et_pswd = (EditText) findViewById(R.id.EditText_password);
				et_pswdcfm = (EditText) findViewById(R.id.EditText_passwordconfirm);
				et_email = (EditText) findViewById(R.id.EditText_email);
				String pswd = et_pswdcfm.getText().toString();
				String pswdcfm = et_pswd.getText().toString();
				if ((TextUtils.isEmpty(et_username.getText())) || TextUtils.isEmpty(et_pswd.getText()) 
						|| TextUtils.isEmpty(et_pswdcfm.getText()) ||
						TextUtils.isEmpty(et_email.getText()) )
				{
        	        Message msg = Message.obtain();
        	        msg.what = 0;
        	        msg.obj = "必填项不可为空";
        	        handler.sendMessage(msg);	
					
				}
				else if (pswd.equals(pswdcfm))
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
        			
        			//上传新用户信息
        			JSONObject obj_newuser = new JSONObject();
        			obj_newuser.put("Op", "NewUser");
        			user = new JSONObject();
        			user.put("username", et_username.getText());
        			user.put("password", et_pswd.getText());
        			user.put("email", et_email.getText());
        			user.put("realname", ((EditText)findViewById(R.id.EditText_realname)).getText());
        			user.put("company", ((EditText)findViewById(R.id.EditText_company)).getText());
        			user.put("description", ((EditText)findViewById(R.id.EditText_description)).getText());
        			        			
        			obj_newuser.put("RequestData", user);
        			//obj_upload.put("SignName", et_signname.getText());
        			ClientToServer.print(obj_newuser.toString() + "[FINAL]");
        			
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
	        	        msg.obj = "用户名已被注册";
	        	        handler.sendMessage(msg);	        	        	
        	        }
        	        else
        	        {
	        	        Message msg = Message.obtain();
	        	        msg.what = 1;
	        	        msg.obj = "注册成功";
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
				else 
				{
        	        Message msg = Message.obtain();
        	        msg.what = 0;
        	        msg.obj = "两次输入的密码不一致";
        	        handler.sendMessage(msg);						
				}
				}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_user, menu);
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
	

}
