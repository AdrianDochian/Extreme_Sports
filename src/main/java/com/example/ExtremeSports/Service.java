package com.example.ExtremeSports;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Service
{
    private static Service singleton = getInstance();
    private ArrayList<Country> countries = new ArrayList<>();
    private Set<Event> events;

    private Service()
    {
    }

    public static Service getInstance()
    {
        if (singleton == null)
        {
            singleton = new Service();
        }
        return singleton;
    }

    public boolean countryAlreadyRecorded(String newCountry)
    {
        for (Country item : countries)
        {
            if (item.name.equals(newCountry))
            {
                return true;
            }
        }
        return false;
    }

    public void addCountry(String newCountry)
    {
        if (! countryAlreadyRecorded(newCountry))
        {
            countries.add(new Country(newCountry));
        }
    }

    public Country getCountry(String country)
    {
        addCountry(country);

        for (Country item : countries)
        {
            if (item.name.equals(country))
            {
                return item;
            }
        }
        return null;
    }

    public Set<Event> getAllEvents()
    {
        return events;
    }

    public void addEvent(Event newEvent)
    {
        events.add(newEvent);
    }

    public Set<Event> getFilteredEvents(String filter, String startDateString, String endDateString)
    {
        Set<Event> result = new TreeSet<>();

        // get Events by sport type
        StringTokenizer st = new StringTokenizer(filter, ",");

        ArrayList<String> filterSports = new ArrayList<>();

        while (st.hasMoreTokens())
        {
            filterSports.add(st.nextToken().trim());
        }

        // get wanted start date
        st = new StringTokenizer(startDateString, "-");
        LocalDate wantedStartDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                Integer.parseInt(st.nextToken()));

        // get wanted end date
        st = new StringTokenizer(endDateString, "-");
        LocalDate wantedEndDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                Integer.parseInt(st.nextToken()));

        for (Event event : events)
        {
            for (String sportType : filterSports)
            {
                if (event.sportType.toLowerCase().equals(sportType.toLowerCase()) &&
                wantedStartDate.isBefore(event.endDate) && ! wantedEndDate.isBefore(event.startDate))
                {
                    result.add(event);
                }
            }
        }

        return result;
    }

    public void getData()
    {
        Event.eventsCounter = 1;
        events = new TreeSet<>();

        try
        {
            BufferedReader fp = new BufferedReader(new FileReader("./src/sports.in"));
            String buffer;
            while ((buffer = fp.readLine()) != null)
            {
                // update Country
                Country country = singleton.getCountry(buffer);

                // update Region
                Region region = country.getRegion(fp.readLine());

                // update City
                City city = region.getCity(fp.readLine());

                // update Event
                Event event = new Event(country, region, city, fp.readLine(), fp.readLine(), fp.readLine(),
                        Integer.parseInt(fp.readLine()));
                singleton.addEvent(event);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void removeEvent(Event toBeDeleted)
    {
        events.remove(toBeDeleted);
    }

    public Event getEventById(int id)
    {
        for (Event event : events)
        {
            if (event.id == id)
            {
                return event;
            }
        }
        return null;
    }

    public boolean isInteger(String text)
    {
        if (text.equals(""))
        {
            return false;
        }

        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) < '0' || text.charAt(i) > '9')
            {
                return false;
            }
        }
        return true;
    }

    public void updateDatabase()
    {
        try
        {
            File oldFile = new File("./src/sports.in");
            oldFile.delete();

            FileWriter fw = new FileWriter(new File("./src/sports.in"), true);

            String result = "";
            for (Event event : events)
            {
                result += event.country.name + "\n";
                result += event.region.name + "\n";
                result += event.city.name + "\n";
                result += event.sportType + "\n";
                result += event.startDate + "\n";
                result += event.endDate + "\n";
                result += event.cost + "\n";
            }

            fw.write(result);
            fw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        String result = "";

        for (Event event : events)
        {
            result += event + "\n";
        }

        return result;
    }
}
