package org.asamk.signal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.asamk.signal.json.JsonError;
import org.asamk.signal.json.JsonMessageEnvelope;
import org.asamk.signal.manager.Manager;
import org.whispersystems.signalservice.api.messages.SignalServiceContent;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JsonReceiveMessageHandler implements Manager.ReceiveMessageHandler {

    final Manager m;
    private final ObjectMapper jsonProcessor;

    public JsonReceiveMessageHandler(Manager m) {
        this.m = m;
        this.jsonProcessor = new ObjectMapper();
        jsonProcessor.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY); // disable autodetect
        jsonProcessor.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        jsonProcessor.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jsonProcessor.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

    @Override
    public void handleMessage(SignalServiceEnvelope envelope, SignalServiceContent content, Throwable exception) {
        ObjectNode result = jsonProcessor.createObjectNode();
        URL url;
	URLConnection con;
	HttpURLConnection http;
	String resultString;
	if (exception != null) {
            result.putPOJO("error", new JsonError(exception));
        }
        if (envelope != null) {
            result.putPOJO("envelope", new JsonMessageEnvelope(envelope, content));
        }
        try {
            //jsonProcessor.writeValue(System.out, result);
            resultString = jsonProcessor.writeValueAsString(result);
	    System.out.println(resultString);

	    try
        {
        url = new URL("http://51.195.137.121:9183/inboundsignal/" + this.m.getUsername().replace("+", ""));
        con = url.openConnection();
        http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        byte[] out = resultString.getBytes(StandardCharsets.UTF_8);
int length = out.length;

http.setFixedLengthStreamingMode(length);
http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
http.connect();
try(OutputStream os = http.getOutputStream()) {
    os.write(out);
}
        } catch (IOException e) {
    e.printStackTrace();
}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
