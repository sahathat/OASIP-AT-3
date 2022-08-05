package com.company.model;

import java.time.ZonedDateTime;

public class DateTime {
    private ZonedDateTime startdate;
    private ZonedDateTime enddate;

    public ZonedDateTime getStartdate() {
        return startdate;
    }

    public DateTime(ZonedDateTime startdate, ZonedDateTime enddate) {
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public void setStartdate(ZonedDateTime startdate) {
        this.startdate = startdate;
    }

    public ZonedDateTime getEnddate() {
        return enddate;
    }

    public void setEnddate(ZonedDateTime enddate) {
        this.enddate = enddate;
    }

    @Override
    public String toString() {
        return "DateTime{" +
                "startdate=" + startdate +
                ", enddate=" + enddate +
                '}';
    }
}
