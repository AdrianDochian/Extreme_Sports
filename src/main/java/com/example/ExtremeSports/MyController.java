package com.example.ExtremeSports;

import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.StringTokenizer;

@Controller
public class MyController
{
    @RequestMapping (value = "/", method = RequestMethod.GET)
    public String initMainPage(HttpServletRequest request, Model model)
    {
        model.addAttribute("events", Service.getInstance().getAllEvents());
        return "index";
    }

    @RequestMapping (value = "/", method = RequestMethod.POST)
    public String catchMainPage(HttpServletRequest request, Model model)
    {
        String deleteId = request.getParameter("deleteId");

        if (! deleteId.equals("") && ! Service.getInstance().isInteger(deleteId))
        {
            model.addAttribute("warnMsg", "Invalid entry in the Event ID field!");
            model.addAttribute("events", Service.getInstance().getAllEvents());
            return "index";
        }
        if (deleteId.equals(""))
        {
            model.addAttribute("warnMsg", "Please insert an ID!");
        } else
        {
            Event toBeDeleted = Service.getInstance().getEventById(Integer.parseInt(deleteId));
            if (toBeDeleted == null)
            {
                model.addAttribute("warnMsg", "No event with specified ID: " + deleteId +
                        " was found!");
            } else
            {
                // Remove event from structures and wait for garbage collector
                // Remove event from city
                toBeDeleted.city.events.remove(toBeDeleted);

                // Remove event from the main Structure
                Service.getInstance().removeEvent(toBeDeleted);

                // Update changes
                Service.getInstance().updateDatabase();

                model.addAttribute("successMsg", "Event with ID " + deleteId + " was deleted!");
            }
        }

        model.addAttribute("events", Service.getInstance().getAllEvents());
        return "index";
    }

    @RequestMapping (value = "/filter", method = RequestMethod.GET)
    public String initFilterPage(HttpServletRequest request, Model model)
    {
        model.addAttribute("events", Service.getInstance().getAllEvents());
        return "filter";
    }

    @RequestMapping (value = "/filter", method = RequestMethod.POST)
    public String catchFilterPage(HttpServletRequest request, Model model)
    {
        String filter = request.getParameter("filter");
        String startDateString = request.getParameter("startDate");
        String endDateString = request.getParameter("endDate");

        if (startDateString.equals(""))
        {
            startDateString = new String("2020-01-01");
        }
        if (endDateString.equals(""))
        {
            endDateString = new String("2020-12-31");
        }

        if (! filter.equals(""))
        {
            model.addAttribute("filteredEvents", Service.getInstance().getFilteredEvents(filter,
                    startDateString, endDateString));
        }

        model.addAttribute("events", Service.getInstance().getAllEvents());

        return "filter";
    }

    @RequestMapping (value = "/create", method = RequestMethod.GET)
    public String initCreatePage(HttpServletRequest request, Model model)
    {
        model.addAttribute("events", Service.getInstance().getAllEvents());
        return "create";
    }

    @RequestMapping (value = "/create", method = RequestMethod.POST)
    public String catchCreatePage(HttpServletRequest request, Model model)
    {
        String country = request.getParameter("country").toLowerCase();
        String region = request.getParameter("region").toLowerCase();
        String city = request.getParameter("city").toLowerCase();
        String sportType = request.getParameter("sportType").toLowerCase();
        String startDateString = request.getParameter("startDate");
        String endDateString = request.getParameter("endDate");
        String cost = request.getParameter("cost");

        if (country.equals("") || region.equals("") || city.equals("") || sportType.equals("") ||
                startDateString.equals("") || endDateString.equals("") || cost.equals(""))
        {
            model.addAttribute("warnMsg", "You must fill all fields!!!");
        } else
        {
            if (! Service.getInstance().isInteger(cost))
            {
                model.addAttribute("warnMsg", "Invalid entry in the cost field!");
                model.addAttribute("events", Service.getInstance().getAllEvents());
                return "create";
            }

            StringTokenizer st = new StringTokenizer(startDateString, "-");
            LocalDate wantedStartDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()));

