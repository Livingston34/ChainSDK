package xybank.com.chainsdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xybank.chain.android.sdk.ChainAPI;
import com.xybank.chain.android.sdk.ChainAPI.EncryptMode;
import com.xybank.chain.android.sdk.http.HttpCallbackStringListener;


public class MainActivity extends Activity implements View.OnClickListener {

    private EditText real_name_et;
    private EditText phone_mobile_et;
    private EditText id_card_et;
    private EditText bank_card_et;
    private TextView record_no_tv;
    private Button _btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        real_name_et = findViewById(R.id.real_name_et);
        phone_mobile_et = findViewById(R.id.phone_mobile_et);
        id_card_et = findViewById(R.id.id_card_et);
        bank_card_et = findViewById(R.id.bank_card_et);
        record_no_tv = findViewById(R.id.record_no_tv);
        _btn = findViewById(R.id._btn);

        _btn.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        String realName = real_name_et.getText().toString();
        if (realName.length() == 0) {
            Toast.makeText(this, "请输入用户姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        String mobile = phone_mobile_et.getText().toString();
        if (mobile.length() == 0) {
            Toast.makeText(this, "请输入用户手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
//        view.setClickable(false);
        String idCard = id_card_et.getText().toString();
        String bankCard = bank_card_et.getText().toString();
        ChainAPI chainAPI = ChainAPI.getInstance(this);
        String encryptContent = chainAPI.encryptContent(realName, mobile, idCard, null, EncryptMode.SM3);
        String auzCertificateNo = chainAPI.getAuzCertificateNo(EncryptMode.SM3);
        chainAPI.authorize(encryptContent, auzCertificateNo, ChainAPI.EncryptMode.SM3,
                new HttpCallbackStringListener() {
                    @Override
                    public void onFinish(final String response) {
                        view.setClickable(true);
                        Log.d("TAG", "记录的流水号为======" + response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "本次流水号为: " + response, Toast.LENGTH_SHORT).show();
                                record_no_tv.setText("本次流水号为: " + response);
                            }
                        });
                    }

                    @Override
                    public void onError(final Exception e) {
                        view.setClickable(true);
                        Log.e("TAG", "onError======" + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                record_no_tv.setText("本次错误信息为: " + e.getMessage());
                            }
                        });
                    }
                });
    }
}
