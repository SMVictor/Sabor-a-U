package com.soda.proyecto.saborau.Utilities;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.soda.proyecto.saborau.dataAccess.PedidoDataFirebase;

public class MiFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "NOTICIAS";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String stringToken = FirebaseInstanceId.getInstance().getToken();

        Firebase.setAndroidContext(getApplicationContext());
        Firebase ref2 = new Firebase(PedidoDataFirebase.FIREBASE_URL);

        Token token = new Token();
        token.setToken(stringToken);

        ref2.child("Tokens").push().setValue(token, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {}
        });
    }
}
