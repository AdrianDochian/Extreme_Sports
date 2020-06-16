package com.example.ExtremeSports;

import java.util.ArrayList;

public class City
{
    public String name;
    public ArrayList<Event> events;

    public City(String name)
    {
        this.name = name;
        events = new ArrayList<>();
    }

    public void addEvent(Event newEvent)
    {
        events.add(newEvent);
    }

    @Override
    public boolean equals(Object other)
    {
        City otherCity = (City)other;

        if (name.compareToIgnoreCase(otherCity.name) == 0)
        {
            return true;
        }
        return false;
    }
}
