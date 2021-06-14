package com.miya10kei.actuator.jfr.configuration

import com.miya10kei.actuator.jfr.FlightRecorderEndpoint
import com.miya10kei.actuator.jfr.domain.FlightRecorder
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailableEndpoint(endpoint = FlightRecorderEndpoint::class)
@ConditionalOnWebApplication(type = SERVLET)
@EnableConfigurationProperties(value = [FlightRecorderEndpointProperties::class])
class FlightRecorderEndpointAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun flightRecorder(): FlightRecorder {
    return FlightRecorder()
  }

  @Bean
  @ConditionalOnMissingBean
  fun flightRecorderEndpoint(flightRecorder: FlightRecorder): FlightRecorderEndpoint {
    return FlightRecorderEndpoint(flightRecorder)
  }
}
