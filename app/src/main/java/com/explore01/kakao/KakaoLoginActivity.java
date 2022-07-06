package com.explore01.kakao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class KakaoLoginActivity extends AppCompatActivity {

    private static final String TAG = "KakaoLoginActivity";
    private View loginBtn, logoutBtn;
    private TextView nickName;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_login);

        loginBtn = findViewById(R.id.kakaologinbtn);
        logoutBtn = findViewById(R.id.kakaologoutbtn);
        nickName = findViewById(R.id.kakaonickname);
        profileImage = findViewById(R.id.kakaoprofile);

        Function2<OAuthToken, Throwable, Unit> callback =  new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {  //토근이 전달되면 성공, null이면 실패
                if (oAuthToken != null) { //성공이라면
                    Intent intent = new Intent(KakaoLoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                if (throwable != null) { //오류라면

                }
                updateKakaoLoginUi();
                return null;
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(KakaoLoginActivity.this)){  //기기에 카카오톡이 설치되어있는지 확인하는 API
                    UserApiClient.getInstance().loginWithKakaoTalk(KakaoLoginActivity.this,callback);
                } else {  //카카오톡 설치가 안되어 있을 경우 웹으로 이동
                    UserApiClient.getInstance().loginWithKakaoAccount(KakaoLoginActivity.this,callback);
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });
        updateKakaoLoginUi();
    }

    private void updateKakaoLoginUi(){  //사용자가 카카오톡 계정으로 로그인이 되어있는지 확인. 되어있지 않다면 로그인 버튼을 화면에 보임. 되어있다면 사용자 프로필 사진과 닉네임과 로그아웃 버튼이 보임.
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {  //로그인 상태 여부 확인

                    Log.i(TAG, "invoke: id=" + user.getId());
                    Log.i(TAG, "invoke: email=" + user.getKakaoAccount().getEmail());
                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());
                    Log.i(TAG, "invoke: gender=" + user.getKakaoAccount().getGender());
                    Log.i(TAG, "invoke: age=" + user.getKakaoAccount().getAgeRange());

                    nickName.setText(user.getKakaoAccount().getProfile().getNickname());  //닉네임 얻어오기
                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(profileImage); //프로필 이미지 얻어오기

                    loginBtn.setVisibility(View.GONE);
                    logoutBtn.setVisibility(View.VISIBLE);;
                } else {
                    nickName.setText(null);
                    profileImage.setImageBitmap(null);
                    loginBtn.setVisibility(View.VISIBLE);
                    logoutBtn.setVisibility(View.GONE);;
                }
                return null;
            }
        });
    }
}