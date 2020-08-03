# circuit-breaker-demo
Demo of circuit breaker pattern in microservices

Circuit breaker pattern is a way to handle the request load when the microservices are not responding or is down completely. Here the client can either timeout the requests or handle in a way where the server is not overloaded with the requests. Here client can do either of the following
- throw an exception
- provide a dummy response
- respond with values from cache

We are going to use Netflix-Hystrix to do the circuit-breaker implementation.

> This implemenation is an extension over the service-discovery implementation. Please refer to [this](https://github.com/chetankamadinni/service-discovery-demo) repo for service-discovery related details.

### Steps
- Create a spring-boot app with the below dependency
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
	<version>2.2.2.RELEASE</version>
</dependency>
```
- Add `@EnableCircuitBreaker` annotation to your application class.

- Add `@HystrixCommand` annotation to your method where you want to implement the circuit breaker. Provide a fallback method which will return the response whenever circuit breaks.
```java
@GetMapping("/hi")
@HystrixCommand(fallbackMethod = "fallbackHi")
public String hi() {
	ResponseEntity<String> exchange = restTemplate.exchange("http://service/hello", HttpMethod.GET, null, String.class);
	return exchange.getBody();
}
public String fallbackHi() {
	return "Service is not responding! Please try after sometime.";
}
  ```
### Decide on when to break a circuit
When does the circuit should break? and when should the calls be sent again? We can configure this by taking into consideration of different parameters as listed below
- Timeout a request after p seconds
- Consider for last n request
- m out of n requests are failed
- After t seconds(sleep window) the client should agian start sending the requests
```java
commandProperties = {
	@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
	@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
	@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
	@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") }
```
The above example considers a request timeout of 2secs. If among the last 5 requests, 50% of requests are failed to respond then circuit is broken. The client will again start sending the request after 5 secs(sleep window). You can test this behavior by bringing down the service and making the calls to client application.

### Configure Hystrix Dashboard
To monitor all these requests, hystrix provides a dashboard. To configure the dashboard add the below dependency
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
	<version>2.2.2.RELEASE</version>
</dependency>
```
Make sure your pom has actuator dependency as well. Hystrix dashboard uses actuator
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
Add `@EnableHystrixDashboard` annotation to the application class.

Add `management.endpoints.web.exposure.include=*` to the application.properties file.

To access the dashboard hit the app end point with /hystrix. http://localhost:8083/hystrix. As we just using a single hystrix app and not in a cluster.
Copy the URL http://localhost:8083/actuator/hystrix.stream to the textbox and hit Monitor Stream button. This will give the api call details.

