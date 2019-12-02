package com.logingps.logingps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.logingps.logingps.models.Coordinates;

public class ReportActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coordinatesRef = db.collection("coordinates");

    TextView result_query_coordinates;

    private static final Integer QUERY_LIMIT = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        setTitle("Location Report");

        result_query_coordinates = findViewById(R.id.result_query_coordinates);

        getLocations();
    }

    private void getLocations() {
        coordinatesRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(QUERY_LIMIT)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String coordinatesList = "";

                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Coordinates coordinates = queryDocumentSnapshot.toObject(Coordinates.class);

                            String latitude = coordinates.getLatitude();
                            String longitude = coordinates.getLongitude();
                            String timestamp = coordinates.getTimestamp();

                            coordinatesList += "\nLatitude: " + latitude
                                    + "\nLongitude: " + longitude
                                    + "\nTimestamp: " + timestamp
                                    + "\n";

                            if (coordinatesList.isEmpty()) {
                                result_query_coordinates.setText("No coordinates found!");
                            } else {
                                result_query_coordinates.setText(coordinatesList);
                            }
                        }
                    }
                });
    }

    public void goBackMenu(View view) {
        Intent intent = new Intent(view.getContext(), HomeActivity.class);
        startActivity(intent);
    }
}
