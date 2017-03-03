package io.telenor.bustripper;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Searches for bus stops in area provided.
 */
public class FindBusStop implements Runnable {


    private static final String SEARCH_URL = "http://reisapi.ruter.no/Place/GetPlaces/";

    private String searchTerm;

    private Client client;

    private TripsCallback listener;

    public FindBusStop(TripsCallback callback, String searchTerm) {
        this.listener = callback;
        this.searchTerm = searchTerm;
    }

    public void run() {
        ClientConfig configuration = new ClientConfig();

        client = ClientBuilder.newClient(configuration);
        
        String safeSearchTerm = searchTerm;

        try{
            safeSearchTerm  = URLEncoder.encode(searchTerm, "UTF-8").
                    replaceAll("\\+", "%20").
                    replaceAll("\\.", "");
        } catch (UnsupportedEncodingException ex){
            Logger.getLogger(FindBusStop.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        Invocation.Builder invocationBuilder = client
            .target(SEARCH_URL + safeSearchTerm)
            .request(MediaType.APPLICATION_JSON);

        final AsyncInvoker asyncInvoker = invocationBuilder.async();
        BusStopsCallBack callback = new BusStopsCallBack(listener);
        asyncInvoker.get(callback);
    }
}
