package com.example.ExtremeSports;

import sun.reflect.generics.tree.Tree;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.StringTokenizer;

public class Event implements Comparable<Event>
{
    public static int eventsCounter;
    public int id;
    public String sportType;
    public LocalDate startDate;
    public LocalDate endDate;
    public int cost;
    public Country country;
    public Region region;
    public City city;

    public Event(Country country, Region region, City city, String sportType, String startDateString,
                 String endDateString, int cost)
    {
        id = eventsCounter++;
        this.city = city;
        this.region = region;
        this.country = country;
        this.sportType = sportType;

        StringTokenizer st = new StringTokenizer(startDateString, "-");
        startDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                Integer.parseInt(st.nextToken()));

        st = new StringTokenizer(endDateString, "-");
        endDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                Integer.parseInt(st.nextToken()));

        this.cost = cost;
    }

    @Override
    public String toString()
    {
        return country.name + " " + region.name + " " + city.name + " " + sportType + " [" + startDate.toString() +
                " <-> " + endDate.toString() + "] " + cost;
    }

    @Override
    public boolean equals(Object other)
    {
        Event otherEvent = (Event) other;
        if (country.equals(otherEvent.country) && region.equals(otherEvent.region) && city.equals(otherEvent.city) &&
                sportType.equals(otherEvent.sportType) && startDate.equals(otherEvent.startDate)
                && endDate.equals(otherEvent.endDate) && cost == otherEvent.cost)
        {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Event other)
    {
        // cost order
        if (cost != other.cost)
        {
            return cost - other.cost;
        }

        // lexicographical order
        return sportType.compareTo(other.sportType);
    }
}