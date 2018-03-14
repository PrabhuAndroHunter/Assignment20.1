package com.assignment;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.toString();
    private EditText mNameEdT, mPhoneNumberEdT;
    private Button mSaveBtn, mViewBtn;
    private String name, phoneNumber;
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init layout
        setContentView(R.layout.activity_main);
        // init views
        mNameEdT = (EditText) findViewById(R.id.edit_text_name);
        mPhoneNumberEdT = (EditText) findViewById(R.id.edit_text_phone_number);
        mSaveBtn = (Button) findViewById(R.id.button_save);
        mViewBtn = (Button) findViewById(R.id.button_view);

        // set Onclick listener on save button
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mNameEdT.getText().toString();
                phoneNumber = mPhoneNumberEdT.getText().toString();
                if (!name.equalsIgnoreCase("")) {
                    if (!phoneNumber.equalsIgnoreCase("")) {
                        saveContact();
                    } else {
                        mPhoneNumberEdT.setError("Please Enter Ph Number");
                    }
                } else {
                    mNameEdT.setError("PLease Enter name");
                }
            }
        });

        // set Onclick listener on view button
        mViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent read = new Intent();
                read.setAction(android.content.Intent.ACTION_VIEW);
                read.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivity(read);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        check for contact permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
    }

    // this method will save contact information
    private void saveContact() {
        ArrayList <ContentProviderOperation> contentProviderOperations = new ArrayList <ContentProviderOperation>();
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //add contact name
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        //add contact number
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            ContentProviderResult[] results = getApplicationContext().getContentResolver().
                    applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
            Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
            // After save, clear fields
            mNameEdT.setText("");
            mPhoneNumberEdT.setText("");
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException: " + e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(TAG, "OperationApplicationException: " + e.getMessage());
        }
    }

    // This method will call when user grant permission or deny permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Contact permission is mandatory!!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
