package com.trx.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trx.mobilesafe.R;

/**
 * 自定义组合控件
 *
 * 1. 写一个类继承RelativeLayout(ViewGroup)
 * 2. 写布局文件
 * 3. 将布局添加到RelativeLayout中(initView方法)
 * 4. 增加api
 * 5. 自定义属性(1. values/attrs.xml, 2. 声明命名空间 , 3.在自定义view中配置属性, 4. 在自定义view中加载属性值 )
 * Created by trx08 on 2016/3/14.
 */
public class SettingItemView extends RelativeLayout {

    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/com.trx.mobilesafe";
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbCheck;

    private String mTitle;
    private String mDescOn;
    private String mDescOff;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

        initAttrs(attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

        initAttrs(attrs);
    }

//    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initView();
//
//        initAttrs(attrs);
//    }

    void initView(){
        View v = View.inflate(getContext(), R.layout.setting_item_view, null);

        tvTitle = (TextView)v.findViewById(R.id.tv_title);
        tvDesc = (TextView)v.findViewById(R.id.tv_desc);
        cbCheck = (CheckBox)v.findViewById(R.id.cb_check);

        this.addView(v);
    }

    void initAttrs(AttributeSet attrs){
        mTitle = attrs.getAttributeValue(NAME_SPACE, "mtitle");
        mDescOn = attrs.getAttributeValue(NAME_SPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAME_SPACE, "desc_off");

        tvTitle.setText(mTitle);

    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    public void setDesc(String desc){
        tvDesc.setText(desc);
    }

    public void setChecked(boolean checked){
        cbCheck.setChecked(checked);

        if (checked){
            setDesc(mDescOn);
        }else {
            setDesc(mDescOff);
        }
    }

    public Boolean isChecked(){
        return cbCheck.isChecked();
    }
}
