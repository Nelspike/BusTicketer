package bus.ticketer.filters;

import android.text.InputFilter;
import android.text.Spanned;

public class MonthInputFilter implements InputFilter {

    private int min, max;

    public MonthInputFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public MonthInputFilter(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);
            
            if (isInRange(min, max, input))
                return null;
            
        } catch (NumberFormatException nfe) { }
        
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}