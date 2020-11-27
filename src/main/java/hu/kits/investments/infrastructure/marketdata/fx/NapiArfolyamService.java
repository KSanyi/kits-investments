package hu.kits.investments.infrastructure.marketdata.fx;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.kits.investments.domain.asset.Currency;
import hu.kits.investments.domain.marketdata.fx.FXRateWebService;
import hu.kits.investments.domain.marketdata.fx.FXRates.FXRate;

public class NapiArfolyamService implements FXRateWebService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private static final String URL = "http://api.napiarfolyam.hu/";
    private static List<String> CURRENCIES_CODES = Stream.of(Currency.values()).map(Currency::name).collect(toList());
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<FXRate> getDailyFXRates(LocalDate from, LocalDate to) {
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL + "?bank=mnb&datum=" + from.format(DATE_FORMAT) + "&datumend=" + to.format(DATE_FORMAT)))
                .build();
        String responseXMLString = executeHttpGet(httpRequest);

        List<FXRate> fxRates = parseFXRates(responseXMLString);
        logger.info("Daily fx rates: {}", fxRates);
        return fxRates;
    }
    
    private static String executeHttpGet(HttpRequest httpRequest) {
        
        HttpClient client = HttpClient.newHttpClient();
        
        logger.info("Sending HTTP GET: {}", httpRequest.uri());
        long start = System.currentTimeMillis();
        try {
            HttpResponse<String> httpResponse = client.send(httpRequest, BodyHandlers.ofString());
            long stop = System.currentTimeMillis();
            
            logger.info("Got response with {} bytes in {} millis", httpResponse.body().getBytes().length, (stop - start)); 
            
            return validateAndParseResponse(httpRequest, httpResponse);    
        } catch(Exception ex) {
            throw new RuntimeException("Error executing http request", ex);
        }
        
    }
    
    private static String validateAndParseResponse(HttpRequest httpRequest, HttpResponse<String> httpResponse) {
        if(httpResponse.statusCode() == 200) {
            logger.debug("HTTP response: SUCCESS");
            return httpResponse.body();
        } else {
            logger.warn("HTTP response code: {}, details: {}", httpResponse.statusCode(), httpResponse.body());
            throw new RestException(httpRequest.uri() + ": " + httpResponse.body());
        }
    }
    
    private static List<FXRate> parseFXRates(String xmlString) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try (var inputStream = new ByteArrayInputStream(xmlString.getBytes())) {
           
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            
            NodeList itemNodes = doc.getElementsByTagName("item");
            
            return IntStream.range(0, itemNodes.getLength())
                .mapToObj(itemNodes::item)
                .filter(itemNode -> itemNode.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .flatMap(element -> parseFXRate(element).stream())
                .collect(toList());
        } catch(Exception ex) {
            logger.error("Could not parse price from xml: {}", xmlString, ex);
        }
        return List.of();
    }
    
    private static Optional<FXRate> parseFXRate(Element element) {
        
        String currencyString = element.getElementsByTagName("penznem").item(0).getTextContent();
        if(CURRENCIES_CODES.contains(currencyString)) {
            Currency currency =  Currency.valueOf(currencyString);
            String rateString = element.getElementsByTagName("kozep").item(0).getTextContent();
            BigDecimal rate = new BigDecimal(rateString);
            String dateString = element.getElementsByTagName("datum").item(0).getTextContent();
            LocalDate date = LocalDate.parse(dateString, DATE_TIME_FORMAT);
            return Optional.of(new FXRate(date, currency, rate));
        } else {
            return Optional.empty();
        }
    }
    
    public static class RestException extends RuntimeException {
        
        public RestException(String message) {
            super(message);
        }
         
    }
    
}
