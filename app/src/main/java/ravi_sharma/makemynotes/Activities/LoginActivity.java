package ravi_sharma.makemynotes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import ravi_sharma.makemynotes.R;
import ravi_sharma.makemynotes.Shared_Pref.PrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, name;
    private ProgressBar progressBar;
    private Button btnSignup;
    LinearLayout l;
    PrefManager p;

    private static String url = "http://fossfoundation.com/makeMyNotes/email.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        p = new PrefManager(this);
        if (p.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        l = (LinearLayout)findViewById(R.id.layout);
        inputEmail = (EditText) findViewById(R.id.email);
        name = (EditText)findViewById(R.id.name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String nm = name.getText().toString().trim();
                if ((!TextUtils.isEmpty(email) && !TextUtils.isEmpty(nm) && Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                    sendRequest(email, nm);
                } else if (email.isEmpty()) {
                    inputEmail.setError("Email Cannot Be Empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid Email");
                }
                else if (nm.isEmpty()) {
                    name.setError("Enter your name");
                }
            }
        });


    }

    private void sendRequest(final String email, final String name) {
        l.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("successfully"))
                        {
                            p.setLogin(true, name);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Email is Already Exists", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Please use another email", Toast.LENGTH_SHORT).show();
                        }
                        l.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        l.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Registration Error...", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> m = new HashMap<>();
                m.put("email", email);
                return m;
            }
        };

        RequestQueue r = Volley.newRequestQueue(getApplicationContext());
        r.add(sr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.layout).setVisibility(View.VISIBLE);
    }

}
