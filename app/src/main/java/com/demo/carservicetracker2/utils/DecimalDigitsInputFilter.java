package com.demo.carservicetracker2.utils;
import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Pattern;
public class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        StringBuilder sb = new StringBuilder();
        sb.append("[0-9]{0,");
        sb.append(digitsBeforeZero - 1);
        sb.append("}+((\\.[0-9]{0,");
        sb.append(digitsAfterZero - 1);
        sb.append("})?)||(\\.)?");
        this.mPattern = Pattern.compile(sb.toString());
    }
    @Override 
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (this.mPattern.matcher(dest).matches()) {
            return null;
        }
        return "";
    }
}
