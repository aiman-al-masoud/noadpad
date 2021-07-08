package com.luxlunaris.noadpadlight.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luxlunaris.noadpadlight.R;
import com.luxlunaris.noadpadlight.model.interfaces.Page;

public class PageFragment extends Fragment {

    public Page page;
    private PageFragment THIS;

    public PageFragment() {
        // Required empty public constructor
        THIS = this;
    }

    public static PageFragment newInstance(Page page) {
        PageFragment fragment = new PageFragment();
        fragment.page = page;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_page, container, false);


        Button pageButton = view.findViewById(R.id.pageButton);

        pageButton.setText(page.getText());


        pageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(THIS.getContext(), ReaderActivity.class);
                intent.putExtra("PAGE", page);
                startActivity(intent);
            }
        });



        return view;
    }
}