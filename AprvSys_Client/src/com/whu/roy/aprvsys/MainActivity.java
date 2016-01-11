package com.whu.roy.aprvsys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.whu.roy.aprvsys.Contract;

public class MainActivity extends Activity {

    //public static final String HOST = "192.168.1.103";  
    public static final String HOST = "10.3.0.1";    
    public static final int PORT = 8080;     
    private Button btn_login;  
    private Button btn_newuser;  
    private Button btn_frtpswd;  
    private TextView txt;  
    private String line; 
    private static final int ERROR = 0, RESPONSE = 1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initControl(); 
	}
	
	private void initControl() {  
        btn_login = (Button) findViewById(R.id.button_login);  
        //txt = (TextView) findViewById(R.id.textView_username);
        btn_login.setOnClickListener(new ReceiverListener());  
        
        btn_newuser = (Button) findViewById(R.id.button_newuser);
        btn_newuser.setOnClickListener(new ReceiverListener()
        {
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
        		startActivity(intent);
        	}
        });  
        
        btn_frtpswd = (Button) findViewById(R.id.button_fgtpswd);
        btn_frtpswd.setOnClickListener(new ReceiverListener()
        {
        	public void onClick(View v)
        	{
        		Intent intent = new Intent(MainActivity.this, FgtPswdActivity.class);
        		startActivity(intent);
        	}
        });  
    }  
	
	// ����Handler����  
    private Handler handler = new Handler() {  
        @Override  
        // ������Ϣ���ͳ�����ʱ���ִ��Handler���������  
        public void handleMessage(Message msg) {  
        	switch (msg.what)
        	{
	        	case ERROR:
	        	{
	        		new AlertDialog.Builder(MainActivity.this)
	        		.setTitle("����")
	        		.setMessage(msg.obj.toString())
	        		.setPositiveButton("ȷ��", null)
	        		.show();
	        		break;
	        	}
	        	case RESPONSE:        	
	        	{
	        		new AlertDialog.Builder(MainActivity.this)
	        		.setTitle("ϵͳ")
	        		.setMessage(msg.obj.toString())
	        		.setPositiveButton("ȷ��", null)
	        		.show();
	        		break;
	        	}        	
        	}
            super.handleMessage(msg);           
        }
    };  
	
    class ReceiverListener implements OnClickListener {
  
        @Override  
        public void onClick(View v) {  
            // TODO Auto-generated method stub  
        	//����û����������ʽ
			EditText t1 = (EditText) findViewById(R.id.editText_username);
			EditText t2 = (EditText) findViewById(R.id.editText_pswd);
			
			if ( (t1.getText().toString().equals("")) || (t2.getText().toString().equals("")) )
			{
    	        Message msg = Message.obtain();
    	        msg.what = ERROR;
    	        msg.obj = "�û��������벻��Ϊ��";
    	        handler.sendMessage(msg);	
			}   
			
			else 
			{       	
            new Thread() {  
                @Override  
                public void run() {
                    try {  
                    	
	                    Socket socket = new Socket(HOST, PORT);  
	        			
	        			//��������
	        			PrintStream ClientToServer = new PrintStream(socket.getOutputStream(), true, "utf-8"); 
	        			//output.print("Hello World from Android" + "[FINAL]");
	        			
	        			//�ϴ���½��Ϣ
	        			JSONObject obj_login = new JSONObject();
	        			obj_login.put("Op", "Login");
	        			JSONObject obj = new JSONObject();
	        			EditText t1 = (EditText) findViewById(R.id.editText_username);
	        			EditText t2 = (EditText) findViewById(R.id.editText_pswd);
	        			obj.put("Username", t1.getText().toString());
	        			obj.put("Password", t2.getText().toString());
	        			obj_login.put("RequestData", obj);
	        			ClientToServer.print(obj_login.toString() + "[FINAL]");
	        			
	                    // ���������
	        			InputStream in = socket.getInputStream();
	        	        BufferedReader ServerToClient = new BufferedReader(  
	        	                new InputStreamReader(in));  
	        	        line = ServerToClient.readLine(); 		        	        
	        	        JSONObject ResponseJSON = new JSONObject(line);
	        	        String result = ResponseJSON.getString("Op");	        	        
	        	        
	        	        if (result.toString().equals("Wrong"))
	        	        {
		        	        Message msg = Message.obtain();
		        	        msg.what = ERROR;
		        	        msg.obj = "�û������������";
		        	        handler.sendMessage(msg);	        	        	
	        	        }
	        	        else
	        	        {
		        	        JSONArray AprvConList = ResponseJSON.getJSONObject("ResultData").getJSONArray("AprvConList");
		        	        //JSONObject obj1 = AprvConList.getJSONObject(0);
		        	        //String title = obj1.getString("title");
		        	       // String author = AprvConList.getJSONObject(1).getString("author");
		        	        String AprvConListStr = AprvConList.toString();		        	        

		        	        JSONArray UpldConList = ResponseJSON.getJSONObject("ResultData").getJSONArray("UpldConList");		        	        
		        	        String UpldConListStr = UpldConList.toString();		 
		        	        
		            		Bundle bd = new Bundle();
		            		bd.putString("AprvConList", AprvConListStr);
		            		bd.putString("UpldConList", UpldConListStr);
		            		bd.putString("Username", t1.getText().toString());
		            		bd.putString("Email", ResponseJSON.getJSONObject("ResultData").getString("Email"));
		            		bd.putString("RealName", ResponseJSON.getJSONObject("ResultData").getString("RealName"));
		            		bd.putString("Company", ResponseJSON.getJSONObject("ResultData").getString("Company"));
		            		bd.putString("Description", ResponseJSON.getJSONObject("ResultData").getString("Description"));
		            		
	                		Intent intent = new Intent(MainActivity.this, ViewContentActivity.class);
		            		intent.putExtras(bd);
	                		startActivity(intent);	        	        	
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
        } 
    }
  
}
