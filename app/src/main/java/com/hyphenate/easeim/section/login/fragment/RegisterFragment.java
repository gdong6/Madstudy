package com.hyphenate.easeim.section.login.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.EMError;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.easeim.section.base.WebViewActivity;
import com.hyphenate.easeim.section.login.viewmodels.LoginViewModel;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class RegisterFragment extends BaseInitFragment implements TextWatcher, View.OnClickListener, EaseTitleBar.OnBackPressListener {

    private EaseTitleBar mToolbarRegister;
    private EditText mEtLoginName;
    private EditText mEtLoginPwd;
    private EditText mEtLoginPwdConfirm;
    private Button mBtnLogin;
    private CheckBox cbSelect;
    private TextView tvAgreement;
    private String mUserName;
    private String mPwd;
    private String mPwdConfirm;
    private LoginViewModel mViewModel;
    private Drawable clear;
    private Drawable eyeOpen;
    private Drawable eyeClose;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarRegister = findViewById(R.id.toolbar_register);
        mEtLoginName = findViewById(R.id.et_login_name);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mEtLoginPwdConfirm = findViewById(R.id.et_login_pwd_confirm);
        mBtnLogin = findViewById(R.id.btn_login);
        cbSelect = findViewById(R.id.cb_select);
        tvAgreement = findViewById(R.id.tv_agreement);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEtLoginName.addTextChangedListener(this);
        mEtLoginPwd.addTextChangedListener(this);
        mEtLoginPwdConfirm.addTextChangedListener(this);
        mBtnLogin.setOnClickListener(this);
        mToolbarRegister.setOnBackPressListener(this);

        EaseEditTextUtils.clearEditTextListener(mEtLoginName);
        mBtnLogin.setEnabled(true);
    }

    @Override
    protected void initData() {
        super.initData();
        tvAgreement.setText(getSpannable());
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.setVisibility(View.GONE);
        mViewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);
        mViewModel.getRegisterObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>(true) {
                @Override
                public void onSuccess(String data) {
                    ToastUtils.showToast(getResources().getString(R.string.em_register_success));
                    onBackPress();
                }

                @Override
                public void onError(int code, String message) {
                    if(code == EMError.USER_ALREADY_EXIST) {
                        ToastUtils.showToast(R.string.demo_error_user_already_exist);
                    }else {
                        ToastUtils.showToast(message);
                    }
                }

                @Override
                public void onLoading(String data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });

        });
        eyeClose = getResources().getDrawable(R.drawable.d_pwd_hide);
        eyeOpen = getResources().getDrawable(R.drawable.d_pwd_show);
        clear = getResources().getDrawable(R.drawable.d_clear);
        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwd, eyeClose, eyeOpen, null, null, null);
        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwdConfirm, eyeClose, eyeOpen, null, null, null);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkEditContent();
    }

    private void checkEditContent() {
        mUserName = mEtLoginName.getText().toString().trim();
        mPwd = mEtLoginPwd.getText().toString().trim();
        mPwdConfirm = mEtLoginPwdConfirm.getText().toString().trim();
        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginPwd, eyeClose);
        EaseEditTextUtils.showRightDrawable(mEtLoginPwdConfirm, eyeClose);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                registerToHx();
                break;
        }
    }

    private void registerToHx() {
        if(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdConfirm)) {
            if(!TextUtils.equals(mPwd, mPwdConfirm)) {
                showToast(R.string.em_password_confirm_error);
                return;
            }
            mViewModel.register(mUserName, mPwd);
        }
    }


    @Override
    public void onBackPress(View view) {
        onBackPress();
    }


    private SpannableString getSpannable() {
        SpannableString spanStr = new SpannableString(getString(R.string.em_login_agreement));
        //设置下划线
        //spanStr.setSpan(new UnderlineSpan(), 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebViewActivity.actionStart(mContext, getString(R.string.em_register_service_agreement_url));
            }
        }, 2, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //spanStr.setSpan(new UnderlineSpan(), 10, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new MyClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebViewActivity.actionStart(mContext, getString(R.string.em_register_privacy_agreement_url));
            }
        }, 11, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    private abstract class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.bgColor = Color.TRANSPARENT;
            ds.setColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }
}
