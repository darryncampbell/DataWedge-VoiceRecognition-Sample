package com.darryncampbell.dwvoicerecognition;

public interface Subject {
    public void register(Observer observer);
    public void unregister(Observer observer);
    public void notifyObservers(String recognisedText);
}
