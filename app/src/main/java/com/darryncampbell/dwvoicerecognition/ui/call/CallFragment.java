package com.darryncampbell.dwvoicerecognition.ui.call;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.darryncampbell.dwvoicerecognition.Levenshtein;
import com.darryncampbell.dwvoicerecognition.Observer;
import com.darryncampbell.dwvoicerecognition.R;
import com.darryncampbell.dwvoicerecognition.Subject;

//  Note, CallFragment, ProductInfoFragment and TasksFragment are coded very similarly.  There is
//  room for more efficient code but I have left it like this for clarity.
public class CallFragment extends Fragment implements Observer {

    String[] sentences = {
            "Call Alice",
            "Call an electronics specialist",
            "Call my manager"
    };
    private View root = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_call, container, false);
        defaultUI();
        ((Subject)getActivity()).register(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((Subject)getActivity()).unregister(this);
    }

    @Override
    public void update(String recognisedText) {
        defaultUI();
        int distance1 = Levenshtein.distance(recognisedText, sentences[0]);
        int distance2 = Levenshtein.distance(recognisedText, sentences[1]);
        int distance3 = Levenshtein.distance(recognisedText, sentences[2]);
        if (distance1 <= distance2 && distance1 <= distance3)
        {
            //  most similar to sentence[0];
            updateUI((TextView)root.findViewById(R.id.txtSay1), (ImageView)root.findViewById(R.id.imgMic1), recognisedText);
        }
        else if (distance2 <= distance1 && distance2 <= distance3)
        {
            //  most similar to sentence[1];
            updateUI((TextView)root.findViewById(R.id.txtSay2), (ImageView)root.findViewById(R.id.imgMic2), recognisedText);
        }
        else
        {
            //  most similar to sentence[2];
            updateUI((TextView)root.findViewById(R.id.txtSay3), (ImageView)root.findViewById(R.id.imgMic3), recognisedText);
        }
    }

    private void defaultUI()
    {
        TextView txt1 = root.findViewById(R.id.txtSay1);
        TextView txt2 = root.findViewById(R.id.txtSay2);
        TextView txt3 = root.findViewById(R.id.txtSay3);
        txt1.setText("Say: " + sentences[0]);
        txt2.setText("Say: " + sentences[1]);
        txt3.setText("Say: " + sentences[2]);
        ((ImageView)root.findViewById(R.id.imgMic1)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_microphone));
        ((ImageView)root.findViewById(R.id.imgMic2)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_microphone));
        ((ImageView)root.findViewById(R.id.imgMic3)).setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_microphone));
    }

    private void updateUI(TextView txt, ImageView img, String recognisedSpeech)
    {
        txt.setText("Recognised: " + recognisedSpeech);
        img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_volume_up));
    }
}