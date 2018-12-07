package com.jay.timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@RestController
@RequestMapping("")
public class TimestampController {

    @GetMapping("/{timezone}")
    public ResponseEntity<String> toUTC(@PathVariable(value = "timezone") String timezone) {
        return getStringResponseEntity(timezone);
    }

    private ResponseEntity<String> getStringResponseEntity(@PathVariable("timezone") String timezone) {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        String formattedDate = simpleDateFormat.format(date);
        return new ResponseEntity<>(formattedDate, HttpStatus.OK);
    }

    @GetMapping("/{timezone1}/{timezone2}")
    public ResponseEntity<String> toUTC(@PathVariable(value = "timezone1") String timezone1, @PathVariable(value = "timezone2") String timezone2) {
        return getStringResponseEntity(timezone1 + "/" + timezone2);
    }
}
