package com.example.ExtremeSports;

import java.util.ArrayList;

public class Region
{
    public String name;
    public ArrayList<City> cities;

    public Region(String name)
    {
        this.name = name;
        cities = new ArrayList<>();
    }

    public boolean addCity(City newCity)
    {
        if (! cities.contains(newCity))
        {
            cities.add(newCity);
            return true;
        }
        return false;
    }
    public boolean cityAlreadyRecorded(String newCity)
    {
        for (City item : cities)
        {
            if (item.name.equals(newCity))
            {
                return true;
            }
        }
        return false;
    }

    public void addCity(String newCity)
    {
        if (! cityAlreadyRecorded(newCity))
        {
            cities.add(new City(newCity));
        }
    }

    public City getCity(String city)
    {
        addCity(city);

        for (City item : cities)
        {
            if (item.name.equals(city))
            {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object other)
    {
        Region otherRegion = (Region)other;

        if (name.compareToIgnoreCase(otherRegion.name) == 0)
        {
            return true;
        }
        return false;
    }
}
