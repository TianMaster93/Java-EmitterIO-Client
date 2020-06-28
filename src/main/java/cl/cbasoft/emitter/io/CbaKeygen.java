package cl.cbasoft.emitter.io;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CbaKeygen {
	
	private String url;
	private String key;
	private String channel;
	private int ttl;
	private boolean sub;
	private boolean pub;
	private boolean store;
	private boolean load;
	private boolean presence;
	private boolean extend;
	
	public static CbaKeygen builder(String url) {
		return new CbaKeygen(url);
	}
	
	private CbaKeygen(String url) {
		this.url = url;
	}
	
	public CbaKeygen key(String key) {
		this.key = key;
		return this;
	}
	
	public CbaKeygen channel(String channel) {
		this.channel = channel;
		return this;
	}
	
	public CbaKeygen ttl(Integer ttl) {
		this.ttl = ttl;
		return this;
	}
	
	public CbaKeygen sub(boolean sub) {
		this.sub = sub;
		return this;
	}
	
	public CbaKeygen pub(boolean pub) {
		this.pub = pub;
		return this;
	}
	
	public CbaKeygen store(boolean store) {
		this.store = store;
		return this;
	}
	
	public CbaKeygen load(boolean load) {
		this.load = load;
		return this;
	}
	
	public CbaKeygen presence(boolean presence) {
		this.presence = presence;
		return this;
	}
	
	public CbaKeygen extend(boolean extend) {
		this.extend = extend;
		return this;
	}

	public String build() throws IOException {
		Document response = Jsoup
			.connect(url)
			.data("key", key)
			.data("channel", channel)
			.data("ttl", ttl + "")
			.data("sub", sub ? "on" : "off")
			.data("pub", pub ? "on" : "off")
			.data("store"	, store 	? "on" : "off")
			.data("load"	, load 		? "on" : "off")
			.data("presence", presence 	? "on" : "off")
			.data("extend"	, extend 	? "on" : "off")
			.post();
		Element keygenResponse = response.getElementById("keygenResponse");
		if (keygenResponse == null) {
			throw new CbaKeygenException("INVALID REQUEST");
		}
		String result = keygenResponse.text();
		if (!result.startsWith("channel:")) {
			throw new CbaKeygenException(result);
		}
		String params[] = result.split(":");
		return params[2].trim();
	}
}
