package com.ricardorainha.mustache.model;

public class Appointment {

    private long time;
    private long scheduledOn;
    private Barbershop barbershop;
    private boolean confirmed;

    public Appointment() { }

    public Appointment(long time, long scheduledOn, Barbershop barbershop, boolean confirmed) {
        this.time = time;
        this.scheduledOn = scheduledOn;
        this.barbershop = barbershop;
        this.confirmed = confirmed;
    }

    public long getTime() {
        return time;
    }

    public String getTimeString() {
        return String.valueOf(getTime());
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getScheduledOn() {
        return scheduledOn;
    }

    public void setScheduledOn(long scheduledOn) {
        this.scheduledOn = scheduledOn;
    }

    public Barbershop getBarbershop() {
        return barbershop;
    }

    public void setBarbershop(Barbershop barbershop) {
        this.barbershop = barbershop;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean hasAlreadyPassed() {
        return System.currentTimeMillis() > time;
    }
}
