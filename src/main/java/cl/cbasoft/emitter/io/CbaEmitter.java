package cl.cbasoft.emitter.io;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class CbaEmitter extends MqttClient {

	public String clientId;
	
	private CbaEmitter(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
		super(serverURI, clientId, persistence);
	}
	
	public static CbaEmitter Client(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
		return new CbaEmitter(serverURI, clientId, persistence);
	}
	
	public static CbaEmitter DefaultClient(String serverURI) throws MqttException {
		String clientId = "EMITTER_CBA_CLIENT_" + UUID.randomUUID().toString();
		return Client(serverURI, clientId, new MemoryPersistence());
	}
	
	@Override
	public void connect() throws MqttSecurityException, MqttException {
		MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setMaxReconnectDelay(30*1000);
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(30);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        super.connect(options);
	}
	
	public void connect(MqttConnectOptions options) throws MqttSecurityException, MqttException {
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        super.connect(options);
	}
	
	private String formatChannel(String key, String channel, Integer ttl, Integer last) {
		String formatted = key + "/" + channel + "?";
		formatted += ttl == null ? "" : "ttl=" + ttl + "&";
		formatted += last == null ? "" : "last=" + last;
		formatted = formatted.replaceAll("//", "/");
		if (formatted.endsWith("?"))
			formatted = formatted.substring(0, formatted.length() - 1);
		return formatted;
	}
	
	public void publish(String key, String channel, Integer ttl, byte[] payload) throws MqttPersistenceException, MqttException {
		String topic = this.formatChannel(key, channel, ttl, null);
		super.publish(topic, payload, 2, true);
	}
	
	public void subscribe(String key, String channel, Integer last) throws MqttPersistenceException, MqttException {
		String topic = this.formatChannel(key, channel, null, last);
		super.subscribe(topic);
	}
	
	public void presence(String key, String channel) throws MqttPersistenceException, MqttException {
		String topic = "emitter/presence/";
		String payload = "{\"key\":\"" + escapeJSON(key) + "\",\"channel\":\"" + escapeJSON(channel) + "\"}";
		super.publish(topic, payload.getBytes(), 2, true);
	}

	public void keygen(String key, String channel) throws MqttPersistenceException, MqttException {
		String topic = "emitter/keygen/";
		String payload = "{\"key\":\"" + escapeJSON(key) + "\",\"channel\":\"" + escapeJSON(channel) + "\"}";
		super.publish(topic, payload.getBytes(), 2, true);
	}
	
	private String escapeJSON(String json) {
		return json.replaceAll("\"", "\\\"");
	}
}
