package com.example.easyspace.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskTextWatcher implements TextWatcher {

    private final EditText editText;
    private final String mask;
    private boolean isUpdating;
    private String old = "";

    public MaskTextWatcher(EditText editText, String mask) {
        this.editText = editText;
        this.mask = mask;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = MaskTextWatcher.unmask(s.toString());

        if (isUpdating) {
            old = str;
            isUpdating = false;
            return;
        }

        String mascara = "";
        int i = 0;
        for (char m : mask.toCharArray()) {
            if (m != '#') {
                if (i < str.length()) {
                    mascara += m;
                }
            } else {
                if (i < str.length()) {
                    mascara += str.charAt(i);
                    i++;
                } else {
                    break;
                }
            }
        }

        isUpdating = true;
        editText.setText(mascara);
        editText.setSelection(mascara.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "").replaceAll(" ", "");
    }
}