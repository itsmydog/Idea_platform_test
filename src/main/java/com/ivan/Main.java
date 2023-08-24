package com.ivan;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        LocalTime timeDeparture = null;
        LocalTime timeArrival = null;
        LocalTime timeX = null;
        List<Double> priceList = new ArrayList();
        LocalTime timeY = LocalTime.parse("23:59");
        File file = new File("/Users/user/Desktop/test_idea/tickets.json");
        File fileWriter = new File("/Users/user/Desktop/test_idea/tickets.txt");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        TicketList ticketList = gson.fromJson(reader, TicketList.class);

        // Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика
        for (NameOfFields name : ticketList.getTickets()) {
            if (name.getDestination_name().equals("Тель-Авив") && name.getOrigin_name().equals("Владивосток")) {
                timeDeparture = name.getDeparture_time();
                timeArrival = name.getArrival_time();
                timeX = timeArrival.minusHours(timeDeparture.getHour())
                        .minusMinutes(timeDeparture.getMinute());
                if (timeX.isBefore(timeY)) {
                    timeY = timeX;
                }
            }
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
        for (Double priceAverage : priceList) {
            priceSume += priceAverage;
        }
        double mediana = priceList.get(count / 2);

        String answer = "Минимальное время полета между городами Владивосток и Тель-Авив: " + timeY + "\n" +
                "Разницу между средней ценой и медианой для полета между городами Владивосток и Тель-Авив: " + (priceSume / count - mediana);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileWriter, StandardCharsets.UTF_8));
        writer.write(answer);

        writer.close();
        reader.close();
    }


}
