package cn.ucai.superwechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.OkHttpUtils;
import cn.ucai.superwechat.utils.ResultUtils;

public class FirentProfileActivity extends Activity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tex_title)
    TextView texTitle;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.tv_userinfo_nick)
    TextView tvUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView tvUserinfoName;
    @BindView(R.id.btn_send_msg)
    Button btnSendMsg;
    @BindView(R.id.btn_send_video)
    Button btnSendVideo;

    User user;
    @BindView(R.id.btn_add_contact)
    Button btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firent_profile);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        imgBack.setVisibility(View.VISIBLE);
        texTitle.setVisibility(View.VISIBLE);
        texTitle.setText("详细资料");
        user = (User) getIntent().getSerializableExtra(I.User.TABLE_NAME);
        if (user != null) {
            showUserInfo();
        } else {
            String username=getIntent().getStringExtra(I.User.USER_NAME);
            if (username==null){
                MFGT.finish(this);
            }else {
                syncUserInfo(username);
            }
        }
    }

    private void syncUserInfo(String username) {
        NetDao.getUserInfoByUsername(this, username, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s!=null){
                    Result result= ResultUtils.getResultFromJson(s,User.class);
                    if (result!=null){
                        if (result.isRetMsg()){
                            User u= (User) result.getRetData();
                            if (user!=null){
                                user=u;
                                showUserInfo();
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showUserInfo() {
        tvUserinfoNick.setText(user.getMUserNick());
        EaseUserUtils.setAppUserAvatarByPath(this, user.getMUserName(), profileImage,null);
        tvUserinfoName.setText("微信号：" + user.getMUserName());
        if (isFirent()) {
            btnSendMsg.setVisibility(View.VISIBLE);
            btnSendVideo.setVisibility(View.VISIBLE);
        } else {
            btnAddContact.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFirent() {
        User u = SuperWeChatHelper.getInstance().getAppContactList().get(user.getMUserName());
        if (u == null) {
            return false;
        } else {
            SuperWeChatHelper.getInstance().saveAppContact(user);
            return true;
        }
    }

    @OnClick(R.id.img_back)
    public void imgBack() {
        MFGT.finish(this);
    }

    @OnClick(R.id.btn_add_contact)
    public void sendAddContactMsg() {
        MFGT.gotoAddFirent(this,user.getMUserName());
    }
    @OnClick(R.id.btn_send_msg)
    public void sendMsg(){
        MFGT.gotoChat(this,user.getMUserName());
    }
    @OnClick(R.id.btn_send_video)
    public void sendVideo(){
        startActivity(new Intent(this,VoiceCallActivity.class)
        .putExtra("username",user.getMUserName())
        .putExtra("isComingCall",false));
    }
}
