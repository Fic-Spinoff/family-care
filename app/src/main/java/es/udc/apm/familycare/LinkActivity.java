package es.udc.apm.familycare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LinkActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_link_unbind)
    public void onClick(View view) {
        Toast.makeText(this, "Unbind device clicked", Toast.LENGTH_SHORT).show();
    }
}
