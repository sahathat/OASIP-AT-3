package com.company;

import com.company.model.DateTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ZonedDateTime startTime1 = ZonedDateTime.of(2000,1,1,1,0,0,0, ZoneId.systemDefault());
        ZonedDateTime endTime1 = ZonedDateTime.of(2000,1,1,1,20,0,0, ZoneId.systemDefault());

        ZonedDateTime startTime2 = ZonedDateTime.of(2000,1,1,1,50,0,0, ZoneId.systemDefault());
        ZonedDateTime endTime2 = ZonedDateTime.of(2000,1,1,2,10,0,0, ZoneId.systemDefault());

        ZonedDateTime startTimeInput = ZonedDateTime.of(2000,1,1,1,20,0,0, ZoneId.systemDefault());
        ZonedDateTime endTimeInput = ZonedDateTime.of(2000,1,1,1,40,0,0, ZoneId.systemDefault());

        if(startTime1.isBefore(endTimeInput) && startTimeInput.isBefore(endTime1)){
            System.out.println(startTime1.isBefore(endTimeInput));
            System.out.println(startTimeInput.isBefore(endTime1));
            System.out.println("is overlap");
        } else {
            System.out.println(startTime1.isBefore(endTimeInput));
            System.out.println(startTimeInput.isBefore(endTime1));
            System.out.println("is no overlap");
        }

        Set<DateTime> setdate = new LinkedHashSet<>();

        setdate.add(new DateTime(startTime1,endTime1));
        setdate.add(new DateTime(startTime2,endTime2));
//        setdate.add(new DateTime(startTimeInput,endTimeInput));

        System.out.println(setdate);

        setdate.forEach(
                e -> {
                    System.out.println("Start date: " + e.getStartdate());
                    System.out.println("End date: " + e.getEnddate());
                }
        );

        AtomicBoolean isNonOverlap = new AtomicBoolean(true);

        if(isNonOverlap.get()){
            setdate.forEach(
                e -> {
                        if(e.getStartdate().isBefore(endTimeInput) && startTimeInput.isBefore(e.getEnddate())){
                            System.out.println(e.getStartdate().isBefore(endTimeInput));
                            System.out.println(startTimeInput.isBefore(e.getStartdate()));
                            isNonOverlap.set(false);
                        } else {
                            System.out.println(e.getStartdate().isBefore(endTimeInput));
                            System.out.println(startTimeInput.isBefore(e.getStartdate()));
                        }
                    }
            );
        };

        System.out.println(isNonOverlap.get());

//        System.out.println(isNonOverlap);
    }
}
