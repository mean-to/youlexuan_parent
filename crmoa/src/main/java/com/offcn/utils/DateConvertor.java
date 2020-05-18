package com.offcn.utils;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvertor implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        SimpleDateFormat sim=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sim.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
