package com.ivan;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        LocalTime timeDeparture = null;
        LocalTime timeArrival = null;
        LocalTime timeX = null;
        List<Double> priceList = new ArrayList();
        Set nameCarrier = new HashSet<>();
        LocalTime timeY = LocalTime.parse("23:59");
        File file = new File("/Users/user/Desktop/test_idea/tickets.json");
        File fileWriter = new File("/Users/user/Desktop/test_idea/tickets.txt");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();


        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileWriter));

        TicketList ticketList = gson.fromJson(reader, TicketList.class);

        // записываем всех перевозчиков
        for (NameOfFields carrier : ticketList.getTickets()) {
            nameCarrier.add(carrier.getCarrier());
        }

        // проходимся с нужными фильтрами по файлу
        for (Object carrierSet : nameCarrier) {
            for (NameOfFields name : ticketList.getTickets())
                if (name.getCarrier().equals(carrierSet)
                        && name.getDestination_name().equals("Тель-Авив")
                        && name.getOrigin_name().equals("Владивосток")) {
                    timeDeparture = name.getDeparture_time();
                    timeArrival = name.getArrival_time();
                    timeX = timeArrival.minusHours(timeDeparture.getHour())
                            .minusMinutes(timeDeparture.getMinute());
                    if (timeX.isBefore(timeY)) {
                        timeY = timeX;
                    }

                }
            writer.write("Перевозчик: " + carrierSet + ", рейс Владивосток : Тель-Авив" + " минимальное время: " + timeY);
        }


        // добавляем в массив все цены рейса Владивосток - Тель-Авив
        int count = 0;
        for (NameOfFields price : ticketList.getTickets()) {
            if (price.getDestination_name().equals("Тель-Авив") && price.getOrigin_name().equals("Владивосток")) {
                priceList.add(price.getPrice());
                count++;
            }
        }
        Collections.sort(priceList);

        double priceSume = 0.0;
        for (Double z : priceList) {
            priceSume += z;
        }
        double priceAverage = priceSume / count;

        if (priceList.size() % 2 == 1) {
            writer.write("Разница между средней ценой и медианой для полета между городами  Владивосток и Тель-Авив "
                    + (priceAverage - priceList.get(count / 2)));
        } else {
            double x = priceList.get(count / 2);
            double y = priceList.get(count / 2 - 1);
            writer.write("Разница между средней ценой и медианой для полета между городами  Владивосток и Тель-Авив "
                    + (priceAverage - (x + y) / 2));

        }


        writer.close();
        reader.close();
    }


}
