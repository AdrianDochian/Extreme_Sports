package com.example.ExtremeSports;

import java.util.ArrayList;

public class Country
{
    public String name;
    public ArrayList<Region> regions;

    public Country(String name)
    {
        this.name = name;
        regions = new ArrayList<>();
    }

    public boolean regionAlreadyRecorded(String newRegion)
    {
        for (Region item : regions)
        {
            if (item.name.equals(newRegion))
            {
                return true;
            }
        }
        return false;
    }

    public void addRegion(String newRegion)
    {
        if (! regionAlreadyRecorded(newRegion))
        {
            regions.add(new Region(newRegion));
        }
    }

    public Region getRegion(String region)
    {
        addRegion(region);

        for (Region item : regions)
        {
            if (item.name.equals(region))
            {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object other)
    {
        Country otherCountry = (Country)other;

        if (name.compareToIgnoreCase(otherCountry.name) == 0)
        {
            return true;
        }
        return false;
    }
}
