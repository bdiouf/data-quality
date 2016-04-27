package org.talend.dataquality.datamasking.semantic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.talend.dataquality.datamasking.functions.Function;

public class DateFunctionAdapter extends Function<String> {

    private static final long serialVersionUID = -2845447810365033162L;

    private Function<Date> function;

    private SimpleDateFormat sdf;

    public DateFunctionAdapter(Function<Date> functionToAdapt, String dateFormat) {
        function = functionToAdapt;
        sdf = new SimpleDateFormat(dateFormat);
    }

    @Override
    protected String doGenerateMaskedField(String input) {
        try {
            final Date inputDate = sdf.parse(input);
            final Date result = function.generateMaskedRow(inputDate);
            return sdf.format(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input;
    }

}