            st = new StringTokenizer(endDateString, "-");
            LocalDate wantedEndDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()));

            if (wantedStartDate.isAfter(wantedEndDate))
            {
                model.addAttribute("warnMsg", "Start date is after the end date!\nEvent could not" +
                        " be recorded!");
            } else
            {

                // Check if this event was already recorded
                for (Event existingEvent : Service.getInstance().getAllEvents())
                {
                    if (country.equals(existingEvent.country.name) && region.equals(existingEvent.region.name) &&
                            city.equals(existingEvent.city.name) && sportType.equals(existingEvent.sportType) &&
                            wantedStartDate.equals(existingEvent.startDate) && wantedEndDate.equals(existingEvent.endDate) &&
                            Integer.parseInt(cost) == existingEvent.cost)
                    {
                        // The event was already recorded
                        model.addAttribute("warnMsg", "The Event was already recorded!");
                        model.addAttribute("events", Service.getInstance().getAllEvents());
                        return "create";

                    }
                }

                // The event is valid and wasn't recorded yet.

                // update Country
                Country newCountry = Service.getInstance().getCountry(country);

                // update Region
                Region newRegion = newCountry.getRegion(region);

                // update City
                City newCity = newRegion.getCity(city);

                // update Event
                Event newEvent = new Event(newCountry, newRegion, newCity, sportType, startDateString, endDateString,
                        Integer.parseInt(cost));

                Service.getInstance().addEvent(newEvent);

                //Update changes
                Service.getInstance().updateDatabase();

                model.addAttribute("successMsg", "Event in " + newEvent.city.name +
                        " was successfuly recorded!");
                model.addAttribute("events", Service.getInstance().getAllEvents());
                return "create";
            }
        }

        model.addAttribute("events", Service.getInstance().getAllEvents());
        return "create";
    }

    @RequestMapping (value = "/update", method = RequestMethod.GET)
    public String initUpdatePage(HttpServletRequest request, Model model)
    {
        model.addAttribute("events", Service.getInstance().getAllEvents());
        return "update";
    }

    @RequestMapping (value = "/update", method = RequestMethod.POST)
    public String catchUpdatePage(HttpServletRequest request, Model model)
    {
        String eventId = request.getParameter("eventId");


        // No event Id inserted
        if (eventId.equals(""))
        {
            model.addAttribute("warnMsg", "Please enter an event ID");
            model.addAttribute("events", Service.getInstance().getAllEvents());
            return "update";
        }

        if (! Service.getInstance().isInteger(eventId))
        {
            model.addAttribute("warnMsg", "Please enter a number in the Event ID field");
            model.addAttribute("events", Service.getInstance().getAllEvents());
            return "update";
        }

        Event toBeUpdated = Service.getInstance().getEventById(Integer.parseInt(eventId));

        // Event couldn't be found
        if (toBeUpdated == null)
        {
            model.addAttribute("warnMsg", "No event with specified ID " + eventId +
                    " was found!");
            model.addAttribute("events", Service.getInstance().getAllEvents());
            return "update";
        }

        String startDateString = request.getParameter("startDate");
        String endDateString = request.getParameter("endDate");
        String cost = request.getParameter("cost");

        LocalDate newPossibleStart;
        LocalDate newPossibleEnd;

        if (startDateString.equals(""))
        {
            newPossibleStart = toBeUpdated.startDate;
        } else
        {
            StringTokenizer st = new StringTokenizer(startDateString, "-");
            newPossibleStart = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()));
        }

        if (endDateString.equals(""))
        {
            newPossibleEnd = toBeUpdated.endDate;
        } else
        {
            StringTokenizer st = new StringTokenizer(endDateString, "-");
            newPossibleEnd = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()));
        }

        if (newPossibleStart.isAfter(newPossibleEnd))
        {
            model.addAttribute("events", Service.getInstance().getAllEvents());
            model.addAttribute("warnMsg", "Invalid input data!");
            return "update";
        }

        if (! cost.equals("") && ! Service.getInstance().isInteger(cost))
        {
            model.addAttribute("events", Service.getInstance().getAllEvents());
            model.addAttribute("warnMsg", "Invalid entry in the New cost field!");
            return "update";
        }

        // update is possible

        if (! cost.equals(""))
        {
            toBeUpdated.cost = Integer.parseInt(cost);
        }

        toBeUpdated.startDate = newPossibleStart;
        toBeUpdated.endDate = newPossibleEnd;

        // Update changes
        Service.getInstance().updateDatabase();

        // Refresh program memory;
        Service.getInstance().getData();

        model.addAttribute("events", Service.getInstance().getAllEvents());
        model.addAttribute("successMsg", "Event with ID: " + eventId + " was updated!");
        return "update";
    }
}