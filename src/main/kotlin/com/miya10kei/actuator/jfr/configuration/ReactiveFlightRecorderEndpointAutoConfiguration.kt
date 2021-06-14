package com.miya10kei.actuator.jfr.configuration

import com.miya10kei.actuator.jfr.ReactiveFlightRecorderEndpoint
import com.miya10kei.actuator.jfr.domain.FlightRecorder
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailableEndpoint(endpoint = ReactiveFlightRecorderEndpoint::class)
@ConditionalOnWebApplication(type = REACTIVE)
@EnableConfigurationProperties(value = [FlightRecorderEndpointProperties::class])
class ReactiveFlightRecorderEndpointAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun flightRecorder(): FlightRecorder {
    return FlightRecorder()
  }

  @Bean
  @ConditionalOnMissingBean
  fun reactiveFlightRecorderEndpoint(flightRecorder: FlightRecorder): ReactiveFlightRecorderEndpoint {
    return ReactiveFlightRecorderEndpoint(flightRecorder)
  }
}
