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
