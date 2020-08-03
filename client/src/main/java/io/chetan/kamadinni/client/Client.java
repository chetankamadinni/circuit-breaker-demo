package io.chetan.kamadinni.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
public class Client {

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/hi")
	@HystrixCommand(fallbackMethod = "fallbackHi", threadPoolKey = "hiServicePool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "20"),
			@HystrixProperty(name = "maxQueueSize", value = "10") }, commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") })
	public String hi() {
		ResponseEntity<String> exchange = restTemplate.exchange("http://service/hello", HttpMethod.GET, null,
				String.class);
		return exchange.getBody();
	}

	public String fallbackHi() {
		return "Service is not responding! Please try after sometime.";
	}

}
