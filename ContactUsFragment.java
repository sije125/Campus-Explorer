package com.ibrahim.campusexplorer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibrahim.campusexplorer.MainActivity;
import com.ibrahim.campusexplorer.R;


public class ContactUsFragment extends Fragment
{
    Button sendBtn;
    private EditText emailEd, nameEd, messageEd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        emailEd = view.findViewById(R.id.emailInputEditText);
        nameEd = view.findViewById(R.id.nameInputEditText);
        messageEd = view.findViewById(R.id.messageEd);
        sendBtn = view.findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String email = String.valueOf(emailEd.getText());
                final String name = String.valueOf(nameEd.getText());
                final String message = String.valueOf(messageEd.getText());
                sendEmail(name, message, email);
            }
        });
        return view;
    }

    private void sendEmail(String name, String message, String email)
    {
        Log.i("Send email", "");
        String[] TO = {"bro.fcb@gmail.com"};
        String[] CC = {""};
        String subject = "Feedback from "+ name;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message+ "\nE-mail: "+email);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            getActivity().finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
