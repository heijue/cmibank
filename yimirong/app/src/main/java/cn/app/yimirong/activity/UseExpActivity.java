package cn.app.yimirong.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.event.EventBus;

public class UseExpActivity extends AppCompatActivity {

	private String str_info;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.use_epx);
		TextView epx_details = (TextView) findViewById(R.id.epx_details);
		Button btn_use = (Button) findViewById(R.id.btn_use);
		ImageView btn_dismiss = (ImageView) findViewById(R.id.btn_dismiss);
        Intent intent = getIntent();
        str_info = intent.getStringExtra("STR_INFO");
		if (str_info!=null) {
			epx_details.setText(Html.fromHtml(str_info));
		}

		btn_use.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EventBus.getDefault().post(new UerExp());
				finish();
			}
		});
		btn_dismiss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
