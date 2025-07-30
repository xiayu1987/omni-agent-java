// package com.beraising.agent.omni.service;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.client.reactive.ReactorClientHttpConnector;
// import org.springframework.web.reactive.function.client.WebClient;

// import reactor.netty.http.HttpProtocol;
// import reactor.netty.http.client.HttpClient;

// @Configuration
// public class HttpClientConfig {

//     @Bean
//     public WebClient webClient() {
//         HttpClient httpClient = HttpClient.create()
//             .protocol(HttpProtocol.HTTP11);

//         return WebClient.builder()
//                 .clientConnector(new ReactorClientHttpConnector(httpClient))
//                 .build();
//     }
// }