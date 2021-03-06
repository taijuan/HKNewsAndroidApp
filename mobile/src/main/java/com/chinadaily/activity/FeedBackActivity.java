package com.chinadaily.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chinadaily.R;
import com.chinadaily.base.BaseAppActivity;
import com.chinadaily.http.FastJsonCallback;
import com.chinadaily.http.HttpConstants;
import com.chinadaily.http.HttpUtilsKt;
import com.chinadaily.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import static com.chinadaily.base.BaseAppActivityKt.setTabBarTop;

public class FeedBackActivity extends BaseAppActivity implements View.OnClickListener {

    private EditText feedback_content;
    private EditText feedback_phone;

    //不超过500字
    private final static int CHAR_MAX_NUM = 500;
    private TextView user_feedback_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTabBarTop(findViewById(R.id.tabBar));
        feedback_content = findViewById(R.id.feedback_content);
        feedback_phone = findViewById(R.id.feedback_phone);
        user_feedback_num = findViewById(R.id.user_feedback_num);
        ((TextView) findViewById(R.id.baseTitle)).setText(R.string.my_feed);
        findViewById(R.id.baseBack).setOnClickListener(this);
        TextView submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        feedback_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > CHAR_MAX_NUM) {
                    ToastUtils.showShort("字数已经超过限制");
                    s = s.subSequence(0, CHAR_MAX_NUM);
                    feedback_content.setText(s);
                    feedback_content.setSelection(s.length());
                }
                user_feedback_num.setText(s.length() + "/" + CHAR_MAX_NUM);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.baseBack:
                finish();
                break;
            case R.id.submit:
                String content = feedback_content.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    submitFB(content);
                } else {
                    ToastUtils.showShort("please input your feedback");
                    return;
                }
                break;
        }
    }

    private void submitFB(String content) {
        String contact = feedback_phone.getText().toString();
        FastJsonCallback<JSONObject> fastJsonCallback = new FastJsonCallback<JSONObject>(getLifecycle()) {
            @Override
            public void onSuccess(JSONObject body) {
                String resMsg = body.getString("resMsg");
                if (resMsg.equals("success")) {
                    finish();
                } else {
                    ToastUtils.showShort("submit fail");
                }
            }

            @Override
            public void onError(@NotNull String exception) {
                ToastUtils.showShort("submit fail");
            }
        };
        fastJsonCallback.setUrl(HttpConstants.STATIC_URL + "addFeedback?content=" + content + "&email=" + contact);
        fastJsonCallback.setClazz(new TypeReference<JSONObject>() {
        });
        HttpUtilsKt.httpGet(fastJsonCallback);
    }
}
