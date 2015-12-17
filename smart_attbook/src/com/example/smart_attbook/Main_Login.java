package com.example.smart_attbook;





import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Main_Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main__login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        
        
//        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        String text = pref.getString("editText", "");
//        int a = text.length();
//		    
//        if(a != 0 ) {
//        	Intent j = new Intent(Main_Login.this, Professor_lecture_select.class);
//        	startActivity(j);
//        	finish();
//        }
        
        final Button button = (Button)findViewById(R.id.button3);
        
        button.setOnClickListener(new View.OnClickListener() {
  		
			@Override
			public void onClick(View v) {
		    	SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		    	SharedPreferences.Editor editor = pref.edit();
		    	
		    	EditText et1 = (EditText)findViewById(R.id.editText1);
		    	editor.putString("editText", et1.getText().toString());
		    	editor.commit();

		    	Intent s = new Intent(Main_Login.this,Professor_lecture_select.class);
				startActivity(s);
	        	finish();
			}
		});
    }
}
