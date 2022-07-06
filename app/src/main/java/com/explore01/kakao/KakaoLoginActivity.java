package com.explore01.kakao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.util.regex.Pattern;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class KakaoLoginActivity extends AppCompatActivity {

    private static final String TAG = "KakaoLoginActivity";
    private View LoginBtnK, loginBtn, registerBtn, logoutBtn;
    private TextView nickName;
    private ImageView profileImage;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private BackKeyClickHandler backKeyClickHandler;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_login);

        LoginBtnK = findViewById(R.id.kakaologinbtn);
        /*logoutBtn = findViewById(R.id.kakaologoutbtn);
        nickName = findViewById(R.id.kakaonickname);
        profileImage = findViewById(R.id.kakaoprofile);*/
        editTextEmail = findViewById(R.id.et_eamil);
        editTextPassword = findViewById(R.id.et_password);
        /*loginBtn = findViewById(R.id.loginBtn);*/
        registerBtn = findViewById(R.id.registerBtn);

        /*
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override


            public void onClick (View view){
                Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(loginIntent);
            }
        });
       */

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KakaoLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //String uid = user.getUid().toString();
                    Intent intent = new Intent(KakaoLoginActivity.this, MainActivity.class);
                    //intent.putExtra("CurrentUid",uid);
                    startActivity(intent);
                    finish();

                } else {

                }
            }
        };


        backKeyClickHandler = new BackKeyClickHandler(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {  //토근이 전달되면 성공, null이면 실패
                if (oAuthToken != null) { //성공이라면
                    Intent intent = new Intent(KakaoLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if (throwable != null) { //오류라면

                }
                updateKakaoLoginUi();
                return null;
            }
        };

        LoginBtnK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(KakaoLoginActivity.this)) {  //기기에 카카오톡이 설치되어있는지 확인하는 API
                    UserApiClient.getInstance().loginWithKakaoTalk(KakaoLoginActivity.this, callback);
                } else {  //카카오톡 설치가 안되어 있을 경우 웹으로 이동
                    UserApiClient.getInstance().loginWithKakaoAccount(KakaoLoginActivity.this, callback);
                }
            }
        });

      /*  logoutBtn.setOnClickListener(new View.OnClickListener() {
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
        updateKakaoLoginUi();*/
    }

    private void updateKakaoLoginUi() {  //사용자가 카카오톡 계정으로 로그인이 되어있는지 확인. 되어있지 않다면 로그인 버튼을 화면에 보임. 되어있다면 사용자 프로필 사진과 닉네임과 로그아웃 버튼이 보임.
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {  //로그인 상태 여부 확인

                    Log.i(TAG, "invoke: id=" + user.getId());
                    Log.i(TAG, "invoke: email=" + user.getKakaoAccount().getEmail());
                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());
                    Log.i(TAG, "invoke: gender=" + user.getKakaoAccount().getGender());
                    Log.i(TAG, "invoke: age=" + user.getKakaoAccount().getAgeRange());

                  /*nickName.setText(user.getKakaoAccount().getProfile().getNickname());  //닉네임 얻어오기
                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(profileImage); //프로필 이미지 얻어오기*/
                } else {
                 /*nickName.setText(null);
                   profileImage.setImageBitmap(null);*/
                }
                return null;
            }
        });
    }

    public void signIn(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        if (isValidEmail() && isValidPasswd()) {
            loginUser(email, password);
        }
    }

    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(this, "비밀번호 범위는 4~16자 입니다.", Toast.LENGTH_SHORT).show();
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    private void loginUser(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(KakaoLoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();


                        } else {
                            // 로그인 실패
                            Toast.makeText(KakaoLoginActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼


        backKeyClickHandler.onBackPressed();
        //super.onBackPressed();

    }
}