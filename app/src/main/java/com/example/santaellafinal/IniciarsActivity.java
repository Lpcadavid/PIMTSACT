package com.example.santaellafinal;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class IniciarsActivity extends AppCompatActivity {
    private static final String TAG = "IniciarsActivity";
    private static final int REQ_ONE_TAP = 2;

    private FirebaseAuth auth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private Button googleButton;

    // ActivityResultLauncher para manejar el resultado de la autenticación
    private ActivityResultLauncher<Intent> signInActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciars);

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Inicializar el cliente One Tap
        oneTapClient = Identity.getSignInClient(this);

        // Construir el request de inicio de sesión con las opciones correctas
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id)) // Asegúrate de tener el id correcto aquí
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();

        // Inicializar vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        googleButton = findViewById(R.id.oneTapSignInButton);

        // Configurar listeners de botones
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
        googleButton.setOnClickListener(v -> signIn());

        // Registrar ActivityResultLauncher
        signInActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onSignInResult
        );
    }

    private void onSignInResult(ActivityResult activityResult) {
    }

    private boolean validateFields(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar campos
        if (!validateFields(email, password)) return;

        // Mostrar progreso
        loginButton.setEnabled(false);

        // Intentar login
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(IniciarsActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(IniciarsActivity.this, CrudActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = (task.getException() != null) ? task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(IniciarsActivity.this, "Error en el login: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validar campos
        if (!validateFields(email, password)) return;

        // Validar longitud de contraseña
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar progreso
        registerButton.setEnabled(false);

        // Intentar registro
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    registerButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(IniciarsActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(IniciarsActivity.this, CrudActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = (task.getException() != null) ? task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(IniciarsActivity.this, "Error en el registro: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        // Usamos el PendingIntent de la respuesta y pasamos su IntentSender
                        IntentSender intentSender = result.getPendingIntent().getIntentSender();
                        // Usamos startIntentSenderForResult para manejar el IntentSender
                        startIntentSenderForResult(intentSender, REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        signInWithGoogle();  // Método para iniciar flujo alternativo de Google
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, "One Tap Failed: " + e.getLocalizedMessage());
                    signInWithGoogle(); // Si One Tap falla, intenta el flujo alternativo
                });
    }

    private void signInWithGoogle() {
        // Configura Google Sign-In aquí
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_ONE_TAP);
    }

    // Método para manejar el resultado de la autenticación
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP && resultCode == RESULT_OK) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Error al obtener credenciales de Google: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        // Verificar si el idToken es nulo
        if (idToken == null) {
            Log.e(TAG, "ID Token is null");
            return;
        }

        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si la autenticación es exitosa, obtener el usuario de Firebase
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user); // Redirigir a la actividad correspondiente
                    } else {
                        // Si falla la autenticación, loguear el error
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null); // Si el usuario no pudo iniciar sesión, no hacer nada
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Usuario autenticado correctamente, redirigir a CrudActivity
            Intent intent = new Intent(IniciarsActivity.this, CrudActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Si el usuario no está autenticado, podrías mostrar un mensaje de error o mantener en la actividad actual
            Toast.makeText(this, "Error de autenticación. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Si el usuario ya está autenticado, redirigir a CrudActivity inmediatamente
            Log.d(TAG, "Usuario ya logueado: " + currentUser.getEmail());
            Intent intent = new Intent(IniciarsActivity.this, CrudActivity.class);
            startActivity(intent);
            finish();
 }
}
}