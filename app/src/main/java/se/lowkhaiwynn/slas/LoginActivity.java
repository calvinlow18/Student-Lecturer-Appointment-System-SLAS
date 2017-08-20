package se.lowkhaiwynn.slas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView id_tv;
    private EditText pass_et;
    private String user;
    private CheckBox saveLoginCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark));
        }
        user = getIntent().getExtras().getString("user");
        // Set up the login form.
        TextInputLayout idview = (TextInputLayout) findViewById(R.id.idview);
        id_tv = (AutoCompleteTextView) findViewById(R.id.id);
        pass_et = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.signin_button);
        saveLoginCheckBox = (CheckBox)findViewById(R.id.rem_me_checkbox);

        if(user.equals("lecturer")) {
            idview.setHint("Lecturer ID");
        } else {
            idview.setHint("Student ID");
        }
        loginButton.setEnabled(false);
        loginButton.setBackgroundColor(getResources().getColor(R.color.tomato));
        id_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(id_tv.getText().length()!=0 && pass_et.getText().length() != 0) {
                    loginButton.setEnabled(true);
                    loginButton.setBackgroundColor(getResources().getColor(R.color.blue));
                } else {
                    loginButton.setEnabled(false);
                    loginButton.setBackgroundColor(getResources().getColor(R.color.tomato));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pass_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(id_tv.getText().length()!=0 && pass_et.getText().length() != 0) {
                    loginButton.setEnabled(true);
                    loginButton.setBackgroundColor(getResources().getColor(R.color.blue));
                } else {
                    loginButton.setEnabled(false);
                    loginButton.setBackgroundColor(getResources().getColor(R.color.tomato));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        // Reset errors.
        id_tv.setError(null);
        pass_et.setError(null);

        // Store values at the time of the login attempt.
        String id = id_tv.getText().toString();
        String password = pass_et.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            pass_et.setError(getString(R.string.error_field_required));
            focusView = pass_et;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(id)) {
            id_tv.setError(getString(R.string.error_field_required));
            focusView = id_tv;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            if(!DatabaseTask.quitIfNoConnection(this)) {
                CheckLogin checkLogin = new CheckLogin(this);
                checkLogin.execute(id, password);
            }
        }
    }

    class CheckLogin extends AsyncTask<String, Void, String> {
        ProgressDialog progress;
        private Activity activity;

        public CheckLogin(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(activity);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(false);
            progress.setMessage("Logging In... ...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(DatabaseTask.isNetworkAvailable(getApplicationContext())) {
                String login_url = null;
                if (user.equals("lecturer")) {
                    login_url = "http://smartkidsedu.tk/Android/lectlogin.php";
                } else {
                    login_url = "http://smartkidsedu.tk/Android/studlogin.php";
                }
                String login_name = params[0];
                String login_pass = params[1];
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("login_name", "UTF-8")+"="+URLEncoder.encode(login_name, "UTF-8")+"&"+
                            URLEncoder.encode("login_pass", "UTF-8")+"="+URLEncoder.encode(login_pass, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader
                            = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String response = "";
                    String line = "";
                    while((line = bufferedReader.readLine()) != null)
                    {
                        response+=line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return response;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String result) {
            if(progress.isShowing()) {
                progress.dismiss();
            }
            if(DatabaseTask.isNetworkAvailable(activity)) {
                String message = "Login Failed. Please Check your Sunway ID and password and try again.";
                JSONObject jsonObject;
                JSONArray jsonArray;
                if (!result.equals("false")) {
                    try {
                        jsonObject = new JSONObject(result);
                        jsonArray = jsonObject.optJSONArray("server_response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject JO = jsonArray.getJSONObject(i);
                            String id;
                            String name;
                            String email;
                            String no;
                            String department;
                            if (user.equals("lecturer")) {
                                id = JO.optString("LectID");
                                name = JO.optString("LectName");
                                email = JO.optString("LectEmail");
                                department = JO.optString("LectDept");
                                String office = JO.optString("LectOffice");
                                no = JO.optString("LectNo");
                                DatabaseTask.user = new Lecturer(id, name, email, department, office, no);

                                if(saveLoginCheckBox.isChecked()) {
                                    DatabaseTask.storeLecturer(id, name, email, department, office, no, user);
                                    System.out.println("checked save login");
                                }
                            } else {
                                id = JO.optString("StuID");
                                name = JO.optString("StuName");
                                email = JO.optString("StuEmail");
                                no = JO.optString("StuNo");
                                String intake = JO.optString("StuIntake");
                                int sem = JO.optInt("StuSem");
                                department = JO.optString("StuDept");
                                DatabaseTask.user = new Student(id, name, email, no, intake, sem, department);
                                if(saveLoginCheckBox.isChecked()) {
                                    DatabaseTask.storeStudent(id, name, email, no, intake, sem, department, user);
                                }
                            }
                            message = "Welcome " + name;
                        }
                        Intent intent = new Intent(getApplicationContext(), AfterLogin.class);
                        intent.putExtra("Pass Login", true);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            } else {
                DatabaseTask.quitDialog(activity);
            }

        }

    }
}

