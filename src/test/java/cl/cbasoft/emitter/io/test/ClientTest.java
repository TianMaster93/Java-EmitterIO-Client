package cl.cbasoft.emitter.io.test;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import cl.cbasoft.emitter.io.CbaEmitter;
import cl.cbasoft.emitter.io.CbaKeygen;

public class ClientTest {

	private static final String SERVER_URI 	= "ws://localhost:30000";
	private static final String KEYGEN_URI 	= "http://localhost:30000/keygen";
	private static final String MASTER_KEY 	= "HNjKruDBTCcYBuv_fv6e1ciiVmNeuLFU"; //ONLY FOR TEST
	
	public static void main(String[] args) throws MqttSecurityException, MqttException, IOException, URISyntaxException {
		String channel 	  = "channel-test/";
		String keyChannel = CbaKeygen
				.builder(KEYGEN_URI)
				.key(MASTER_KEY)
				.channel(channel)
				.pub(true)
				.sub(true)
				.build();
		
		System.out.println("KEYCHANNEL => " + keyChannel);
		
		CbaEmitter client = CbaEmitter.DefaultClient(SERVER_URI);
		client.setCallback(new MqttCallback() {
			
			@Override
			public void messageArrived(String channel, MqttMessage message) throws Exception {
				System.out.println("messageArrived");
				System.out.println("Channel: " + channel);
				System.out.println("Message: " + new String(message.getPayload()));
			}
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				System.out.println("deliveryComplete");
			}
			
			@Override
			public void connectionLost(Throwable cause) {
				System.err.println("connectionLost");
			}
		});
		
		client.connect();
		client.subscribe(keyChannel, channel, null);
		client.publish(keyChannel, channel, null, "HELLO EMITTER :D".getBytes());
		System.out.println("I AM READY");
//		client.close();
	}

}
